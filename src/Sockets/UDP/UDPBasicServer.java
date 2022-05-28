package Sockets.UDP;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UDPBasicServer {
    private final static int PORT = 26780;
    private final static int MAXDATALENGTH = 64;

    public static void main(String[] args) {
        try {
            DatagramSocket server = new DatagramSocket(PORT);
            System.out.println("Started a server on port: " + PORT);

            byte[] sendBuf;
            byte[] recvBuf = new byte[MAXDATALENGTH];

            String sent, received;
            DatagramPacket send, receive;
            InetAddress ip;
            int sendPort;

            do {
                receive = new DatagramPacket(recvBuf, recvBuf.length);
                System.out.println("Waiting for data from client...");
                server.receive(receive);
                received = new String(receive.getData()).trim();
                System.out.println("Got data from client: " + received);
                sent = received + "!!!";
                sendBuf = sent.getBytes();

                ip = receive.getAddress();
                sendPort = receive.getPort();

                send = new DatagramPacket(sendBuf, sendBuf.length, ip, sendPort);
                System.out.println("Sending data " + sent);
                server.send(send);
                System.out.println("Data has been sent");
            } while (!received.equals("Stop"));
            System.out.println("Closing sockets");
            server.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}