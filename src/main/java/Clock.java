package main.java;

import javax.swing.*;

public class Clock {
    private int time;

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public void increaseTime() {
        time++;
    }

    public void display(String name) {

    }

    private class ClockFrame extends JFrame {
        private JLabel label;

        public ClockFrame(String name) {
            setName(name);
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            label = new JLabel(String.valueOf(Clock.this.getTime()));
            add(label);
            setSize(20, 20);
            setVisible(true);
        }

        public void update() {
            increaseTime();
            label.setText(String.valueOf(Clock.this.getTime()));
        }
    }
}
