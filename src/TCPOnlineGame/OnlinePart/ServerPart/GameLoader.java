package TCPOnlineGame.OnlinePart.ServerPart;
//основной игровой класс
//TODO диагональная скорость, поспать, боты, пушки))))) КОРОЛЕВСКАЯ БИТВА С БОТАМИ ААААААА ПЛАНЫЫЫЫ
//TODO binary search everywhere, where possible

import TCPOnlineGame.GameObjects.Game;
import TCPOnlineGame.GameObjects.Wall;
import TCPOnlineGame.Utils.LeeAlgorithm;
import TCPOnlineGame.Utils.Vector2D;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class GameLoader {
    private static final boolean test = true;
    public static boolean[][] botMap;
    public static int cellSize = Game.DEFAULTCELLSIZE;
    private static Image MapImage;


    //инициализация миникарты (замена цветов)
    public BufferedImage initMap(BufferedImage map) {
        System.out.println("Initialising map");
        BufferedImage miniMap = new BufferedImage(map.getWidth(), map.getHeight(), BufferedImage.TYPE_INT_ARGB);
        boolean[][] botMapTmp = new boolean[map.getWidth()][map.getHeight()];
        int checkColor;
        for (int i = 0; i < map.getWidth(); i++) {
            for (int j = 0; j < map.getHeight(); j++) {
                checkColor = map.getRGB(i, j);
                botMapTmp[j][i] = checkColor == new Color(0, 0, 0).getRGB() ||
                        checkColor == new Color(255, 0, 0).getRGB();

                if (checkColor == new Color(0, 0, 255).getRGB() ||
                        checkColor == new Color(255, 255, 255).getRGB() ||
                        checkColor == new Color(255, 0, 0).getRGB()
                ) {
                    miniMap.setRGB(i, j, new Color(0, 0, 0, 128).getRGB());
                } else {
                    miniMap.setRGB(i, j, map.getRGB(i, j));
                }
            }
        }

        LeeAlgorithm.printMap(botMapTmp);
        botMap = botMapTmp;

        System.out.println("Initialising map finished");
        return miniMap;
    }

    public Game initGame(Image mapImage) {
        Game res = new Game();
        if (test) {
            res.playerSpawnPoints.add(new Vector2D(0, 0));
            res.walls.add(new Wall(100, 0, 1000, 1000));
        } else {
            System.out.println("Initialising game...");

            //Изображение карты
            BufferedImage mapPixel = new BufferedImage(
                    mapImage.getWidth(null),
                    mapImage.getHeight(null),
                    BufferedImage.TYPE_INT_RGB
            );

            Graphics2D mapGraphics = mapPixel.createGraphics();
            mapGraphics.drawImage(mapImage, 0, 0, null);
            mapGraphics.dispose();

            for (int i = 0; i < mapPixel.getWidth(); i++) {
                for (int j = 0; j < mapPixel.getHeight(); j++) {
                    if (mapPixel.getRGB(i, j) == new Color(0, 0, 0).getRGB()) {
                        res.walls.add(new Wall(i * cellSize, j * cellSize, cellSize, cellSize));
                    } else if (mapPixel.getRGB(i, j) == new Color(0, 0, 255, 255).getRGB()) {
                        res.playerSpawnPoints.add(new Vector2D(i * cellSize, j * cellSize));
                    }
                }
            }

            //TODO слияние соседних стен в одну
            System.out.println("Walls: " + res.walls.size());
            System.out.println("Player spawnPoints: " + res.playerSpawnPoints.size());
            System.out.println("Initialising game finished");
        }
        return res;
    }

    public void loadImages() {
        try {
            MapImage = ImageIO.read(new File("src/TCPOnlineGame/Resources/Images/Map2.png"));
        } catch (IOException e) {
            System.err.println("Failed to load map image");
            e.printStackTrace();
        }
    }

    //перезагрузка игры
    public Game getNewGame() {
        loadImages();
        return initGame(MapImage);
    }


    //TODO зависимость от кол-ва игроков
    public Vector2D randomSpawnPoint(Game game) {
        //0.00001 чтобы не было 2, при Math.random = 1
        return game.playerSpawnPoints.get((int) (Math.random() - 0.00001 * game.playerSpawnPoints.size()));
    }
}
