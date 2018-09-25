package main.java;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public abstract class SimpleMulticastSocket {
    private int bufferSize = 1024;
    private MulticastSocket multicastSocket;
    private boolean running = true;
    private InetAddress group;

    public SimpleMulticastSocket() {
        try {
            multicastSocket = new MulticastSocket(Protocol.PORT);
            group = InetAddress.getByName("0.0.0.0");
            multicastSocket.joinGroup(group);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void receiveLoop() {
        while(running) {
            String[] msg = receiveMsg().split(Protocol.SPLITTER);
            processMsg(msg);
        }
    }

    abstract void processMsg(String[] msg);

    protected void sendMsg(byte[] msg) {
        DatagramPacket packet = new DatagramPacket(msg, msg.length, group, Protocol.PORT);
        try {
            multicastSocket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected String receiveMsg() {
        byte[] msg = new byte[bufferSize];
        DatagramPacket packet = new DatagramPacket(msg, msg.length);
        try {
            multicastSocket.receive(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new String(packet.getData(), 0);
    }
}
