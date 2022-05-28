package Sockets.UDP.Serialization;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;

public class UDPBasicSerialization {
    private final static int PORT = 26782;
    private static final String ADDRESS = "127.0.0.1";
    private final static int MAXDATALENGTH = 64;

    public static void main(String[] args) {
        new Thread(() -> {
            ByteArrayOutputStream bOS;
            ByteArrayInputStream bIS;
            ObjectOutputStream oOut;
            ObjectInputStream oIn;

            try {
                bOS = new ByteArrayOutputStream(MAXDATALENGTH);
                oOut = new ObjectOutputStream(new BufferedOutputStream(bOS));

                try {
                    DatagramSocket server = new DatagramSocket(PORT);
                    System.out.println("Started a server on port: " + PORT);

                    byte[] sendBuf;
                    byte[] readBuf = new byte[MAXDATALENGTH];

                    Object received;
                    DatagramPacket sendPack, receivePack;
                    InetAddress ip;
                    int sendPort;

                    receivePack = new DatagramPacket(readBuf, readBuf.length);
                    System.out.println("Waiting for a game...");
                    server.receive(receivePack);

                    bIS = new ByteArrayInputStream(readBuf);
                    oIn = new ObjectInputStream(new BufferedInputStream(bIS));
                    received = oIn.readObject();

                    System.out.println("Object received with type: ");
                    System.out.println(received.getClass());
                    if (received.getClass().equals(GameData.class)) {
                        System.out.println("Received a game: ");
                        GameData got = (GameData) received;
                        System.out.println(got);
                        oOut.writeObject(got);
                        oOut.flush();

                        ip = receivePack.getAddress();
                        sendPort = receivePack.getPort();

                        sendBuf = bOS.toByteArray();

                        sendPack = new DatagramPacket(sendBuf, sendBuf.length, ip, sendPort);
                        System.out.println("Sending game " + got);
                        server.send(sendPack);
                        System.out.println("Game has been sent");
                    } else {
                        System.out.println("Incorrect object received");
                    }
                    System.out.println("Closing sockets");
                    server.close();
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();

        new Thread(() -> {
            ByteArrayOutputStream bOS;
            ByteArrayInputStream bIS;
            ObjectOutputStream oOut;
            ObjectInputStream oIn;

            bOS = new ByteArrayOutputStream(MAXDATALENGTH);
            try {
                oOut = new ObjectOutputStream(new BufferedOutputStream(bOS));
                try {
                    DatagramSocket client = new DatagramSocket();
                    System.out.println("Client socket created");
                    InetAddress ip = InetAddress.getByName(ADDRESS);

                    byte[] sendBuf = new byte[MAXDATALENGTH];
                    byte[] readBuf = new byte[MAXDATALENGTH];

                    Object received;
                    DatagramPacket sendPack, receivePack;
                    int sendPort = PORT;

                    ArrayList<GameObjectData> gameObjects = new ArrayList<>();
                    gameObjects.add(new GameObjectData(10, 20, 30, 40, false));
                    gameObjects.add(new GameObjectData(-10, 120, 10, 80, true));
                    GameData gameToSend = new GameData(gameObjects);
                    System.out.println("Game created");

                    System.out.println("Sending game " + gameToSend);
                    oOut.writeObject(gameToSend);
                    oOut.flush();
                    sendBuf = bOS.toByteArray();

                    sendPack = new DatagramPacket(sendBuf, sendBuf.length, ip, PORT);
                    client.send(sendPack);
                    System.out.println("Game has been sent");
                    receivePack = new DatagramPacket(readBuf, readBuf.length);

                    System.out.println("Waiting for a game...");
                    client.receive(receivePack);

                    bIS = new ByteArrayInputStream(readBuf);
                    oIn = new ObjectInputStream(new BufferedInputStream(bIS));
                    received = oIn.readObject();

                    System.out.println("Object received with type: ");
                    System.out.println(received.getClass());
                    if (received.getClass().equals(GameData.class)) {
                        System.out.println("Received a game: ");
                        GameData got = (GameData) received;
                        System.out.println(got);
                    } else {
                        System.out.println("Incorrect object received");
                    }

                    System.out.println("Closing sockets");
                    client.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
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
