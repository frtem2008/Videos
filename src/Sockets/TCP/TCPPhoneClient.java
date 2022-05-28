package Sockets.TCP;

import java.io.IOException;
import java.util.Scanner;

public class TCPPhoneClient {
    private static final String ADDRESS = "127.0.0.1";
    private static final int PORT = 26780;

    public static void main(String[] args) {
        Scanner s = new Scanner(System.in);
        TCPPhone server = new TCPPhone(ADDRESS, PORT);
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
            System.out.println("Closing the socket");
            server.close();
        } catch (IOException e) {
            System.out.println("Unable to close the socket");
            e.printStackTrace();
        }
    }
}
