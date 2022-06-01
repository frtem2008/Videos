package TCPOnlineGame.OnlinePart;
//TODO передаелать это в нормальный сервер
import TCPOnlineGame.Drawing.Drawer;
import TCPOnlineGame.GameObjects.Bot;
import TCPOnlineGame.GameObjects.Game;
import TCPOnlineGame.GameObjects.Player;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.io.File;
import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.net.ServerSocket;

public class TCPPhoneSerialization {
    private static final int PORT = 26781;
    private static final String ADDRESS = "127.0.0.1";


    public static void main(String[] args) {
        new Thread(() -> {
            try (ServerSocket server = new ServerSocket(PORT)) {
                System.out.println("Server started on port: " + PORT);
                while (true) {
                    TCPSerPhone client = new TCPSerPhone(server);
                    System.out.println("Client connected with ip address: " + client.getIp());

                    String data;
                    System.out.println("Writing: " + client.writeLine("Aboba"));
                    System.out.println("Waiting for reply...");
                    data = client.readLine();
                    System.out.println("Data received: " + data);

                    System.out.println("Waiting for a game...");
                    Object received = client.readObject();
                    System.out.println("Object received with type: ");
                    System.out.println(received.getClass());

                    if (received.getClass().equals(Game.class)) {
                        System.out.println("Received a game: ");
                        Game got = (Game) received;
                        System.out.println(got);
                        Drawer a = new Drawer();
                        BufferedImage bg = ImageIO.read(new File("src/TCPOnlineGame/Resources/Images/background.jpg"));
                        DataBuffer buff = bg.getRaster().getDataBuffer();
                        //TODO пересылка не изображений, а сериализованной игры, нето надо 240 мбпс
                        int bytes = buff.getSize() * DataBuffer.getDataTypeSize(buff.getDataType()) / 8;
                        System.err.println(bytes);
                        BufferedImage toSend = new BufferedImage(200, 200, BufferedImage.TYPE_INT_ARGB);
                        toSend.getGraphics().drawOval(100,100,100,100);
                        a.drawGame(got, toSend.getGraphics(),50,50,50,50,128);
                        File abc = new File("Sent.png");
                        abc.createNewFile();
                        System.out.println(abc.getAbsolutePath());
                        ImageIO.write(toSend,"png", abc);
                        System.out.println("Sending an image");
                        client.writeImage(toSend);
                    } else {
                        System.out.println("Incorrect object received");
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();

        new Thread(() -> {
            JFrame a = new JFrame("Test");
            a.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            a.setLayout(null);
            //разворачиваем
            a.setBounds(300, 300, 600, 600);
            a.setVisible(true);

            try (TCPSerPhone client = new TCPSerPhone(ADDRESS, PORT)) {
                System.out.println("Player connected to server with ip: " + ADDRESS + " on port " + PORT);

                String data;
                System.out.println("Waiting for data...");
                data = client.readLine();
                System.out.println("Data received: " + data);
                System.out.println("Writing: " + client.writeLine("Amogus"));


                Game toSend = new Game();
                toSend.bots.add(new Bot(10, 20, 30, 40));
                toSend.players.add(new Player(-10, 120, 10, 80));
                System.out.println("Game created");

                System.out.println("Sending game...");
                client.writeObject(toSend);
                System.out.println("Game sent");

                System.out.println("Waiting for an image");
                BufferedImage received = client.readImage();
                System.out.println("Object received with type: ");

                System.out.println(received.getClass());
                System.out.println("Drawing image");
                File abc = new File("Received.png");
                abc.createNewFile();
                System.out.println(abc.getAbsolutePath());
                ImageIO.write(received,"png", abc);
                a.getGraphics().drawImage(received, 0, 0, null);
                System.out.println("Finished");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }
}
class InstrumentationAgent {
    private static volatile Instrumentation globalInstrumentation;

    public static void premain(final String agentArgs, final Instrumentation inst) {
        globalInstrumentation = inst;
    }

    public static long getObjectSize(final Object object) {
        if (globalInstrumentation == null) {
            throw new IllegalStateException("Agent not initialized.");
        }
        return globalInstrumentation.getObjectSize(object);
    }
}