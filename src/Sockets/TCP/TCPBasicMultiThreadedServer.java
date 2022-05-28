package Sockets.TCP;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPBasicMultiThreadedServer {
    private static final int PORT = 26780;

    public static void main(String[] args) {
        try {
            ServerSocket waiter = new ServerSocket(PORT);
            System.out.println("Server started on port: " + PORT);
            System.out.println("Waiting for clients to connect...");
            System.out.println();

            while (true) {
                Socket connection = waiter.accept();
                new Thread(() -> {
                    BufferedWriter writer = null;
                    BufferedReader reader = null;

                    String data;

                    if (!Thread.currentThread().isInterrupted()) {
                        try {
                            writer = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
                            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                        } catch (IOException e) {
                            System.out.println("Failed to create streams");
                            e.printStackTrace();
                            Thread.currentThread().interrupt();
                        }

                        if (!Thread.currentThread().isInterrupted()) {
                            System.out.println();
                            System.out.println("New client connected with ip: " + connection.getInetAddress().getHostAddress());

                            do {
                                System.out.println("Waiting for data from a client...");
                                try {
                                    data = reader.readLine() + "!!!";
                                    System.out.println("Data received from client: " + data);
                                    try {
                                        System.out.println("Writing data to client");
                                        writer.write(data);
                                        writer.newLine();
                                        writer.flush();
                                    } catch (IOException e) {
                                        System.out.println("Failed to write data");
                                        break;
                                    }

                                    System.out.println("Sent data to client");
                                    System.out.println();
                                } catch (IOException e) {
                                    System.out.println("Failed to read data");
                                    break;
                                }
                            } while (!data.equals("Stop"));

                            System.out.println("Closing all streams relative to this client");
                            try {
                                writer.close();
                                reader.close();
                                connection.close();
                            } catch (IOException e) {
                                System.out.println("Failed to close all streams");
                                e.printStackTrace();
                            }
                        }
                    }
                }).start();
            }
        } catch (IOException e) {
            System.out.println("Unable to start a server:");
            e.printStackTrace();
        }
    }
}
