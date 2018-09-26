package main.java;

public class Slave extends SimpleMulticastSocket {
    private LocalClock localClock = new LocalClock();
    private int bufferSize = 1024;
    private int id = 0;
    private int time = 0;
    private int time2 = 0;//todo change name
    private int delayId = 0;//todo change name
    private long sleepTime = 12000;

    @Override
    public void processMsg(String[] msg) {
        switch (msg[0]) {
            case Protocol.SYNC:
                time = localClock.getTime();

                System.out.println(msg[1]);
                System.out.println(msg[1].length());

                id = Integer.valueOf(msg[1]);
                break;
            case Protocol.FOLLOW_UP:
                int rcvTime = Integer.valueOf(msg[1]);
                int rcvId = Integer.valueOf(msg[2]);
                if(rcvId == id) localClock.setEcart(rcvTime - time);//todo stock id+time in list or map
                break;
            case Protocol.DELAY_RESPONSE:
                int rcvTime2 = Integer.valueOf(msg[1]);//todo change name
                int rcvId2 = Integer.valueOf(msg[2]);//todo change name
                if(rcvId2 == delayId) localClock.setDelai((localClock.getCorrectedTime() - time2) / 2);//todo stock id+time in list or map
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
        byte[] msg = (Protocol.DELAY_REQUEST + Protocol.SPLITTER + delayId++).getBytes();
        time2 = localClock.getCorrectedTime();
        sendMsg(msg);
    }

    public LocalClock getLocalClock() {
        return localClock;
    }
}
