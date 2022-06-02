package TCPOnlineGame.OnlinePart;

import TCPOnlineGame.Control.Keyboard;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPSerPhone implements Closeable {
    private final Socket socket; //сам сокет

    //потоки для чтения / записи
    private final ObjectInputStream objectReader;
    private final ObjectOutputStream objectWriter;

    public String info; //информация о клиенте (для сервера)
    public int id = 0; //уникальный итендификатор (для сервера), если равен 0, то ещё не загеран

    //закрыт ли сокет (для сервера)
    public boolean closed = false;

    //конструктор для клиента
    public TCPSerPhone(String ip, int port) {
        try {
            this.socket = new Socket(ip, port);
            this.objectWriter = createObjectWriter();
            this.objectReader = createObjectReader();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    //конструктор для сервера
    public TCPSerPhone(ServerSocket server) {
        try {
            this.socket = server.accept();//ожидание клиентов
            this.objectWriter = new ObjectOutputStream(socket.getOutputStream());
            this.objectReader = new ObjectInputStream(socket.getInputStream());
            objectWriter.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    //получение ip адреса в виде строки через сокет
    public String getIp() {
        return socket.getInetAddress().getHostAddress();
    }

    public int writeImage(BufferedImage img) {
        try {
            ImageIO.write(img, "png", objectWriter);
            objectWriter.flush();
            return 0;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public int writeKeyboard(Keyboard keys) {
        return writeObject(keys);
    }

    public BufferedImage readImage() {
        try {
            return ImageIO.read(objectReader);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new BufferedImage(0, 0, 1);
    }

    //отправка сообщения
    public int writeLine(String msg) {
        if (!closed) {
            writeObject(msg);
            return 0;
        }
        return -2; //сокет сервера закрыт
    }

    public int writeObject(Serializable obj) {
        if (!closed) {
            try {
                objectWriter.writeObject(obj);
                objectWriter.flush();
                return 0;
            } catch (IOException e) {
                e.printStackTrace();
                return -1; //попытка отправки оффлайн киенту
            }
        }
        return -2; //сокет сервера закрыт
    }

    public Keyboard readKeyboard() {
        Object a = readObject();
        if (a.equals("-1") || a.equals("-2"))
            return null;
        return (Keyboard) a;
    }

    public Object readObject() {
        if (!closed) {
            try {
                return objectReader.readObject();
            } catch (IOException | ClassNotFoundException e) {
                //e.printStackTrace();
                return "-1"; //попытка отправки оффлайн киенту
            }
        }
        return "-2"; //сокет сервера закрыт
    }


    //считывание сообщения
    public String readLine() {
        if (!closed) {
            Object data = readObject();
            if (data.getClass().equals(String.class))
                return (String) data;
            else
                return "-3"; //Получена не строка
        }
        return "-2"; //сокет сервера закрыт
    }

    //создание потока ввода
    private ObjectInputStream createObjectReader() throws IOException {
        return new ObjectInputStream(socket.getInputStream());
    }

    //создание потока вывода
    private ObjectOutputStream createObjectWriter() throws IOException {
        return new ObjectOutputStream(socket.getOutputStream());
    }

    //чтобы можно было использовать try-catch with resources
    @Override
    public void close() throws IOException {
        closed = true;
        objectReader.close();
        objectWriter.close();
        socket.close();
    }

    //собственный equals (для addReplace и не только на сервере)
    public boolean equals(Object x) {
        if (x == null || x.getClass() != this.getClass())
            return false;
        if (x == this)
            return true;
        TCPSerPhone cur = (TCPSerPhone) x;
        if (cur.socket == this.socket &&
                cur.id == this.id &&
                cur.getIp().equals(this.getIp())) {
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return "TCPSerPhone{" +
                "id=" + id +
                "ip=" + getIp() +
                ", closed=" + closed +
                '}';
    }
}
