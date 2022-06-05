package monitor.server.db;

import model.ConfigProperties;
import model.Metrics;
import monitor.server.ConfigReader;
import util.DateUtil;

import java.sql.*;
import java.util.Date;
import java.util.*;

import static constants.Constants.*;

public class MetricsRepositoryImpl implements MetricsRepository {

    private Connection connection;
    private DateUtil dateUtil;

    public MetricsRepositoryImpl() {
        connection = Database.getConnection();
        dateUtil = new DateUtil();
    }

    /**
     * Saves the metrics to db
     */
    public void save(Metrics metrics) throws SQLException {
        String sql = "INSERT INTO metrics" +
                " (host, date_time , cpu_percent , ram_usage, disk_usage)" +
                " VALUES (?,?,?,?,?)";
        try(PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, metrics.getHost());
            Timestamp timeStamp = dateUtil.convertDateToTimeStamp(metrics.getDateTime());
            ps.setTimestamp(2, timeStamp);
            ps.setFloat(3, metrics.getCpuPercent());
            ps.setFloat(4, metrics.getRamUsage());
            ps.setString(5, metrics.getDiskUsage());

            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error in persisting metric into database");
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * Queries metrics by hostname, fromTimestamp and toTimeStamp
     */
    public synchronized List<Metrics> getMetricsByHostFromToTimeStamp(String host, Timestamp fromTimeStamp, Timestamp toTimeStamp)
            throws SQLException {
        List<Metrics> metricsList = new ArrayList<>();

        String sql = "SELECT host, date_time , cpu_percent , ram_usage, disk_usage" +
                " FROM  metrics" +
                " WHERE host = ?" +
                " AND date_time >= ?" +
                " AND date_time <= ?" +
                " ORDER BY date_time DESC";

        try(PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, host);
            ps.setTimestamp(2, fromTimeStamp);
            ps.setTimestamp(3, toTimeStamp);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Timestamp dateTime = rs.getTimestamp(DATE_TIME);
                Metrics metrics = new Metrics(host, dateTime);
                metrics.setCpuPercent(rs.getFloat(CPU_PERCENT));
                metrics.setRamUsage(rs.getFloat(RAM_USAGE));
                metrics.setDiskUsage(rs.getString(DISK_USAGE));

                metricsList.add(metrics);
            }

        }catch (SQLException ex){
            System.err.println("Error in querying metrics from the database");
            ex.printStackTrace();
            throw ex;
        }
        return metricsList;
    }

    /**
     * Queries metrics by host for past 24 hours
     * max records - 2880 for host assuming 30sec period
     */
    public List<Metrics> getMetricsByHostForPast24Hours(String host) throws SQLException {
        List<Metrics> metricsList = new ArrayList<>();

        String sql = "SELECT host, date_time , cpu_percent , ram_usage, disk_usage" +
                " FROM  metrics" +
                " WHERE host = ?" +
                " AND date_time >= ?" +
                " ORDER BY date_time DESC";

        Date dateTime24HoursBefore = dateUtil.get24HourPriorDateTime();

        try(PreparedStatement ps = connection.prepareStatement(sql)){
            ps.setString(1, host);
            ps.setTimestamp(2, dateUtil.convertDateToTimeStamp(dateTime24HoursBefore));

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Timestamp dateTime = rs.getTimestamp(DATE_TIME);
                Metrics metrics = new Metrics(host, dateTime);
                metrics.setCpuPercent(rs.getFloat(CPU_PERCENT));
                metrics.setRamUsage(rs.getFloat(RAM_USAGE));
                metrics.setDiskUsage(rs.getString(DISK_USAGE));

                metricsList.add(metrics);
            }
        }catch (SQLException e){
            System.err.println("Error in querying all rows from the database");
            e.printStackTrace();
            throw e;
        }

        return metricsList;
    }
}
