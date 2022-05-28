package Graphics.BasicJFrame;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Main {
    //изображения
    private static Image Player;
    private static Image Bot;
    //изображение миникарты на жкране
    private static BufferedImage miniMapImage;


    //начало игры ()
    public void startDrawing(JFrame frame) {
        new Thread(() -> {
            //подгружаем изображения
            loadImages();
            //изображение для отрисовки (для изменения пикселей после рисования объектов)
            BufferedImage frameImage = new BufferedImage(1920, 1080, BufferedImage.TYPE_INT_RGB);

            //создание буфера
            frame.createBufferStrategy(2);
            BufferStrategy bs = frame.getBufferStrategy();

            //для стабилизации и ограничения фпс
            long start, end, len;
            double frameLength;

            //графика итогового окна
            Graphics2D frameGraphics;

            //длина кадра (число после дроби - фпс)
            frameLength = 1000.0 / 60;
            int frames = 0;

            //размер JFrame на самом деле (при растягивании мышью и проч)
            Dimension frameSize = frame.getContentPane().getSize();


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
        g.drawImage(Player, 200, 200, null);
        g.drawImage(Bot, 200, 400, 60,80, null);
        g.setColor(Color.RED);
        g.drawLine(100,100,200,200);
        g.setColor(Color.GREEN);
        g.fillRect(300,300,20,100);
        g.setColor(Color.BLACK);
        g.drawOval(120,20,100,50);
    }

    //функция загрузки изображений (путь к папке: src/Graphics/BasicJFrame/Images/)
    public void loadImages() {
        System.out.println("Loading images");
        try {
            Player = ImageIO.read(new File("src/Graphics/BasicJFrame/Images/player.png"));
            Bot = ImageIO.read(new File("src/Graphics/BasicJFrame/Images/bot.png"));
        } catch (IOException e) {
            System.out.println("Failed loading images");
            e.printStackTrace();
            return;
        }
        System.out.println("Finished loading images");
    }
}
