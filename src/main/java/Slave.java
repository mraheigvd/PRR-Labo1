package main.java;

import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.Random;

public class Slave extends SimpleUDP {
    private LocalClock localClock = new LocalClock();
    private int syncTime = 0;
    private int syncId = 0;
    private int requestId = 0;
    private boolean delayRequestStarted = false;

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
        Thread t1 = new Thread(() -> {//sync loop
            while(isRunning()) processSync();
        });

        t1.start();
    }

    /**
     * precess "sync" and "follow up" messages to synchronise clock with master
     */
    private void processSync() {
        String msg = processDatagramToString(MCReceiveMsg());
        switch (msg) {
            case Protocol.SYNC:
                syncTime = localClock.getUncorrectedTime();
                syncId = ByteBuffer.wrap(MCReceiveMsg().getData()).getInt();//id
                break;
            case Protocol.FOLLOW_UP:
                int rcvSynTime = ByteBuffer.wrap(MCReceiveMsg().getData()).getInt();//time
                int rcvSynId = ByteBuffer.wrap(MCReceiveMsg().getData()).getInt();//id
                if(rcvSynId == syncId) localClock.setEcart(rcvSynTime - syncTime);

                if(!delayRequestStarted) {
                    Thread t2 = new Thread(() -> {//delay loop
                        Random random = new Random();
                        while(isRunning()) {
                            try {
                                Thread.sleep((4 * Protocol.K) + random.nextInt((60 - 4) * Protocol.K));
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                            sendDelayRequest();
                        }
                    });
                    t2.start();
                    delayRequestStarted = true;
                }
                break;
        }
    }

    /**
     * send a delay request and wait the response to calculate the delay (and correct it)
     */
    private void sendDelayRequest() {
        try {
            ByteBuffer bbId = ByteBuffer.allocate(4);
            byte[][] messages = {Protocol.DELAY_REQUEST.getBytes(), bbId.putInt(++requestId).array()};//request message
            int requestTime = localClock.getUncorrectedTime();//save current time
            DGSendMsg(messages, InetAddress.getByName(Protocol.DELAY_ADDRESS), Protocol.REQ_PORT);//send

            String strings = processDatagramToString(DGReceiveMsg());//receive response
            int responseTime = localClock.getUncorrectedTime();//save current time
            int receiveTime = ByteBuffer.wrap(DGReceiveMsg().getData()).getInt();//time
            int receiveId = ByteBuffer.wrap(DGReceiveMsg().getData()).getInt();//id

            if(receiveId == requestId) localClock.setDelai((responseTime - requestTime) / 2);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public LocalClock getLocalClock() {
        return localClock;
    }

    public static void main(String[] args) throws InterruptedException {
        ClockFrame clockFrame = new ClockFrame(new Slave().getLocalClock(), "slave");
        while(true) {
            clockFrame.update();
            Thread.sleep(500);
        }
    }
}
