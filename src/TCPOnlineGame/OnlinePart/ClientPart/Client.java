package TCPOnlineGame.OnlinePart.ClientPart;

import TCPOnlineGame.Control.Keyboard;
import TCPOnlineGame.GameObjects.Game;
import TCPOnlineGame.OnlinePart.TCPSerPhone;
import TCPOnlineGame.Utils.FileUtils;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class Client {
    private static File idFile = new File("src/TCPOnlineGame/OnlinePart/ClientFiles/id.dat");
    private static File clientFileDir = new File("src/TCPOnlineGame/OnlinePart/ClientFiles");

    private static final Keyboard keys = new Keyboard();

    public static void main(String[] args) {
        System.out.println("Client started\n");
        new Thread(() -> {
            connect(26780, "127.0.0.1", getId(0));
        }).start();

        new Thread(() -> {
            readKeyboard();
        }).start();
    }

    public static void readKeyboard() {
        JFrame keyReader = new JFrame();
        keyReader.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        keyReader.setBounds(new Rectangle(200, 200, 400, 400));
        keyReader.setVisible(true);
        keyReader.addKeyListener(keys);

        while (true) {
            keys.update();
            System.out.println("keys: a: " + keys.getA() + ", d: " + keys.getD() + ", w: " + keys.getW() + ", s: " + keys.getS());
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static int getId(int old) {
        try {
            int id;

            System.out.println("Attempting to create files...");

            System.out.println("Creating client files directory: " + clientFileDir.mkdir());
            System.out.println("Client files directory path: " + clientFileDir.getAbsolutePath());
            System.out.println();

            if (!idFile.exists()) {
                System.out.println("Creating file to store id: " + idFile.createNewFile());
                id = newId(old);
                FileUtils.clearFile(idFile);
                FileUtils.appendStrToFile(idFile, String.valueOf(id));
                return -id;
            } else {
                System.out.println("File to store id already exists");
                id = Integer.parseInt(FileUtils.readFile(idFile).split("\n")[0].trim());
                return id;
            }
        } catch (IOException e) {
            System.err.println("Getting id failed");
            e.printStackTrace();
            return 0;
        }
    }

    private static int newId(int oldId) {
        int id;
        do {
            id = (int) ((Math.random() * 32767) + 1);
        } while (id == oldId);
        return id;
    }

    public static void connect(int PORT, String ADDRESS, int idToLogin) {
        try (TCPSerPhone client = new TCPSerPhone(ADDRESS, PORT)) {
            System.out.println();
            System.out.println("Player connected to server with ip: " + ADDRESS + " on port " + PORT);
            System.out.println();
            registration(client, idToLogin);
            System.out.println();
            System.out.println("Connected to server");

            System.out.println("Starting sending keys");

            while (true) {
                sendKeys(client, keys);
                System.out.println(receiveGame(client));
                Thread.sleep(1000);
            }

        } catch (IOException | InterruptedException e) {
            System.err.println("Failed to connect to a server");
            e.printStackTrace();
        }
    }

    public static void registration(TCPSerPhone client, int idToLogin) {
        System.out.println("Starting registration");
        String received;
        boolean flag;
        int idToSend = idToLogin, writeSuccess;

        do {
            writeSuccess = client.writeLine(String.valueOf(idToSend));
            System.out.println("Id sent: " + idToSend);
            checkWriteSuccess(writeSuccess, client);
            received = client.readLine();
            System.out.println("Data received: " + received);
            checkReadSuccess(received, client);
            switch (received) {
                case "Error: player with this id already exists, try another one":
                    System.out.println("Login failed: player with id: " + idToLogin + " already exists");
                    idToSend = -newId(idToLogin); //creating new id
                    flag = true;
                    break;
                case "Registration success":
                    System.out.println("Successfully registrated and logged in with id: " + idToSend);
                    flag = false;
                    break;
                case "Login success":
                    System.out.println("Successfully logged in with id: " + idToSend);
                    flag = false;
                    break;
                case "Login failed: id is already online":
                    System.out.println("Id: " + idToSend + " is already online \ncontact to admins (ungazhiv2008@yandex.ru) for more information");
                    System.exit(20);
                case "Login failed: id is free":
                    System.out.println("Login failed: id: " + idToSend + " is free (maybe your account was deleted)" +
                            "\ncontact to admins (ungazhiv2008@yandex.ru) for more information");
                    System.exit(40);
                case "Unexpected error":
                    System.out.println("You were banned! sorry)");
                    System.exit(-100);
                default:
                    flag = true;
                    break;
            }
        } while (flag);

    }

    public static int checkWriteSuccess(int writeSuccess, TCPSerPhone client) {
        if (writeSuccess != 0) {
            try {
                System.out.println("Player with ip: " + client.getIp() + " and id: " + client.id + " disconnected");
                System.out.println("Closing socket");
                client.close();
                System.exit(20);
                return -1;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return -1;
        }
        return 0;
    }

    //функция для удаления неактивных клиентов
    public static int checkReadSuccess(String read, TCPSerPhone client) {
        if (read != null) {
            if (read.equals("-1") || read.equals("-2") || read.equals("-3")) {
                try {
                    System.out.println("Player with ip: " + client.getIp() + " and id: " + client.id + " disconnected");
                    System.out.println("Closing socket");
                    client.close();
                    System.exit(20);
                    return -1;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return 0;
        }
        return -1;
    }

    public static void sendKeys(TCPSerPhone client, Keyboard kb) {
        System.out.println("sending keys...");
        client.writeKeyboard(kb);
        System.out.println("Keys have been sent");
    }

    public static Game receiveGame(TCPSerPhone client) {
        System.out.println("Waiting for a game...");
        Object res = client.readObject();
        System.out.println("Got an object with type " + res.getClass());
        if (res.getClass().equals(Game.class)) {
            System.out.println("Received a game: ");
            System.out.println(res);
            return (Game) res;
        } else {
            return new Game();
        }
    }
}
