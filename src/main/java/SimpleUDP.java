package main.java;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.logging.Logger;

public abstract class SimpleUDP {
    public static final Logger LOG = Logger.getLogger(SimpleUDP.class.getName());

    private int bufferSize = 1024;
    private MulticastSocket multicastSocket;
    private DatagramSocket datagramSocket;
    private InetAddress group;

    public SimpleUDP() {
        try {
            multicastSocket = new MulticastSocket(Protocol.SYNC_PORT);
            group = InetAddress.getByName(Protocol.SYNC_ADDRESS);
            multicastSocket.joinGroup(group);
            datagramSocket = initDatagramSocket();
            start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public abstract DatagramSocket initDatagramSocket();

    abstract public void start();

    protected void MCSendMsg(byte[] msg) {
        DatagramPacket packet = new DatagramPacket(msg, msg.length, group, Protocol.SYNC_PORT);
        try {
            multicastSocket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected DatagramPacket MCReceiveMsg() {
        return receiveMsg(multicastSocket);
    }

    protected void DGSendMsg(byte[] msg, InetAddress address, int port) {
        DatagramPacket packet = new DatagramPacket(msg, msg.length, address, port);
        try {
            datagramSocket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected DatagramPacket DGReceiveMsg() {
        return receiveMsg(datagramSocket);
    }

    public static String processDatagram(DatagramPacket packet) {
        String temp =  new String(packet.getData());
        return temp.substring(0, temp.indexOf('\0'));
    }

    private DatagramPacket receiveMsg(DatagramSocket socket) {
        byte[] msg = new byte[bufferSize];
        DatagramPacket packet = new DatagramPacket(msg, msg.length);
        try {
            socket.receive(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        LOG.info(new String(packet.getData()));

        return packet;
    }
}
