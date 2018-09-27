package main.java;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.logging.Logger;

public abstract class SimpleMulticastSocket {
    private int bufferSize = 1024;
    private MulticastSocket multicastSocket;
    private boolean running = true;
    private InetAddress group;

    public SimpleMulticastSocket() {
        try {
            multicastSocket = new MulticastSocket(Protocol.PORT);
            group = InetAddress.getByName("224.0.0.0");
            multicastSocket.joinGroup(group);
            start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static final Logger LOG = Logger.getLogger(SimpleMulticastSocket.class.getName());

    protected void receiveLoop() {
        while(running) {
            String[] msg = receiveMsg().split(Protocol.SPLITTER);
            processMsg(msg);
        }
    }

    abstract public void processMsg(String[] msg);

    abstract public void start();

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

        /*try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/

        String temp =  new String(packet.getData());
        LOG.info(temp);
        return temp.substring(0, temp.indexOf('\0'));
    }
}
