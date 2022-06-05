package util;

import java.net.Inet4Address;
import java.net.UnknownHostException;

public class HostUtil {

    public static String getHost(){
        String host = "";
        try {
            host = Inet4Address.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            System.err.println("Error in getting host");
            host = "localhost";
        }
        return host;
    }
}
