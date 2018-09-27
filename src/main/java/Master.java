package main.java;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class Master extends SimpleUDP {
    private Clock clock = new Clock();
    private int id = 0;
    private long sleepTime;

    public Master(int sleepTime) {
        this.sleepTime = sleepTime;
    }

    @Override
    public DatagramSocket initDatagramSocket() {
        try {
            return new DatagramSocket(Protocol.REQ_PORT);
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void start() {
        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                while(true) processDelayRequest();
            }
        });

        Thread t2 = new Thread(new Runnable() {
            @Override
            public void run() {
                while(true) {
                    try {
                        Thread.sleep(sleepTime);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    sendSync();
                }
            }
        });

        t1.start();
        t2.start();
    }

    private void sendSync() {
        id++;
        byte[] msg = (Protocol.SYNC + Protocol.SPLITTER + id).getBytes();
        int time = clock.getTime();
        MCSendMsg(msg);
        msg = (Protocol.FOLLOW_UP + Protocol.SPLITTER + time + Protocol.SPLITTER + id).getBytes();
        MCSendMsg(msg);
        try {
            Thread.sleep(sleepTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void processDelayRequest() {
        DatagramPacket packet = DGReceiveMsg();
        String msg = new String(packet.getData());
        String[] strings = msg.substring(0, msg.indexOf('\0')).split(Protocol.SPLITTER);
        if(strings[0].equals(Protocol.DELAY_REQUEST)) {
            byte[] buffer = (Protocol.DELAY_RESPONSE + Protocol.SPLITTER + clock.getTime() + Protocol.SPLITTER + strings[1]).getBytes();
            DGSendMsg(buffer, packet.getAddress(), Protocol.RES_PORT);
        }
    }

    public Clock getClock() {
        return clock;
    }

    public static void main(String[] args) throws InterruptedException {
        ClockFrame clockFrame = new ClockFrame(new Master(3000).getClock(), "master");
        while(true) {
            clockFrame.update();
            Thread.sleep(500);
        }
    }
}
