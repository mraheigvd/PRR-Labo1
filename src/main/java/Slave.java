package main.java;

import java.io.IOException;
import java.net.*;

public class Slave extends SimpleUDP {
    private LocalClock localClock = new LocalClock();
    private int syncTime = 0;
    private int syncId = 0;
    private int requestId = 0;
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
        Thread t1 = new Thread(() -> {
            while(isRunning()) processSync();
        });

        Thread t2 = new Thread(() -> {
            while(isRunning()) {
                try {
                    Thread.sleep(sleepTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                sendDelayRequest();
            }
        });

        t1.start();
        t2.start();
    }

    /**
     * precess "sync" and "follow up" messages to synchronise clock with master
     */
    private void processSync() {
        String[] msg = processDatagram(MCReceiveMsg()).split(Protocol.SPLITTER);
        switch (msg[0]) {
            case Protocol.SYNC:
                syncTime = localClock.getUncorrectedTime();
                syncId = Integer.valueOf(msg[1]);
                break;
            case Protocol.FOLLOW_UP:
                int rcvSynTime = Integer.valueOf(msg[1]);
                int rcvSynId = Integer.valueOf(msg[2]);
                if(rcvSynId == syncId) localClock.setEcart(rcvSynTime - syncTime);//todo stock id+time in list or map
                break;
        }
    }

    /**
     * send a delay request and wait the response to calculate the delay (and correct it)
     */
    private void sendDelayRequest() {
        try {
            byte[] buffer = (Protocol.DELAY_REQUEST + Protocol.SPLITTER + ++requestId).getBytes();
            int requestTime = localClock.getUncorrectedTime();
            DGSendMsg(buffer, InetAddress.getByName(Protocol.DELAY_ADDRESS), Protocol.REQ_PORT);
            String[] strings = processDatagram(DGReceiveMsg()).split(Protocol.SPLITTER);
            int responseTime = localClock.getUncorrectedTime();
            //int receiveTime = Integer.valueOf(strings[1]);
            int receiveId = Integer.valueOf(strings[2]);
            if(receiveId == requestId) localClock.setDelai((responseTime - requestTime) / 2);//todo stock id+time in list or map
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
