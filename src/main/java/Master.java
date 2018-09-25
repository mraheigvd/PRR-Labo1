package main.java;

public class Master extends SimpleMulticastSocket {
    private Clock clock;
    private int id;
    private int bufferSize = 1024;
    private long sleepTime = 3000;
    private boolean running = true;

    @Override
    void processMsg(String[] msg) {
        switch(msg[0]) {
            case Protocol.DELAY_REQUEST:
                sendMsg((Protocol.DELAY_RESPONSE + Protocol.SPLITTER + clock.getTime() + Protocol.SPLITTER + msg[1]).getBytes());
                break;
        }

    }

    public void syncLoop() {
        byte[] msg = new byte[bufferSize];
        while(running) {
            id++;
            msg = (Protocol.SYNC + Protocol.SPLITTER + id).getBytes();
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
    }
}
