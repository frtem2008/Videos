package TCPOnlineGame.GameObjects;
//класс для хранения игры

import TCPOnlineGame.Utils.Vector2D;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;

public class Game implements Serializable {
    //данные
    @Serial
    private static final long serialVersionUID = 1L;

    public static final int DEFAULTCELLSIZE = 64;
    //изменяемые характеристики(кол-во игроков, поле зрения, масштаб...)

    public ArrayList<Wall> walls;
    public ArrayList<Player> players;
    public ArrayList<Bot> bots;
    public ArrayList<Bullet> bullets;
    public ArrayList<Vector2D> playerSpawnPoints;

    //инициализация в конструкторе
    public Game() {
        walls = new ArrayList<>();
        players = new ArrayList<>();
        bots = new ArrayList<>();
        bullets = new ArrayList<>();
        playerSpawnPoints = new ArrayList<>();
    }

    /**
     * игровой тик
     *
     * @param gameTime нужен, чтобы двигать ботов раз в несколько кадров
     *                 боты пока не двигаются
     */
    public void tick(double gameTime) {
        for (int i = 0; i < bullets.size(); i++) {
            bullets.get(i).move();
        }

        for (int i = 0; i < bots.size(); i++) {
            if (gameTime % 10 == 0) {
                //bots.get(i).moveBot(GameLoader.botMap, (int) bots.get(i).cords.x, (int) bots.get(i).cords.y);
            }
            //bots.get(i).moveBot();
        }
    }

    @Override
    public String toString() {
        return "Game{" +
                "walls=" + walls +
                ", players=" + players +
                ", bots=" + bots +
                ", bullets=" + bullets +
                ", playerSpawnPoints=" + playerSpawnPoints +
                '}';
    }
}
