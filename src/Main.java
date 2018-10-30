import main.java.ClockFrame;
import main.java.Master;
import main.java.Slave;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        ClockFrame clockFrameMaster = new ClockFrame(new Master(3000).getClock(), "master");
        ClockFrame clockFrameSlave = new ClockFrame(new Slave(12000).getLocalClock(), "slave");
        while(true) {
            clockFrameMaster.update();
            clockFrameSlave.update();
            Thread.sleep(500);
        }
    }
}
