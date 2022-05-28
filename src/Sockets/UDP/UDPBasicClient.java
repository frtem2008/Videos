package Sockets.UDP;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;

public class UDPBasicClient {
    private static final String ADDRESS = "127.0.0.1";
    private final static int PORT = 26780;
    private final static int MAXDATALENGTH = 64;

    public static void main(String[] args) {
        Scanner s = new Scanner(System.in);

        try {
            DatagramSocket client = new DatagramSocket();
            System.out.println("Client socket created");
            InetAddress ip = InetAddress.getByName(ADDRESS);

            byte[] sendBuf = new byte[MAXDATALENGTH];
            byte[] recvBuf = new byte[MAXDATALENGTH];

            String sent, read;
            DatagramPacket sendPacket, receivePacket;

            do {
                System.out.println("Input a string to send: ");
                sent = s.nextLine();
                sendBuf = sent.getBytes();

                sendPacket = new DatagramPacket(sendBuf, sendBuf.length, ip, PORT);
                System.out.println("Sending data: " + sent);
                client.send(sendPacket);
                System.out.println("Data has been sent");

                receivePacket = new DatagramPacket(recvBuf, recvBuf.length);
                System.out.println("Waiting for data from server...");
                client.receive(receivePacket);
                read = new String(receivePacket.getData()).trim();
                System.out.println("Got data from server: " + read);
            } while (!sent.equals("Stop"));
            System.out.println("Closing sockets");
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}