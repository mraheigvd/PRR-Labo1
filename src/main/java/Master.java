package main.java;

public class Master extends SimpleMulticastSocket {
    private Clock clock = new Clock();
    private int id = 0;
    private long sleepTime = 3000;

    @Override
    public void processMsg(String[] msg) {
        switch(msg[0]) {
            case Protocol.DELAY_REQUEST:
                sendMsg((Protocol.DELAY_RESPONSE + Protocol.SPLITTER + clock.getTime() + Protocol.SPLITTER + msg[1]).getBytes());
                break;
        }

    }

    @Override
    public void start() {
        Thread t1 = new Thread(this::receiveLoop);
        Thread t2 = new Thread(new Runnable() {
            @Override
            public void run() {
                while(true) {
                    try {
                        Thread.sleep(sleepTime);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    sync();
                }
            }
        });

        t1.start();
        t2.start();
    }

    public void sync() {
        id++;
        byte[] msg = (Protocol.SYNC + Protocol.SPLITTER + id).getBytes();
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

    public Clock getClock() {
        return clock;
    }
}
