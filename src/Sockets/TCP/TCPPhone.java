package Sockets.TCP;

//модуль для облегчения работы с сокетами

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPPhone implements Closeable {
    private final Socket socket; //сам сокет
    //ридер и райтер
    private final BufferedReader reader;
    private final BufferedWriter writer;

    //закрыт ли сокет (для сервера)
    public boolean closed = false;

    //конструктор для клиента
    public TCPPhone(String ip, int port) {
        try {
            this.socket = new Socket(ip, port);
            this.reader = createReader();
            this.writer = createWriter();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    //конструктор для сервера
    public TCPPhone(ServerSocket server) {
        try {
            this.socket = server.accept();//ожидание клиентов
            this.reader = createReader();//создание ридера
            this.writer = createWriter();//создание райтера
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
                writer.write(msg);
                writer.newLine();
                writer.flush();
                return 0;
            } catch (IOException e) {
                return -1; //попытка отправки оффлайн киенту
            }
        }
        return -2; //сокет сервера закрыт
    }

    //считывание сообщения
    public String readLine() {
        if (!closed) {
            try {
                return reader.readLine();
            } catch (IOException e) {
                return "-1"; //попытка отправки оффлайн киенту
            }
        }
        return "-2"; //сокет сервера закрыт
    }

    //создание потока ввода
    private BufferedReader createReader() throws IOException {
        return new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    //создание потока вывода
    private BufferedWriter createWriter() throws IOException {
        return new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
    }

    //чтобы можно было использовать try-catch with resources
    @Override
    public void close() throws IOException {
        writer.close();
        reader.close();
        socket.close();
    }

    //собственный equals (для addReplace и не только на сервере)
    public boolean equals(Object x) {
        if (x == null || x.getClass() != this.getClass())
            return false;
        if (x == this)
            return true;
        TCPPhone cur = (TCPPhone) x;
        if (cur.socket == this.socket &&
                cur.getIp().equals(this.getIp())) {
            return true;
        }
        return false;
    }
}
