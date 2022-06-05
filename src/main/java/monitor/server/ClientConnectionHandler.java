package monitor.server;

import model.Metrics;
import monitor.server.db.MetricsRepository;
import monitor.server.db.MetricsRepositoryImpl;
import util.DateUtil;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import static constants.Constants.*;

/**
 * Client Connection Handler class that gets executed when clients connect to metrics server
 */
public class ClientConnectionHandler implements Runnable {

    private DataOutputStream clientOutput;
    private DataInputStream clientInput;
    private Socket clientConnection;
    private MetricsRepository metricsRepository;
    private DateUtil dateUtil;

    /*
     throw Exception so that we don't continue if I/O channel could not be opened.
     */
    public ClientConnectionHandler(Socket clientConnection) throws IOException {
        this.clientConnection = clientConnection;
        //Use DataInputStream and DataOutputStream as wrappers over I/O streams
        this.clientInput = new DataInputStream(clientConnection.getInputStream());
        this.clientOutput = new DataOutputStream(clientConnection.getOutputStream());
        this.dateUtil = new DateUtil();

        this.metricsRepository = new MetricsRepositoryImpl();
    }

    @Override
    public void run() {
        while (!clientConnection.isClosed()) {
            try {
                //read query from client
                String host = clientInput.readUTF();
                String fromDate = clientInput.readUTF();
                String toDate = clientInput.readUTF();
                //query against db
                List<Metrics> metricsList = queryMetrics(host, fromDate, toDate);
                if (metricsList == null) {
                    //Write ERROR response
                    clientOutput.writeUTF(ERROR_RESPONSE);
                } else {
                    //Write SUCCESS response
                    clientOutput.writeUTF(SUCCESS_RESPONSE);
                    sendMetricsToClient(metricsList);
                }
                //flush the final output
                clientOutput.flush();
            } catch (IOException e) {
                e.printStackTrace();
                System.err.println("Error communicating with client. Connection Closed.");
                break;
            }
        }
        MetricsServer.clientDisconnected();
    }

    /**
     *  Queries the metrics table by host, fromDate and toDate
     */
    private List<Metrics> queryMetrics(String host, String fromDate, String toDate) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(DATE_TIME_PATTERN);
            Timestamp fromTimestamp = dateUtil.convertDateToTimeStamp(sdf.parse(fromDate));
            Timestamp toTimestamp = dateUtil.convertDateToTimeStamp(sdf.parse(toDate));

            return metricsRepository.getMetricsByHostFromToTimeStamp(host, fromTimestamp, toTimestamp);
        } catch (SQLException e) {
            System.err.println("Error in running SQL");
            e.printStackTrace();
            return null;
        } catch (ParseException e){
            System.err.println("Error in Parsing the fromDate or toDate");
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Sends metrics to clients via the outputStream
     */
    private void sendMetricsToClient(List<Metrics> metricsList) throws IOException {
        for (Metrics metrics : metricsList) {
            clientOutput.writeUTF(metrics.toString());
        }
        clientOutput.writeUTF(END_OF_RESPONSE);
    }


}
