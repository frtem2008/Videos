package Sockets.Serialization;

import java.io.IOException;
import java.io.Serializable;
import java.net.ServerSocket;
import java.util.ArrayList;

public class PhoneSerializationExample {
    private static final int PORT = 26780;
    private static final String ADDRESS = "127.0.0.1";

    public static void main(String[] args) {
        new Thread(() -> {
            try (ServerSocket server = new ServerSocket(PORT)) {
                System.out.println("Server started on port: " + PORT);
                while (true) {
                    SerPhone client = new SerPhone(server);
                    System.out.println("Client connected with ip address: " + client.getIp());

                    System.out.println("Waiting for a game...");
                    Object received = client.readObject();
                    System.out.println("Object received with type: ");
                    System.out.println(received.getClass());

                    if (received.getClass().equals(Game.class)) {
                        System.out.println("Received a game: ");
                        Game got = (Game) received;
                        System.out.println(got);

                        System.out.println("Sending a game");
                        client.writeObject(got);
                    } else {
                        System.out.println("Incorrect object received");
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();

        new Thread(() -> {
            try (SerPhone client = new SerPhone(ADDRESS, PORT)) {
                System.out.println("Client connected to server with ip: " + ADDRESS + " on port " + PORT);

                ArrayList<GameObject> gameObjects = new ArrayList<>();
                gameObjects.add(new GameObject(10, 20, 30, 40, false));
                gameObjects.add(new GameObject(-10, 120, 10, 80, true));
                Game toSend = new Game(gameObjects);
                System.out.println("Game created");

                System.out.println("Sending game...");
                client.writeObject(toSend);
                System.out.println("Game sent");

                System.out.println("Waiting for a game");
                Object received = client.readObject();
                System.out.println("Object received with type: ");
                System.out.println(received.getClass());
                if (received.getClass().equals(Game.class)) {
                    System.out.println("Received a game: ");
                    Game got = (Game) received;
                    System.out.println(got);
                } else {
                    System.out.println("Incorrect object received");
                }
                System.out.println("Finished");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }
}

class Game implements Serializable {
    ArrayList<GameObject> gameObjects;

    public Game(ArrayList<GameObject> gameObjects) {
        this.gameObjects = gameObjects;
    }

    @Override
    public String toString() {
        return "Game{" +
                "gameObjects=" + gameObjects +
                '}';
    }
}

class GameObject implements Serializable {
    public int x, y, w, h;
    public transient boolean Bot;

    public GameObject(int x, int y, int w, int h, boolean bot) {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        Bot = bot;
    }

    @Override
    public String toString() {
        return "GameObject{" +
                "x=" + x +
                ", y=" + y +
                ", w=" + w +
                ", h=" + h +
                ", Bot=" + Bot +
                '}';
    }
}
