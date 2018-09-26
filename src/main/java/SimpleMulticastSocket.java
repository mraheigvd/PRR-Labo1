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

            String info = "split : ";
            for(String s : msg) info += s + "-";
            LOG.info(info);

            processMsg(msg);
        }
    }

    abstract public void processMsg(String[] msg);

    abstract public void start();

    protected void sendMsg(byte[] msg) {

        String info = "send : ";
        info += new String(msg);
        LOG.info(info);

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

        String info;
        info = "receive : ";
        info += new String(packet.getData());
        LOG.info(info);

        //return new String(packet.getData(), 0);
        String temp =  new String(packet.getData());
        temp = temp.substring(0, temp.indexOf('\0'));

        //return new String(packet.getData(), 0, 1);
        return temp;
    }
}
