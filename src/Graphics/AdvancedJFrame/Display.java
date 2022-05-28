package Graphics.AdvancedJFrame;
import javax.imageio.ImageIO;
import javax.sql.rowset.serial.SerialArray;
import javax.swing.*;
import java.io.File;
import java.io.IOException;

public class Display extends JFrame {
    //окно
    public static JFrame frame = new JFrame("Advanced JFrame");

    //дефолтные размеры окна
    public static int x = 300, y = 0, w = 1000, h = 800;

    //отслеживание полноэкранного режима
    public static boolean isFullScreen = false;

    //экземпляр главного класса
    public static Main m;

    //подключение графической библиотеки
    static {
        System.setProperty("sun.java2d.opengl", "True");
    }

    //точка входа
    public static void main(String[] args) {
        //иконка для панели задач
        try {
            frame.setIconImage(ImageIO.read(new File("src/Graphics/AdvancedJFrame/Images/player.png")));
        } catch (IOException e) {
            e.printStackTrace();
        }
        //активация кнопки с крестиком
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        //разворачиваем
        frame.setBounds(x, y, w, h);
        frame.setVisible(true);

        //создаём новый экземпляр игры и начинаем рисовать
        m = new Main();
        m.startDrawing(frame);
    }
}
