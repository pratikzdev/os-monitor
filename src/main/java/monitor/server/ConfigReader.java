package monitor.server;

import model.ConfigProperties;
import monitor.server.db.Database;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static constants.Constants.INTERVAL_MILLISECONDS;

/**
 * Class that reads external configuration
 */
public class ConfigReader {

    private static ConfigProperties configProperties;

    public static ConfigProperties getConfigs(){
        if(configProperties == null){
            readConfigProperties();
        }
        return configProperties;
    }

    private static void readConfigProperties(){
        try (InputStream input = Database.class.getClassLoader().getResourceAsStream("config.properties")) {

            Properties properties = new Properties();

            if (input == null) {
                System.err.println("Unable to find config.properties");
                return;
            }

            properties.load(input);

            String url = properties.getProperty("db.url");
            String username = properties.getProperty("db.user");
            String password = properties.getProperty("db.password");
            Long interval = null;
            try {
                interval = Long.valueOf(properties.getProperty("interval"));
            }catch (NumberFormatException ne){
                System.err.println("Incorrect value for interval in configs, exiting...");
                System.exit(-1);
            }

            configProperties = new ConfigProperties();
            configProperties.setUrl(url);
            configProperties.setUsername(username);
            configProperties.setPassword(password);
            configProperties.setInterval(interval);

        } catch (IOException ex) {
            System.err.println("Error in loading config properties");
            ex.printStackTrace();
        }
    }
}
