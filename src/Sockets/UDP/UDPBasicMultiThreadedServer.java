package Sockets.UDP;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UDPBasicMultiThreadedServer {
    private final static int PORT = 26780;
    private final static int MAXDATALENGTH = 64;

    public static void main(String[] args) {
        try {
            DatagramSocket server = new DatagramSocket(PORT);
            System.out.println("Started a server on port: " + PORT);
            do {
                byte[] recvBuf = new byte[MAXDATALENGTH];

                String received;
                DatagramPacket receive;

                receive = new DatagramPacket(recvBuf, recvBuf.length);
                System.out.println("Waiting for data from client...");
                try {
                    server.receive(receive);
                    received = new String(receive.getData()).trim();
                    System.out.println("Got data from client: " + received);
                    System.out.println("Calculating data...");

                    new Thread(() -> {
                        DatagramPacket send;

                        String sent = calc(received);
                        byte[] sendBuf = sent.getBytes();
                        InetAddress ip;
                        int sendPort;

                        ip = receive.getAddress();
                        sendPort = receive.getPort();

                        send = new DatagramPacket(sendBuf, sendBuf.length, ip, sendPort);
                        System.out.println("Sending data " + sent);
                        try {
                            server.send(send);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        System.out.println("Data has been sent");
                    }).start();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            } while (true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //функция для преобразования
    public static String calc(String toCalc) {
        return toCalc + "!!!";
    }
}
