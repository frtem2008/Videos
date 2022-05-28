package Sockets.TCP;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class TCPBasicClient {
    private static final String ADDRESS = "127.0.0.1";
    private static final int PORT = 26780;

    public static void main(String[] args) {
        Socket client;

        BufferedWriter writer;
        BufferedReader reader;

        Scanner stdin = new Scanner(System.in);

        try {
            client = new Socket(ADDRESS, PORT);
            System.out.println("Connected to server with ip: " + ADDRESS + " on port " + PORT);
            writer = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
            reader = new BufferedReader(new InputStreamReader(client.getInputStream()));

            String data;
            do {
                System.out.println("Input data to send: ");
                data = stdin.nextLine();
                writer.write(data);
                writer.newLine();
                writer.flush();

                System.out.println("Data sent to server");
                System.out.println("Waiting for data from server...");

                System.out.println("Data received: " + reader.readLine());
                System.out.println();
            } while (!data.equals("Stop"));

            System.out.println("Closing streams and shutting down");
            writer.close();
            reader.close();
            client.close();
        } catch (IOException e) {
            System.out.println("Failed to connect to a server");
            e.printStackTrace();
        }
    }
}
