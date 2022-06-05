package monitor.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import static constants.Constants.METRICS_SERVER_PORT;

/**
 * Server instance that listens for any client connections and calls the client handler when client queries
 */
public class MetricsServer {

    private static int activeClientCount;

    public static void main(String[] args) {
        try {
            ServerSocket ss = new ServerSocket(METRICS_SERVER_PORT);
            System.out.println("Server Started.");
            while (true) {
                System.out.println("Awaiting client...");
                Socket clientConnection = ss.accept();
                activeClientCount++;
                System.out.println(activeClientCount + " - Client Connected : " + clientConnection.getInetAddress());
                clientConnectionHandler(clientConnection);
            }
        } catch (IOException e) {
            System.err.println("Error with running server or connecting with client");
            e.printStackTrace();
        }
    }

    /**
     * Handler method that gets called when a client tries to connect to server
     */
    private static void clientConnectionHandler(Socket clientConnection) {
        System.out.println("Handling client: " + clientConnection.getInetAddress());
        try {
            Thread thread = new Thread(new ClientConnectionHandler(clientConnection));
            thread.start();
        } catch (IOException e) {
            System.err.println("ERROR Handling client: " + clientConnection.getInetAddress());
            e.printStackTrace();
        }

    }

    /**
     * Reduces active clientCount after disconnection
     */
    public static void clientDisconnected() {
        activeClientCount--;
        System.out.println("Active clients count: " + activeClientCount);
    }
}
