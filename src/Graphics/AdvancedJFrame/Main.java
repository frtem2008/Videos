package Graphics.AdvancedJFrame;

//основной игровой класс

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Main {
    private static final double cameraY = 400;
    //коодинаты игрока, который находится в центре экрана
    public static int mainPlayerX, mainPlayerY;
    //смещение камеры (положения игрока) относительно левого верхнего угла экрана
    private static double cameraX = 500;
    //миникарта
    //изображения
    private static Image Wall, Player, Bot, Bullet, MapImage, Background;
    //клавиатура + мышь
    public final Mouse mouse = new Mouse();
    private final Keyboard keyboard = new Keyboard();

    private static SimplePlayer player = new SimplePlayer(100,100,100,100);

    //начало игры ()
    public void startDrawing(JFrame frame) {
        new Thread(() -> {
            //подгружаем изображения и прогружаем игру
            loadImages();
            //привязываем слушатели
            frame.addKeyListener(keyboard);
            frame.addMouseListener(mouse);
            frame.addMouseMotionListener(mouse);

            //изображение для отрисовки (для изменения пикселей после рисования объектов)
            BufferedImage frameImage = new BufferedImage(1920, 1080, BufferedImage.TYPE_INT_RGB);

            //создание буфера
            frame.createBufferStrategy(2);
            BufferStrategy bs = frame.getBufferStrategy();

            //для использования tab, alt и т.д
            frame.setFocusTraversalKeysEnabled(false);

            //для стабилизации и ограничения фпс
            long start, end, len;
            double frameLength;

            //графика итогового окна
            Graphics2D frameGraphics;

            //длина кадра (число после дроби - фпс)
            frameLength = 1000.0 / 60;
            int frames = 0;

            //размер JFrame на самом деле
            Dimension frameSize;


            //главный игровой цикл
            while (true) {
                //время начала кадра
                start = System.currentTimeMillis();

                //обновление размера JFrame
                frameSize = frame.getContentPane().getSize();
                //получение информации о буфере
                frameGraphics = (Graphics2D) bs.getDrawGraphics();

                //очистка экрана перед рисованием
                frameGraphics.clearRect(0, 0, frame.getWidth(), frame.getHeight());
                frameImage.getGraphics().clearRect(0, 0, frameImage.getWidth(), frameImage.getHeight());
                frameImage.getGraphics().drawImage(Background, 0, 0, null);
                //рисование на предварительном изображении
                this.draw(frameImage.getGraphics());
                //отрисовка миникарты

                //рисование на итоговом окне
                frameGraphics.drawImage(frameImage, 0, 0, frameImage.getWidth(), frameImage.getHeight(), null);

                //очистка мусора
                frameImage.getGraphics().dispose();
                frameGraphics.dispose();

                //показ буфера на холсте
                bs.show();

                //разворот на полный экран
                if (Keyboard.getF11()) {
                    while (Keyboard.getF11()) {
                        keyboard.update();
                        try {
                            Thread.sleep(1);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    frame.dispose();
                    if (Display.isFullScreen) {
                        frame.setUndecorated(false);
                        frame.setExtendedState(Frame.NORMAL);
                        frame.setBounds(Display.x, Display.y, (int) frameSize.getWidth(), (int) frameSize.getHeight());
                        cameraX = 500;
                    } else {
                        cameraX = frameSize.getWidth() / 1.2;
                        frame.setUndecorated(true);
                        frame.setExtendedState(6);
                    }
                    Display.isFullScreen = !Display.isFullScreen;
                    frame.setVisible(true);
                }

                //код для выхода из игры
                if (Keyboard.getQ()) {
                    System.out.println("Выход");
                    System.exit(20);
                }

                //перезагрузка игры
                if (Keyboard.getR()) {
                    System.out.println("Reloading...");
                    loadImages();
                    System.out.println("Reloading finished");
                }

                //обновления клавиатуры и игрока
                keyboard.update();
                player.move();
                frames++;

                //замер времени, ушедшего на отрисовку кадра
                end = System.currentTimeMillis();
                len = end - start;

                //стабилизация фпс
                if (len < frameLength) {
                    try {
                        Thread.sleep((long) (frameLength - len));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        }).start();
    }

    public void draw(Graphics g) {
        g.drawImage(Player, player.x, player.y, player.w, player.h, null);
    }

    //функция загрузки изображений (путь к папке: src/Resources/Images/)
    public void loadImages() {
        System.out.println("Loading images");
        try {
            Player = ImageIO.read(new File("src/Graphics/AdvancedJFrame/Images/player.png"));
            Background = ImageIO.read(new File("src/Graphics/AdvancedJFrame/Images/background.jpg"));
        } catch (IOException e) {
            System.out.println("Failed loading images");
            e.printStackTrace();
            return;
        }
        System.out.println("Finished loading images");
    }
}

class SimplePlayer {
    int x, y, w, h;

    public SimplePlayer(int x, int y, int w, int h) {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
    }

    public void move() {
        if (Keyboard.getA())
            x--;
        if (Keyboard.getD())
            x++;
        if (Keyboard.getW())
            y--;
        if (Keyboard.getS())
            y++;
    }
}
