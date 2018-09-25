package main.java;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class Slave {
    private LocalClock localClock;
    private int bufferSize = 1024;
    private MulticastSocket multicastSocket;
    private boolean running = true;

    private InetAddress group;
    private int id;
    private int time;

    public Slave() {
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
            switch (msg[0]) {
                case Protocol.SYNC:
                    time = localClock.getTime();
                    id = Integer.valueOf(msg[1]);
                    break;
            }
        }
    }

    private String receiveMsg() {
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
