package Sockets.UDP;

import java.io.IOException;

public class UDPPhoneServer {
    private final static int PORT = 26780;
    private final static int MAXDATALENGTH = 64;

    public static void main(String[] args) {
        UDPPhone server = new UDPPhone(PORT, MAXDATALENGTH);

        System.out.println("Started a server on port: " + PORT);
        String received;
        do {

            System.out.println("Waiting for data from client...");
            received = server.readLine();
            System.out.println("Got data from client: " + received);
            System.out.println("Calculating data...");
            String sent = calc(received);
            System.out.println("Sending data " + sent);
            server.writeLine(sent);
            System.out.println("Data has been sent");
        } while (!received.equals("Stop"));
        try {
            System.out.println("Closing sockets");
            server.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //функция для преобразования (возможно, что-то сложное)
    public static String calc(String toCalc) {
        return toCalc + "!!!";
    }
}
