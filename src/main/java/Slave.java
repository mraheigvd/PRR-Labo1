package main.java;

public class Slave extends SimpleMulticastSocket {
    private LocalClock localClock;
    private int bufferSize = 1024;
    private int id;
    private int time;
    private int time2;//todo change name
    private int delayId;//todo change name

    @Override
    void processMsg(String[] msg) {
        switch (msg[0]) {
            case Protocol.SYNC:
                time = localClock.getTime();
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

    private void delayRequest() {
        byte[] msg = (Protocol.DELAY_REQUEST + Protocol.SPLITTER + delayId++).getBytes();
        time2 = localClock.getCorrectedTime();
        sendMsg(msg);
    }
}
