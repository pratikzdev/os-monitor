package constants;

public final class Constants {
    //30 Seconds
    public static final long INTERVAL_MILLISECONDS = 30 * 1000;

    //Number of Bytes in GB
    public static final long BYTES_IN_GB = 1073741824;

    //fromDateTime and toDateTime Pattern
    public static final String DATE_TIME_PATTERN = "dd MM yyyy HH mm ss";

    // SUCCESS RESPONSE
    public static final String SUCCESS_RESPONSE = "SUCCESS";

    // ERROR RESPONSE
    public static final String ERROR_RESPONSE = "ERROR";

    // END OF RESPONSE
    public static final String END_OF_RESPONSE = "END";

    // METRICS SERVER PORT
    public static final int METRICS_SERVER_PORT = 4567;

    //Database columns
    public static final String HOST = "host";
    public static final String DATE_TIME = "date_time";
    public static final String CPU_PERCENT = "cpu_percent";
    public static final String RAM_USAGE = "ram_usage";
    public static final String DISK_USAGE = "disk_usage";

}
