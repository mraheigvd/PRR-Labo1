package main.java;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class Master {
    private Clock clock;
    private int id;
    private int bufferSize = 1024;
    private long sleepTime = 3000;
    private MulticastSocket multicastSocket;
    private boolean running = true;

    private InetAddress group;

    public Master() {
        try {
            multicastSocket = new MulticastSocket(Protocol.PORT);
            group = InetAddress.getByName("0.0.0.0");
            multicastSocket.joinGroup(group);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void syncLoop() {
        byte[] msg = new byte[bufferSize];
        while(running) {
            id++;
            msg = (Protocol.SYNC + Protocol.SPLITTER + id).getBytes();
            int time = clock.getTime();
            sendMsg(msg);
            msg = (Protocol.FOLLOW_UP + Protocol.SPLITTER + time + Protocol.SPLITTER + id).getBytes();
            sendMsg(msg);
            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void sendMsg(byte[] msg) {
        DatagramPacket packet = new DatagramPacket(msg, msg.length, group, Protocol.PORT);
        try {
            multicastSocket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
