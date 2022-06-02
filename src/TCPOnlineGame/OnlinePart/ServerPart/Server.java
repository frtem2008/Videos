package TCPOnlineGame.OnlinePart.ServerPart;

import TCPOnlineGame.Control.Keyboard;
import TCPOnlineGame.GameObjects.Game;
import TCPOnlineGame.GameObjects.Player;
import TCPOnlineGame.OnlinePart.TCPSerPhone;
import TCPOnlineGame.Utils.FileUtils;
import TCPOnlineGame.Utils.Vector2D;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Iterator;

public class Server {
    private static final int PORT = 26780;
    private static final int readingDelayMillis = 500;
    private static HashSet<TCPSerPhone> clients = new HashSet<>();

    private static HashSet<String> bannedIps = new HashSet<>();
    private static HashSet<Integer> bannedIds = new HashSet<>();

    private static HashSet<Integer> idAll = new HashSet<>();
    private static HashSet<Integer> activeIds = new HashSet<>();
    private static HashSet<Integer> idBanned = new HashSet<>();
    private static HashSet<String> ipBanned = new HashSet<>();

    private static File idFile = new File("src/TCPOnlineGame/OnlinePart/ServerFiles/ids.dat");
    private static File banIdFile = new File("src/TCPOnlineGame/OnlinePart/ServerFiles/banId.dat");
    private static File banIpFile = new File("src/TCPOnlineGame/OnlinePart/ServerFiles/banIp.dat");
    private static File serverFileDir = new File("src/TCPOnlineGame/OnlinePart/ServerFiles");

    private static GameLoader gameLoader = new GameLoader();
    private static Game game;

    public static void main(String[] args) {
        createFiles();
        fillArrays();

        new Thread(() -> {
            server();
        }).start();

    }

    public static void createFiles() {
        try {
            boolean successfulCreation;

            File test = new File("pathLiveFishServer");

            System.out.println("Attempting to create files...");
            System.out.println("Creating pathGettingFile: " + test.createNewFile());

            serverFileDir = new File(test.getAbsolutePath().replaceAll("pathLiveFishServer", ""));

            System.out.println("Creating server files directory: " + serverFileDir.mkdir());
            System.out.println("Server files directory path: " + serverFileDir.getAbsolutePath());

            File[] toCreate = {
                    idFile,
                    banIdFile,
                    banIpFile,
            };
            System.out.println();
            for (int i = 0; i < toCreate.length; i++) {
                File cur = toCreate[i];
                if (!cur.exists()) {
                    successfulCreation = cur.createNewFile();
                    if (successfulCreation) {
                        System.out.println("Successfully created file: " + cur.getName());
                    } else {
                        System.out.println("Failed to create file: " + cur.getName());
                    }
                } else {
                    System.out.println("File: " + cur.getName() + " already exists");
                }
            }
            System.out.println();
            FileUtils.deleteFile(test);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error creating files");
        }
        System.out.println();
    }

    public static void fillArrays() {
        fillIds(idAll, FileUtils.readFile(idFile));
        fillIds(idBanned, FileUtils.readFile(banIdFile));
        fillIps(ipBanned, FileUtils.readFile(banIpFile));
    }

    public static void fillIps(HashSet<String> toFill, String ips) {
        String[] ipSplit = ips.split("\n");
        if (ipSplit[0].equals("")) {
            System.out.println("No ips to parse");
            return;
        }
        System.out.println("Ids read from file: ");
        for (int i = 0; i < ipSplit.length; i++) {
            toFill.add(ipSplit[i].trim());
            //красивый вывод без запятой в конце
            if (i != ipSplit.length - 1) {
                System.out.print(ipSplit[i].trim() + ", ");
            } else {
                System.out.print(ipSplit[i].trim());
            }
        }
        System.out.println();
    }

    public static void fillIds(HashSet<Integer> toFill, String ids) {
        String[] idSplit = ids.split("\n");
        if (idSplit[0].equals("")) {
            System.out.println("No ids to parse");
            return;
        }
        System.out.println("Ids read from file: ");
        for (int i = 0; i < idSplit.length; i++) {
            toFill.add(Integer.parseInt(idSplit[i].trim()));
            //красивый вывод без запятой в конце
            if (i != idSplit.length - 1) {
                System.out.print(Integer.parseInt(idSplit[i].trim()) + ", ");
            } else {
                System.out.print(Integer.parseInt(idSplit[i].trim()));
            }
        }
        System.out.println();
    }

    public static void server() {
        try (ServerSocket server = new ServerSocket(PORT)) {
            System.out.println();
            System.out.println("Started a server on port: " + PORT);
            System.out.println("Creating new game...");
            game = gameLoader.getNewGame();
            System.out.println("Created new game");
            System.out.println();

            System.out.println("Waiting for clients to connect...");

            while (true) {
                TCPSerPhone client = new TCPSerPhone(server);
                new Thread(() -> {
                    Game toSend;
                    Keyboard keys;
                    int uniId, tmp, writingSuccess;

                    System.out.println("\n\nNew player connected with ip: " + client.getIp());

                    System.out.println("Authorizing player with ip: " + client.getIp());
                    //регистрация или логин
                    boolean flag = registration(client);

                    if (flag) {
                        System.out.println("Client login success (id: " + client.id + ", ip: " + client.getIp() + ")");
                        clients.add(client);
                    } else {
                        clients.remove(client);
                        System.out.println("Player login failed (ip: " + client.getIp() + ")");
                    }

                    System.out.println();
                    System.out.println("Player operations started");
                    System.out.println("Picking a spawn point for a new player...");
                    Vector2D spawnPoint = gameLoader.randomSpawnPoint(game);
                    Player player = new Player(
                            spawnPoint.x,
                            spawnPoint.y,
                            Game.DEFAULTCELLSIZE,
                            Game.DEFAULTCELLSIZE,
                            client.id);
                    game.players.add(player);
                    System.out.println("Spawn point of a player with id: " + client.id + " is " + spawnPoint);
                    System.out.println("Player operations finished");

                    while (flag) {
                        System.out.println();
                        System.out.println("Waiting for keys from player with id: " + client.id);
                        keys = client.readKeyboard();
                        checkReadSuccess(keys, client);
                        System.out.println("keys: a: " + keys.getA() + ", d: " + keys.getD() + ", w: " + keys.getW() + ", s: " + keys.getS());
                        if (!Thread.currentThread().isInterrupted()) {
                            System.out.println("Got keys from player with id: " + client.id);
                            System.out.println("Moving player");
                            player.move(game, keys);
                            System.out.println("Player moved");
                            System.out.println("Sending a game to client...");
                            writingSuccess = client.writeObject(game);
                            checkWriteSuccess(writingSuccess, client);
                            if (!Thread.currentThread().isInterrupted()) {
                                System.out.println("Game has been sent");
                                try {
                                    Thread.sleep(readingDelayMillis);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                break;
                            }
                        } else {
                            break;
                        }
                    }

                    if (!Thread.currentThread().isInterrupted()) {
                        try {
                            System.out.println("Client with ip: " + client.getIp() + " and id: " + client.id + " disconnected");
                            System.out.println("Closing the socket");
                            clients.remove(client);
                            client.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                            System.err.println("Failed to close the socket");
                        }
                    }
                }).start();
            }
        } catch (IOException e) {
            System.err.println("Unable to start a server");
            e.printStackTrace();
        }
    }

    public static boolean registration(TCPSerPhone client) {
        String keys;
        boolean flag = false;
        int tmp, writingSuccess;
        do {
            refreshActiveIDs();
            keys = client.readLine();
            System.out.println("Data received from player: " + keys);

            tmp = Integer.parseInt(keys);
            System.out.println("Player with ip: " + client.getIp() + " sent uniId to authorize: " + tmp);
            //новый игрок
            if (tmp < 0) {
                if (idAll.contains(-tmp)) {
                    System.out.println("Player with ip: " + client.getIp() + " attempted to register an existing id");
                    client.writeLine("Error: player with this id already exists, try another one");
                    flag = true;
                    continue;
                } else {
                    FileUtils.appendStrToFile(idFile, String.valueOf(-tmp));
                    writingSuccess = client.writeLine("Registration success");
                    if (checkWriteSuccess(writingSuccess, client) == 0) {
                        flag = false;
                        break;
                    }

                    client.id = -tmp;
                    idAll.add(-tmp);
                    System.out.println("Successfully registrated new player with id: " + (-tmp));
                    flag = true;
                    break;
                }
            } else if (tmp > 0) {
                if (idAll.contains(tmp)) {
                    if (!activeIds.contains(tmp)) {
                        writingSuccess = client.writeLine("Login success");
                        if (checkWriteSuccess(writingSuccess, client) == 0) {
                            client.id = tmp;
                            System.out.println("Player with ip: " + client.getIp() + " and id: " + client.id + " has logged in");
                            flag = true;
                            break;
                        }
                    } else {
                        writingSuccess = client.writeLine("Login failed: id is already online");
                        if (checkWriteSuccess(writingSuccess, client) == 0) {
                            System.out.println("Player with ip: " + client.getIp() + " logging in failed: player with id: " + tmp + " has already logged in");
                            continue;
                        }
                        flag = false;
                        break;
                    }
                } else {
                    writingSuccess = client.writeLine("Login failed: id is free");
                    if (checkWriteSuccess(writingSuccess, client) == 0) {
                        System.out.println("Player with ip: " + client.getIp() + " logging in failed: id: " + tmp + " is free");
                        continue;
                    }
                    flag = false;
                    break;
                }
            } else {
                System.out.println("Player with ip: " + client.getIp() + " is a cheater: 0 id ");
                FileUtils.appendStrToFile(banIpFile, client.getIp() + ": " + LocalDateTime.now());
                client.writeLine("Unexpected error"); //сокрытие для читера
                flag = false;
                break;
            }
        } while (flag);
        return flag;
    }

    public static int checkWriteSuccess(int writeSuccess, TCPSerPhone client) {
        if (writeSuccess != 0) {
            try {
                System.out.println("Player with ip: " + client.getIp() + " and id: " + client.id + " disconnected");
                System.out.println("Closing socket");
                clients.remove(client);
                refreshActiveIDs();
                client.close();
                Thread.currentThread().interrupt();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return -1;
        }
        return 0;
    }

    public static void checkReadSuccess(Object read, TCPSerPhone client) {
        if (read == null || read.equals("-1") || read.equals("-2") || read.equals("-3")) {
            try {
                System.out.println("Player with ip: " + client.getIp() + " and id: " + client.id + " disconnected");
                clients.remove(client);
                refreshActiveIDs();
                System.out.println("Closing socket");
                client.close();
                Thread.currentThread().interrupt();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void refreshActiveIDs() {
        Iterator<TCPSerPhone> iter = clients.iterator();
        activeIds.clear();
        TCPSerPhone tmp;
        while (iter.hasNext()) {
            tmp = iter.next();
            activeIds.add(tmp.id);
            System.out.println("Client: " + tmp);
        }
    }
}

