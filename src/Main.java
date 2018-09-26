import main.java.ClockFrame;
import main.java.Master;
import main.java.Slave;

import java.util.ArrayList;
import java.util.logging.Logger;

public class Main {

    public static void main(String[] args) {
        ArrayList<ClockFrame> clockFrames = new ArrayList<>();

        Master master = new Master();
        clockFrames.add(new ClockFrame(master.getClock(), "master"));
        Slave slave1 = new Slave();
        clockFrames.add(new ClockFrame(slave1.getLocalClock(), "slave 1"));
        slave1.getLocalClock().setTime(-10);

        while(true) {
            for(ClockFrame clockFrame : clockFrames) clockFrame.update();
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
