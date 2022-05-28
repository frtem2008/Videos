package Sockets.TCP;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPBasicServer {
    private static final int PORT = 26780;

    public static void main(String[] args) {
        BufferedWriter writer;
        BufferedReader reader;
        String data;

        try {
            ServerSocket waiter = new ServerSocket(PORT);
            System.out.println("Server started on port: " + PORT);
            System.out.println("Waiting for clients to connect...");
            System.out.println();

            Socket connection = waiter.accept();
            System.out.println("Client connected with ip: " + connection.getInetAddress().getHostAddress());

            writer = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            do {
                System.out.println("Waiting for data from a client...");

                data = reader.readLine() + "!!!";
                System.out.println("Data received from client: " + data);

                writer.write(data);
                writer.newLine();
                writer.flush();
                System.out.println("Sent data to client");
                System.out.println();
            } while (!data.equals("Stop"));

            System.out.println("Closing streams and shutting down...");
            writer.close();
            reader.close();
            connection.close();
            waiter.close();
        } catch (IOException e) {
            System.out.println("Unable to start a server:");
            e.printStackTrace();
        }
    }
}
