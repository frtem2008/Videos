package Sockets.TCP.Serialization;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class TCPBasicSerialization {
    private static final String ADDRESS = "127.0.0.1";
    private static final int PORT = 26780;

    public static void main(String[] args) {
        new Thread(() -> {
            Socket server = null;
            ServerSocket serverSocket = null;

            try {
                serverSocket = new ServerSocket(PORT);
                System.out.println("Server created");

                server = serverSocket.accept();
                System.out.println("Client connected");

                ObjectOutputStream objWriter = new ObjectOutputStream(server.getOutputStream());
                ObjectInputStream objReader = new ObjectInputStream(server.getInputStream());
                System.out.println("Streams created");

                objWriter.flush();

                System.out.println("Waiting for a game...");
                Object received = objReader.readObject();

                System.out.println("Object received with type: ");
                System.out.println(received.getClass());

                if (received.getClass().equals(GameData.class)) {
                    System.out.println("Received a game: ");
                    GameData got = (GameData) received;
                    System.out.println(got);

                    System.out.println("Sending a game");
                    objWriter.writeObject(got);
                } else {
                    System.out.println("Incorrect object received");
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }

            try {
                server.close();
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();

        new Thread(() -> {
            try {
                Socket client = new Socket(ADDRESS, PORT);
                System.out.println("Client created");

                ObjectOutputStream objWriter = new ObjectOutputStream(client.getOutputStream());
                ObjectInputStream objReader = new ObjectInputStream(client.getInputStream());
                objWriter.flush();
                System.out.println("Streams created");

                ArrayList<GameObjectData> gameObjects = new ArrayList<>();
                gameObjects.add(new GameObjectData(10, 20, 30, 40, false));
                gameObjects.add(new GameObjectData(-10, 120, 10, 80, true));
                GameData a = new GameData(gameObjects);
                System.out.println("Game created");

                System.out.println("Sending game...");
                objWriter.writeObject(a);
                objWriter.flush();
                System.out.println("Game sent");

                System.out.println("Waiting for a game");
                Object received = objReader.readObject();

                System.out.println("Object received with type: ");
                System.out.println(received.getClass());

                if (received.getClass().equals(GameData.class)) {
                    System.out.println("Received a game: ");
                    GameData got = (GameData) received;
                    System.out.println(got);
                } else {
                    System.out.println("Incorrect object received");
                }

                System.out.println("Finished");
                client.close();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }).start();
    }
}

class GameData implements Serializable {
    ArrayList<GameObjectData> gameObjects;

    public GameData(ArrayList<GameObjectData> gameObjects) {
        this.gameObjects = gameObjects;
    }

    @Override
    public String toString() {
        return "Game{" +
                "gameObjectsData=" + gameObjects +
                '}';
    }
}

class GameObjectData implements Serializable {
    public int x, y, w, h;
    public transient boolean Bot;

    public GameObjectData(int x, int y, int w, int h, boolean bot) {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        Bot = bot;
    }

    @Override
    public String toString() {
        return "GameObjectData{" +
                "x=" + x +
                ", y=" + y +
                ", w=" + w +
                ", h=" + h +
                ", Bot=" + Bot +
                '}';
    }
}