package Sockets.UDP;
//модуль для облегчения работы с сокетами

import java.io.Closeable;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.Objects;

public class UDPPhone implements Closeable {
    private final boolean isServer;
    //закрыт ли сокет (для сервера)
    public boolean closed = false;
    private DatagramSocket socket; //сам сокет
    //ридер и райтер
    private DatagramPacket readPack;
    private DatagramPacket writePack;

    private byte[] readBuf, writeBuf;
    private int maxDataLength, PORT, sendPort;
    private InetAddress ip, sendIp;

    //конструктор для клиента
    public UDPPhone(String ip, int PORT, int maxDataLength) {
        try {
            isServer = false;
            socket = new DatagramSocket();
            readBuf = new byte[maxDataLength];
            writeBuf = new byte[maxDataLength];
            this.ip = InetAddress.getByName(ip);
            this.PORT = PORT;
            this.maxDataLength = maxDataLength;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    //конструктор для сервера
    public UDPPhone(int PORT, int maxDataLength) {
        try {
            isServer = true;
            this.socket = new DatagramSocket(PORT);
            readBuf = new byte[maxDataLength];
            writeBuf = new byte[maxDataLength];
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    //получение ip адреса в виде строки через сокет
    public String getIp() {
        return ip.getHostAddress();
    }

    //отправка сообщения
    public int writeLine(String msg) {
        if (!closed) {
            try {
                writeBuf = msg.getBytes();
                if (isServer) {
                    writePack = new DatagramPacket(writeBuf, writeBuf.length, sendIp, sendPort);
                    System.out.println("Writing line on ip: " + sendIp + " and port: " + sendPort);
                } else {
                    writePack = new DatagramPacket(writeBuf, writeBuf.length, ip, PORT);
                    System.out.println("Writing line on ip: " + ip + " and port: " + PORT);
                }

                socket.send(writePack);
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
                readPack = new DatagramPacket(readBuf, readBuf.length);
                socket.receive(readPack);
                sendIp = readPack.getAddress();
                sendPort = readPack.getPort();
                return new String(readPack.getData()).trim();
            } catch (IOException e) {
                return "-1"; //попытка отправки оффлайн киенту
            }
        }
        return "-2"; //сокет сервера закрыт
    }

    //чтобы можно было использовать try-catch with resources
    @Override
    public void close() throws IOException {
        closed = true;
        socket.close();
    }

    //собственный equals (для addReplace и не только на сервере)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UDPPhone)) return false;
        UDPPhone udpPhone = (UDPPhone) o;
        return maxDataLength == udpPhone.maxDataLength && PORT == udpPhone.PORT && closed == udpPhone.closed && socket.equals(udpPhone.socket) && readPack.equals(udpPhone.readPack) && writePack.equals(udpPhone.writePack) && Arrays.equals(readBuf, udpPhone.readBuf) && Arrays.equals(writeBuf, udpPhone.writeBuf) && getIp().equals(udpPhone.getIp());
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(socket, readPack, writePack, maxDataLength, PORT, getIp(), closed);
        result = 31 * result + Arrays.hashCode(readBuf);
        result = 31 * result + Arrays.hashCode(writeBuf);
        return result;
    }
}
