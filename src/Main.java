import main.java.ClockFrame;
import main.java.Master;
import main.java.Slave;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        ClockFrame clockFrameMaster = new ClockFrame(new Master().getClock(), "master");
        ClockFrame clockFrameSlave = new ClockFrame(new Slave().getLocalClock(), "slave");
        while(true) {
            clockFrameMaster.update();
            clockFrameSlave.update();
            Thread.sleep(500);
        }
    }
}
