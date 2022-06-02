package TCPOnlineGame.GameObjects;
//игрок
//TODO Здоровье

import TCPOnlineGame.Control.Keyboard;
import TCPOnlineGame.Control.Mouse;
import TCPOnlineGame.OnlinePart.ServerPart.GameLoader;
import TCPOnlineGame.Utils.Vector2D;

import java.awt.*;

public class Player extends GameObject {

    public Vector2D armHealth = new Vector2D(50, 100);
    public int id;
    public Vector2D speed = new Vector2D(0.0, 0.0); // скорость игрока по x / y
    public Vector2D maxSpeed = new Vector2D(GameLoader.cellSize / 10.6, GameLoader.cellSize / 10.6); //10.6 - важное число, лучше не менять

    public Player(double x, double y, double w, double h, int id) {
        super(x, y, w, h);
        this.id = id;
    }

    public Player(double x, double y, double w, double h, Image texture, int id) {
        super(x, y, w, h, texture);
        this.id = id;
    }

    //ONLY FOR BOTS BOTS
    public Player(double x, double y, double w, double h) {
        super(x, y, w, h);
    }

    //ONLY FOR BOTS
    public Player(double x, double y, double w, double h, Image texture) {
        super(x, y, w, h, texture);
    }


    //движение игрока
    public void move(Game game, Keyboard keys) {
        //оптимизация столкновений
        //прямоугольник вокруг игрока
        Rectangle collisionArea = new Rectangle((int) (cords.x - maxSpeed.x),
                (int) (cords.y - maxSpeed.y),
                (int) (cords.x + maxSpeed.x),
                (int) (cords.y + maxSpeed.y));

        //отладочный вывод координат
        if (Mouse.mouseClicked) {
            System.out.println("Player X: " + (hitbox.x) + ", Player Y: " + (hitbox.y));
        }

        //изменение скорости
        if ((keys.getA() && keys.getD())
                || (!keys.getA() && !keys.getD())) {
            speed.x *= 0.8;
        }
        if (keys.getA() && !keys.getD()) {
            System.out.println("Moved left");
            speed.x--;
        }
        if (!keys.getA() && keys.getD()) {
            System.out.println("Moved right");
            speed.x++;
        }

        if ((keys.getW() && keys.getS())
                || (!keys.getW() && !keys.getS())) {
            speed.y *= 0.8;
        }
        if (keys.getW() && !keys.getS()) {
            System.out.println("Moved up");
            speed.y--;
        }
        if (!keys.getW() && keys.getS()) {
            System.out.println("Moved down");
            speed.y++;
        }

        //ограничение максивмальной скорости
        if (speed.x > 0 && speed.x < 0.75) {
            speed.x = 0;
        }
        if (speed.x < 0 && speed.x > -0.75) {
            speed.x = 0;
        }
        if (speed.x > maxSpeed.x) {
            speed.x = maxSpeed.x;
        }
        if (speed.x < -maxSpeed.x) {
            speed.x = -maxSpeed.x;
        }

        if (speed.y > 0 && speed.y < 0.75) {
            speed.y = 0;
        }
        if (speed.y < 0 && speed.y > -0.75) {
            speed.y = 0;
        }
        if (speed.y > maxSpeed.y) {
            speed.y = maxSpeed.y;
        }
        if (speed.y < -maxSpeed.y) {
            speed.y = -maxSpeed.y;
        }

        //горизонтальные столкновения
        hitbox.x += speed.x;

        for (int i = 0; i < game.walls.size(); i++) {
            //проверка на то, можно ли вообще столкнуться
            if (game.walls.get(i).cords.y > collisionArea.y + collisionArea.height &&
                    game.walls.get(i).cords.x > collisionArea.x + collisionArea.width) {
                break;
            }

            if (game.walls.get(i).cords.y < collisionArea.y - collisionArea.height &&
                    game.walls.get(i).cords.x < collisionArea.x - collisionArea.width)
                continue;

            if (game.walls.get(i).hitbox.intersects(hitbox)) {
                hitbox.x -= speed.x;
                while (!game.walls.get(i).hitbox.intersects(hitbox)) {
                    hitbox.x += Math.signum(speed.x);
                }
                hitbox.x -= Math.signum(speed.x);
                speed.x = 0;
                cords.x = hitbox.x;
            }
        }
        //вертикальные столкновения
        hitbox.y += speed.y;
        for (int i = 0; i < game.walls.size(); i++) {
            //проверка на то, можно ли вообще столкнуться
            if (game.walls.get(i).cords.y > collisionArea.y + collisionArea.height &&
                    game.walls.get(i).cords.x > collisionArea.x + collisionArea.width)
                break;

            if (game.walls.get(i).cords.y < collisionArea.y - collisionArea.height &&
                    game.walls.get(i).cords.x < collisionArea.x - collisionArea.width)
                continue;

            if (game.walls.get(i).hitbox.intersects(hitbox)) {
                hitbox.y -= speed.y;
                while (!game.walls.get(i).hitbox.intersects(hitbox)) {
                    hitbox.y += Math.signum(speed.y);
                }
                hitbox.y -= Math.signum(speed.y);
                speed.y = 0;
                cords.y = hitbox.y;
            }
        }
        //изменение координат относительно скорости
        cords.x += speed.x;
        cords.y += speed.y;

        //перемещение хитбокса
        hitbox.x = (int) cords.x;
        hitbox.y = (int) cords.y;
    }
}
