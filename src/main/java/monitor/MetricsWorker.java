package monitor;

import com.sun.management.OperatingSystemMXBean;
import model.ConfigProperties;
import model.Metrics;
import monitor.server.ConfigReader;
import monitor.server.db.MetricsRepository;
import monitor.server.db.MetricsRepositoryImpl;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static constants.Constants.BYTES_IN_GB;

/**
 * Worker does the following -
 * - Saves the current metrics
 * - Queries the metrics for last 24 hours by host
 */
public class MetricsWorker implements Runnable {

    private final String host;
    private final MetricsRepository metricsRepository;
    private final ConfigProperties configProperties;

    public MetricsWorker(String host) {
        this.host = host;
        this.metricsRepository = new MetricsRepositoryImpl();
        this.configProperties = ConfigReader.getConfigs();

    }


    @Override
    public void run() {
        while (true) {
            System.out.println("----------Resource Metrics at: " + new Date());

            Date dateTime = new Date();
            double cpuPercent = cpuUsage();
            long ram = ramUsage();
            String diskUsage = diskUsage();

            persistMetrics(dateTime, cpuPercent, ram, diskUsage);
            queryMetricsForLast24Hours();

            //delay 30 seconds
            try {
                Thread.sleep(configProperties.getInterval());
            } catch (InterruptedException e) {
                System.err.println("Sleep thread interrupted");
                e.printStackTrace();
            }
        }
    }

    /**
     *  Save the metrics monitor at this moment
     */
    private void persistMetrics(Date dateTime, double cpuPercent, long ram, String fileUsage) {
        Metrics metrics = new Metrics(host, dateTime);
        metrics.setCpuPercent((float) cpuPercent);
        metrics.setRamUsage(ram);
        metrics.setDiskUsage(fileUsage);

        try {
            metricsRepository.save(metrics);
            System.out.println("Metrics saved to DB.");
        } catch (SQLException e) {
            System.err.println("COULD NOT SAVE Metrics to DB.");
            e.printStackTrace();
        }
    }

    /**
     * Queries the database by hostname for last 24 hours
     */
    private void queryMetricsForLast24Hours(){
        List<Metrics> metricsList = new ArrayList<>();
        System.out.println("----------Last 24 hour metrics for host : " + host);
        try {
            metricsList =  metricsRepository.getMetricsByHostForPast24Hours(host);
        } catch (SQLException ex) {
            System.err.println("Error query the ");
            ex.printStackTrace();
        }
        metricsList.forEach(System.out::println);
        System.out.println("Total " + metricsList.size() +" records matching");

        System.out.println("----------Last 24 hour metrics end");
    }

    /**
     * Gets CPU usage at that moment
     *
     * @return CPU usage at that moment
     */
    private static double cpuUsage() {
        OperatingSystemMXBean osMBean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        osMBean.getProcessCpuLoad();
        double percent = (osMBean.getSystemCpuLoad() * 100);
        System.out.println("CPU Percent : "+ percent + "%");
        return percent;
    }

    /**
     * Gets the ram usage at that moment
     *
     * @return ram usage at that moment
     */
    private static long ramUsage() {
        OperatingSystemMXBean osMBean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();

        long ramS1 = osMBean.getTotalPhysicalMemorySize() - osMBean.getFreePhysicalMemorySize();
        long percent = (100 * ramS1) / osMBean.getTotalPhysicalMemorySize();
        System.out.println("Ram Usage: " + percent);
        return percent;
    }

    /**
     * Gets the disk usage for each root
     * outputs it like 'C:\ Total 104 Usable 1 | D:\ Total 931 Usable 901 |"
     *
     * @return diskusage at that moment
     */
    private static String diskUsage() {
        File[] roots = File.listRoots();

        StringBuilder sb = new StringBuilder();
        for (File root : roots) {

            long totalS = root.getTotalSpace() / BYTES_IN_GB;  //changing Bytes into GigBytes #1073741824
            long usableS = root.getUsableSpace() / BYTES_IN_GB;

            sb.append(root.getPath())
                    .append(" Total ")
                    .append(totalS)
                    .append(" Usable ")
                    .append(usableS);
            sb.append(" | ");
        }
        System.out.println("Disk Usage: " + sb.toString());
        return sb.toString();
    }
}
