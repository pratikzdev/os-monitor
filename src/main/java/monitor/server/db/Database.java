package monitor.server.db;

import model.ConfigProperties;
import monitor.server.ConfigReader;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Database connection manager class
 */
public class Database {

    private static Connection connection;
    private static ConfigProperties configProperties;

    public static Connection getConnection(){
        if(connection == null){
            configProperties = ConfigReader.getConfigs();
            createConnection();
        }
        return connection;
    }

    private static void createConnection() {
        try {
            connection = DriverManager.getConnection(configProperties.getUrl(),
                    configProperties.getUsername(), configProperties.getPassword());
            System.out.println("Connected to Database.");
        } catch (SQLException e) {
            System.err.println("Error connecting to Database. " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        getConnection();
    }
}
