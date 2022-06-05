package monitor;

import util.HostUtil;

public class MetricsMonitorMain {

    public static void main(String[] args) {
        String host = HostUtil.getHost();

        System.out.println("Host : " + host);

        Thread thread = new Thread(new MetricsWorker(host));
        thread.start();
    }
}