package monitor.client;

import util.DateUtil;
import util.HostUtil;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

import static constants.Constants.*;

/**
 * Client that queries the Metrics server at port 4567
 */
public class MetricsClient {

    //IO Streams
    private static DataInputStream serverInput;
    private static DataOutputStream serverOutput;
    private static DateUtil dateUtil;

    //Scanner input
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        try {
            String host = HostUtil.getHost();
            Socket server = new Socket(host, METRICS_SERVER_PORT);
            //contains client output from server
            serverInput = new DataInputStream(server.getInputStream());
            //contains client input to server
            serverOutput = new DataOutputStream(server.getOutputStream());
            dateUtil = new DateUtil();

            performQuery();
        } catch (IOException e) {
            System.err.println("ERROR: Could not Connect to Server.");
            e.printStackTrace();
        }
    }

    /**
     * Takes two inputs from user
     *  - fromDate
     *  - toDate
     *
     * Validate the input dates
     * Queries the server with input
     * Print results if success, print failure if error
     *
     * @throws IOException
     */
    private static void performQuery() throws IOException {
        while (true) {
            String host = HostUtil.getHost();

            System.out.println("Host queried : " + host);

            System.out.println("Enter FROM date-time with pattern: " + DATE_TIME_PATTERN);
            String fromDateTime = scanner.nextLine();

            System.out.println("Enter TO date-time with pattern: " + DATE_TIME_PATTERN);
            String toDateTime = scanner.nextLine();

            if(!isValid(fromDateTime, toDateTime)){
                System.err.println("Error in given date-time, please retry!");
                continue;
            }

            System.out.println("Query FROM date-time: " + fromDateTime);
            System.out.println("Query TO date-time: " + toDateTime);

            queryServer(host, fromDateTime, toDateTime);

            System.out.println("Do you want to query again? Yes|No: ");
            String response = scanner.nextLine();
            if(response.equalsIgnoreCase("yes")){
                continue;
            }
            else{
                System.out.println("Good Bye!");
                System.exit(0);
            }
        }
    }

    /**
     * Calls the server with query that contains host, fromDate and toDate info
     * and prints the results if success results, else system err outs "Query to server failed"
     *
     * @param host host info
     * @param fromDate fromDate from query
     * @param toDate toDate from query
     * @throws IOException error
     */
    private static void queryServer(String host, String fromDate, String toDate) throws IOException {
        //write query request
        serverOutput.writeUTF(host);
        serverOutput.writeUTF(fromDate);
        serverOutput.writeUTF(toDate);
        serverOutput.flush();

        //read response from server
        String result = serverInput.readUTF();
        if (result.equals(ERROR_RESPONSE)) {
            System.err.println("Query to server FAILED.");
        } else if (result.equals(SUCCESS_RESPONSE)) {
            System.out.println("Query Success.");
            printMetrics();
        }
    }

    /**
     * Checks the following -
     * 1. If fromDate and toDate are in DATE_TIME_PATTERN
     * 2. If fromDate is before afterDate
     * 3. If fromDate is within the 24 hour window
     *
     * @param fromDate fromDate to be validated
     * @param toDate toDate to be validated
     * @return true if valid else false
     */
    private static boolean isValid(String fromDate, String toDate) {
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_TIME_PATTERN);
        Date fromDateTime = null;
        try {
            fromDateTime = sdf.parse(fromDate);
        } catch (ParseException e) {
            e.printStackTrace();
            System.err.println("INVALID FROM date-time pattern: " + fromDate);
            return false;
        }
        Date toDateTime = null;
        try {
            toDateTime = sdf.parse(toDate);
        } catch (ParseException e) {
            e.printStackTrace();
            System.err.println("INVALID TO date-time pattern: " + toDate);
            return false;
        }
        Timestamp fromTimestamp = new Timestamp(fromDateTime.getTime());
        Timestamp toTimestamp = new Timestamp(toDateTime.getTime());
        Timestamp priorDateTime = new Timestamp(dateUtil.get24HourPriorDateTime().getTime());


        if(fromTimestamp.after(toTimestamp)) {
            System.err.println("FROM cannot be after TO date-time");
            return false;
        }else if(fromTimestamp.before(priorDateTime)){
            System.err.println("FROM cannot be before 24 hours from now");
            return false;
        }
        return true;
    }

    /**
     * Calls the toString() method the Metrics object and prints rows
     *
     * @throws IOException if error in readUTF()
     */
    private static void printMetrics() throws IOException {
        System.out.println("--------------------- METRICS.");

        while (true) {
            String info = serverInput.readUTF();
            if (info.equals(END_OF_RESPONSE)) {
                System.out.println("---------------------END OF METRICS.");
                break;
            }
            System.out.println(info);
        }
    }
}
