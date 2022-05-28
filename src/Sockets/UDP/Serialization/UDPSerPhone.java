package Sockets.UDP.Serialization;
//модуль для облегчения работы с сокетами

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.Objects;

public class UDPSerPhone implements Closeable {
    private final boolean isServer;
    //закрыт ли сокет (для сервера)
    public boolean closed = false;
    private final DatagramSocket socket; //сам сокет

    private ByteArrayOutputStream bOS;
    private ByteArrayInputStream bIS;
    private ObjectOutputStream oOut;
    private ObjectInputStream oIn;
    //ридер и райтер
    private DatagramPacket readPack;
    private DatagramPacket writePack;

    private byte[] readBuf, writeBuf;
    private int maxDataLength, PORT, sendPort;
    private InetAddress ip, sendIp;

    //конструктор для клиента
    public UDPSerPhone(String ip, int PORT, int maxDataLength) {
        try {
            isServer = false;
            socket = new DatagramSocket();
            bOS = new ByteArrayOutputStream(maxDataLength);
            oOut = new ObjectOutputStream(new BufferedOutputStream(bOS));
            oOut.flush();
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
    public UDPSerPhone(int PORT, int maxDataLength) {
        try {
            isServer = true;
            this.socket = new DatagramSocket(PORT);
            bOS = new ByteArrayOutputStream(maxDataLength);
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
    public int writeObject(Object obj) {
        if (!closed) {
            try {
                oOut.writeObject(obj);
                oOut.flush();
                writeBuf = bOS.toByteArray();
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
                e.printStackTrace();
                return -1; //попытка отправки оффлайн киенту
            }
        }

        return -2; //сокет сервера закрыт
    }

    //считывание сообщения
    public Object readObject() {
        if (!closed) {
            try {
                readPack = new DatagramPacket(readBuf, readBuf.length);
                socket.receive(readPack);
                sendIp = readPack.getAddress();
                sendPort = readPack.getPort();
                bIS = new ByteArrayInputStream(readBuf);
                oIn = new ObjectInputStream(new BufferedInputStream(bIS));
                return oIn.readObject();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
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
        if (!(o instanceof UDPSerPhone)) return false;
        UDPSerPhone that = (UDPSerPhone) o;
        return isServer == that.isServer && closed == that.closed && maxDataLength == that.maxDataLength && PORT == that.PORT && sendPort == that.sendPort && Objects.equals(socket, that.socket) && Objects.equals(readPack, that.readPack) && Objects.equals(writePack, that.writePack) && Arrays.equals(readBuf, that.readBuf) && Arrays.equals(writeBuf, that.writeBuf) && Objects.equals(getIp(), that.getIp()) && Objects.equals(sendIp, that.sendIp);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(isServer, closed, socket, readPack, writePack, maxDataLength, PORT, sendPort, getIp(), sendIp);
        result = 31 * result + Arrays.hashCode(readBuf);
        result = 31 * result + Arrays.hashCode(writeBuf);
        return result;
    }
}
