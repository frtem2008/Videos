package Sockets.TCP;

import java.io.IOException;
import java.net.ServerSocket;

public class TCPPhoneMultiThreadedServer {
    private static final int PORT = 26780;

    public static void main(String[] args) {
        try (ServerSocket server = new ServerSocket(PORT)) {
            System.out.println("Started a server on port: " + PORT);
            System.out.println("Waiting for clients to connect");

            while (true) {
                TCPPhone client = new TCPPhone(server);
                new Thread(() -> {
                    String data;
                    System.out.println("New client connected with ip: " + client.getIp());

                    do {
                        System.out.println("Reading data from client");
                        data = client.readLine();
                        System.out.println("Data from client: " + data);
                        System.out.println("Sending data to client");
                        client.writeLine(data + "!!!");
                        System.out.println("Data sent");
                    } while (!data.equals("Stop"));

                    try {
                        System.out.println("Closing the socket...");
                        client.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                        System.out.println("Failed to close the socket");
                    }
                }).start();
            }
        } catch (IOException e) {
            System.out.println("Unable to start a server");
            e.printStackTrace();
        }
    }
}
