package main.java;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class Master {
    private Clock clock;
    private long id;
    private int bufferSize = 1024;
    private long sleepTime = 3000;
    private MulticastSocket multicastSocket;

    private String address = "0.0.0.0";
    private InetAddress group = InetAddress.getByName("");

    public Master() {
        try {
            multicastSocket = new MulticastSocket(Protocol.PORT);
            multicastSocket.joinGroup(InetAddress.getByName(address));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void syncLoop() {
        byte[] msg = new byte[bufferSize];
        while(true) {
            id++;
            msg = (Protocol.SYNC + Protocol.SPLITTER + id).getBytes();
            long time = clock.getTime();
            // send
            sendMsg(msg);
            msg = (Protocol.FOLLOW_UP + Protocol.SPLITTER + time + Protocol.SPLITTER + id).getBytes();
            // send
            sendMsg(msg);
            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void sendMsg(byte[] msg) {
        DatagramPacket packet = new DatagramPacket(msg, msg.length, address, Protocol.PORT)
    }
}
