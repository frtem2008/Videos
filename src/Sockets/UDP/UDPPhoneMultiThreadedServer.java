package Sockets.UDP;

public class UDPPhoneMultiThreadedServer {
    private final static int PORT = 26781;
    private final static int MAXDATALENGTH = 64;

    public static void main(String[] args) {
        UDPPhone server = new UDPPhone(PORT, MAXDATALENGTH);
        System.out.println("Started a server on port: " + PORT);
        do {
            String received;
            System.out.println("Waiting for data from client...");
            received = server.readLine();
            System.out.println("Got data from client: " + received);
            System.out.println("Calculating data...");

            new Thread(() -> {
                String sent = calc(received);
                System.out.println("Sending data " + sent);
                System.out.println(server.writeLine(sent));
                System.out.println("Data has been sent");
            }).start();
        } while (true);
    }

    //функция для преобразования (возможно, что-то сложное)
    public static String calc(String toCalc) {
        return toCalc + "!!!";
    }
}