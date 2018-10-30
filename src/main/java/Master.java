package main.java;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.nio.ByteBuffer;

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
        Thread t1 = new Thread(() -> {
            while(isRunning()) processDelayRequest();
        });

        Thread t2 = new Thread(() -> {
            while(isRunning()) {
                try {
                    Thread.sleep(sleepTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                sendSync();
            }
        });

        t1.start();
        t2.start();
    }

    /**
     * send a "sync" message followed by a "follow up" message to let slaves synchronise on master clock
     */
    private void sendSync() {
        id++;
        ByteBuffer bbId = ByteBuffer.allocate(4);
        ByteBuffer bbTime = ByteBuffer.allocate(4);
        byte[][] messages = {Protocol.SYNC.getBytes(), bbId.putInt(id).array()};//sync message
        int time = clock.getTime();//save current time
        MCSendMsg(messages);

        messages = new byte[3][];//follow up message
        messages[0] = Protocol.FOLLOW_UP.getBytes();
        messages[1] = bbTime.putInt(time).array();
        messages[2] = bbId.array();//
        MCSendMsg(messages);

        try {
            Thread.sleep(sleepTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * response to a delay request with a delay response
     */
    private void processDelayRequest() {
        DatagramPacket packet = DGReceiveMsg();
        String strings = processDatagramToString(packet);
        if(strings.equals(Protocol.DELAY_REQUEST)) {
            ByteBuffer bbTime = ByteBuffer.allocate(4);
            ByteBuffer bbId = ByteBuffer.allocate(4);

            byte[][] message = {
                    Protocol.DELAY_RESPONSE.getBytes(),
                    bbTime.putInt(clock.getTime()).array(),
                    bbId.putInt(ByteBuffer.wrap(DGReceiveMsg().getData()).getInt()).array()
            };
            DGSendMsg(message, packet.getAddress(), Protocol.RES_PORT);
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
