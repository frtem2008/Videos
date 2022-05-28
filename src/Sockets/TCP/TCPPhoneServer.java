package Sockets.TCP;

import java.io.IOException;
import java.net.ServerSocket;

public class TCPPhoneServer {
    private static final int PORT = 26780;

    public static void main(String[] args) {
        try {
            String data;
            ServerSocket waiter = new ServerSocket(PORT);
            System.out.println("Started a server on port: " + PORT);

            TCPPhone client = new TCPPhone(waiter);
            System.out.println("New client connected with ip address: " + client.getIp());

            do {
                System.out.println("Waiting for data from client");
                data = client.readLine();
                System.out.println("Received data from client: " + data);

                System.out.println("Sending data to client");
                client.writeLine(data + "!!!");
                System.out.println("Data has been sent");
            } while (!data.equals("Stop"));
            System.out.println("Closing all sockets");
            client.close();
            waiter.close();
        } catch (IOException e) {
            System.out.println("Failed to start a server");
            e.printStackTrace();
        }
    }
}
