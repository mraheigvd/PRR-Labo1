import main.java.ClockFrame;
import main.java.Master;
import main.java.Slave;

import java.util.ArrayList;

public class Main {
    static ArrayList<ClockFrame> clockFrames = new ArrayList<>();
    static int sleepTime = 3000;

    public static void main(String[] args) {
        clockFrames.add(new ClockFrame(new Master(sleepTime).getClock(), "master"));
        createSlave("slave 1");
        createSlave("slave 2");
        createSlave("slave 3");
        createSlave("slave 4");
        createSlave("slave 5");

        while(true) {
            for(ClockFrame clockFrame : clockFrames) clockFrame.update();
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private static void createSlave(String name) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        clockFrames.add(new ClockFrame(new Slave(4 * sleepTime).getLocalClock(), name));
    }
}
