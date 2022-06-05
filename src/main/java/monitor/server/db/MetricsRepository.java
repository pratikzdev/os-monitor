package monitor.server.db;

import model.Metrics;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

public interface MetricsRepository {

    void save(Metrics metrics) throws SQLException;

    List<Metrics> getMetricsByHostFromToTimeStamp(String host, Timestamp fromTimestamp, Timestamp toTimestamp) throws SQLException;

    List<Metrics> getMetricsByHostForPast24Hours(String host) throws SQLException;

}