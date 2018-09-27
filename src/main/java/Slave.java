package main.java;

public class Slave extends SimpleMulticastSocket {
    private LocalClock localClock = new LocalClock();
    private int synTime = 0;
    private int reqTime = 0;
    private int resTime = 0;
    private int synId = 0;
    private int reqId = 0;
    private long sleepTime = 12000;

    @Override
    public void processMsg(String[] msg) {
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
            case Protocol.DELAY_RESPONSE:
                resTime = localClock.getUncorrectedTime();
                //int rcvTime = Integer.valueOf(msg[1]);
                int rcvReqId = Integer.valueOf(msg[2]);
                if(rcvReqId == reqId) localClock.setDelai((resTime - reqTime) / 2);//todo stock id+time in list or map
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
                    delayRequest();
                }
            }
        });

        t1.start();
        t2.start();
    }

    private void delayRequest() {
        byte[] msg = (Protocol.DELAY_REQUEST + Protocol.SPLITTER + ++reqId).getBytes();
        reqTime = localClock.getUncorrectedTime();
        sendMsg(msg);
    }

    public LocalClock getLocalClock() {
        return localClock;
    }
}
