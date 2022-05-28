package Sockets.TCP.Serialization;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPSerPhone implements Closeable {
    private final Socket socket; //сам сокет

    //потоки для чтения / записи
    private final ObjectInputStream objectReader;
    private final ObjectOutputStream objectWriter;

    //закрыт ли сокет (для сервера)
    public boolean closed = false;

    //конструктор для клиента
    public TCPSerPhone(String ip, int port) {
        try {
            this.socket = new Socket(ip, port);
            this.objectWriter = new ObjectOutputStream(socket.getOutputStream());
            this.objectReader = new ObjectInputStream(socket.getInputStream());
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

    //отправка сообщения
    public int writeLine(String msg) {
        if (!closed) {
            try {
                objectWriter.writeChars(msg + "\r\n");
                objectWriter.flush();
                return 0;
            } catch (IOException e) {
                return -1; //попытка отправки оффлайн киенту
            }
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
                return -1; //попытка отправки оффлайн киенту
            }
        }
        return -2; //сокет сервера закрыт
    }

    public Object readObject() {
        if (!closed) {
            try {
                return objectReader.readObject();
            } catch (IOException | ClassNotFoundException e) {
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
                cur.getIp().equals(this.getIp())) {
            return true;
        }
        return false;
    }
}
