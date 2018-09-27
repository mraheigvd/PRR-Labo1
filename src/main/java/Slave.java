package main.java;

import java.io.IOException;
import java.net.*;

public class Slave extends SimpleUDP {
    private LocalClock localClock = new LocalClock();
    private int synTime = 0;
    private int reqTime = 0;
    private int resTime = 0;
    private int synId = 0;
    private int reqId = 0;
    private long sleepTime;

    public Slave(int sleepTime) {
        this.sleepTime = sleepTime;
    }

    @Override
    public DatagramSocket initDatagramSocket() {
        try {
            return new DatagramSocket(Protocol.RES_PORT);
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
                while(true) processSync();
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
                    sendDelayRequest();
                }
            }
        });

        t1.start();
        t2.start();
    }

    private void processSync() {
        String[] msg = processDatagram(MCReceiveMsg()).split(Protocol.SPLITTER);
        switch (msg[0]) {
            case Protocol.SYNC:
                synTime = localClock.getUncorrectedTime();
                synId = Integer.valueOf(msg[1]);
                break;
            case Protocol.FOLLOW_UP:
                int rcvSynTime = Integer.valueOf(msg[1]);
                int rcvSynId = Integer.valueOf(msg[2]);
                if(rcvSynId == synId) localClock.setEcart(rcvSynTime - synTime);//todo stock id+time in list or map
                break;
        }
    }

    private void sendDelayRequest() {
        try {
            byte[] buffer = (Protocol.DELAY_REQUEST + Protocol.SPLITTER + ++reqId).getBytes();
            reqTime = localClock.getUncorrectedTime();
            DGSendMsg(buffer, InetAddress.getByName(Protocol.DELAY_ADDRESS), Protocol.REQ_PORT);
            String[] strings = processDatagram(DGReceiveMsg()).split(Protocol.SPLITTER);
            resTime = localClock.getUncorrectedTime();
            int rcvTime = Integer.valueOf(strings[1]);
            int rcvReqId = Integer.valueOf(strings[2]);
            if(rcvReqId == reqId) localClock.setDelai((resTime - reqTime) / 2);//todo stock id+time in list or map
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public LocalClock getLocalClock() {
        return localClock;
    }

    public static void main(String[] args) throws InterruptedException {
        ClockFrame clockFrame = new ClockFrame(new Slave(12000).getLocalClock(), "slave");
        while(true) {
            clockFrame.update();
            Thread.sleep(500);
        }
    }
}
