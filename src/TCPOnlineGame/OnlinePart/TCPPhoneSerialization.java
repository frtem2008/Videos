package TCPOnlineGame.OnlinePart;
//TODO передаелать это в нормальный сервер

import TCPOnlineGame.Control.Keyboard;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.ServerSocket;

public class TCPPhoneSerialization {
    private static final int PORT = 26781;
    private static final String ADDRESS = "127.0.0.1";

    private static Keyboard keys = new Keyboard();

    public static void main(String[] args) {
        new Thread(() -> {
            try (ServerSocket server = new ServerSocket(PORT)) {
                System.out.println("Server started on port: " + PORT);
                while (true) {
                    TCPSerPhone client = new TCPSerPhone(server);
                    System.out.println("Server: Client connected with ip address: " + client.getIp());
                    Keyboard keyboard;
                    while (true) {
                        keyboard = client.readKeyboard();
                        System.out.println("server: keys: a: " + keyboard.getA() + ", d: " + keyboard.getD() + ", w: " + keyboard.getW() + ", s: " + keyboard.getS());
                        Thread.sleep(200);
                    }
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }).start();

        new Thread(() -> {
            JFrame keyReader = new JFrame();
            keyReader.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            keyReader.setBounds(new Rectangle(200, 200, 400, 400));
            keyReader.setVisible(true);
            keyReader.addKeyListener(keys);

            try (TCPSerPhone client = new TCPSerPhone(ADDRESS, PORT)) {
                System.out.println("Client: Player connected to server with ip: " + ADDRESS + " on port " + PORT);
                while (true) {
                    client.writeKeyboard(keys);
                    System.out.println("client: keys: a: " + keys.getA() + ", d: " + keys.getD() + ", w: " + keys.getW() + ", s: " + keys.getS());
                    keys.update();
                    Thread.sleep(200);
                }

            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
}