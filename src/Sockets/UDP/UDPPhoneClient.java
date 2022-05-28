package Sockets.UDP;

import java.io.IOException;
import java.util.Scanner;

public class UDPPhoneClient {
    private static final String ADDRESS = "127.0.0.1";
    private final static int PORT = 26781;
    private final static int MAXDATALENGTH = 64;

    public static void main(String[] args) {
        Scanner s = new Scanner(System.in);
        UDPPhone server = new UDPPhone(ADDRESS, PORT, MAXDATALENGTH);
        System.out.println("Connected to server: ip address is " + server.getIp());
        String data;

        do {
            System.out.println("Input data to send: ");
            data = s.nextLine();
            System.out.println("Sending data to client");
            server.writeLine(data);
            System.out.println("Data has been sent");
            System.out.println("Waiting for data from server");
            System.out.println("Received data from server: " + server.readLine());
        } while (!data.equals("Stop"));

        try {
            System.out.println("Closing sockets");
            server.close();
        } catch (IOException e) {
            System.out.println("Unable to close the socket");
            e.printStackTrace();
        }
    }
}
