package Sockets.UDP.Serialization;

import java.io.IOException;
import java.util.ArrayList;

public class UDPPhoneSerialization {
    private final static int PORT = 26782;
    private static final String ADDRESS = "127.0.0.1";
    private final static int MAXDATALENGTH = 256;

    public static void main(String[] args) {
        new Thread(() -> {
            try {
                UDPSerPhone server = new UDPSerPhone(PORT, MAXDATALENGTH);
                System.out.println("Started a server on port: " + PORT);

                Object received;

                received = server.readObject();

                System.out.println("Object received with type: ");
                System.out.println(received.getClass());
                if (received.getClass().equals(GameData.class)) {
                    System.out.println("Received a game: ");
                    GameData got = (GameData) received;
                    System.out.println(got);
                    System.out.println("Sending game " + got);
                    server.writeObject(got);
                    System.out.println("Game has been sent");
                } else {
                    System.out.println("Incorrect object received");
                    System.out.println(received);
                }
                System.out.println("Closing sockets");
                server.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();

        new Thread(() -> {
                    UDPSerPhone client = new UDPSerPhone(ADDRESS, PORT,MAXDATALENGTH);
                    System.out.println("Client socket created");
                    Object received;

                    ArrayList<GameObjectData> gameObjects = new ArrayList<>();
                    gameObjects.add(new GameObjectData(10, 20, 30, 40, false));
                    gameObjects.add(new GameObjectData(-10, 120, 10, 80, true));
                    GameData gameToSend = new GameData(gameObjects);
                    System.out.println("Game created");

                    System.out.println("Sending game " + gameToSend);
                    client.writeObject(gameToSend);
                    System.out.println("Game has been sent");

                    System.out.println("Waiting for a game...");
                    received = client.readObject();

                    System.out.println("Object received with type: ");
                    System.out.println(received.getClass());
                    if (received.getClass().equals(GameData.class)) {
                        System.out.println("Received a game: ");
                        GameData got = (GameData) received;
                        System.out.println(got);
                    } else {
                        System.out.println("Incorrect object received");
                        System.out.println(received);
                    }

                    System.out.println("Closing sockets");
            try {
                System.out.println("Closing all sockets");
                client.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }).start();
    }
}
