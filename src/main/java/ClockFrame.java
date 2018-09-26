package main.java;

import javax.swing.*;

public class ClockFrame extends JFrame {
    private JLabel label;
    private Clock clock;

    public ClockFrame(Clock clock, String name) {
        this.clock = clock;
        setTitle(name);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        label = new JLabel(String.valueOf(clock.getTime()));
        add(label);
        setSize(250, 100);
        setVisible(true);
    }

    public void update() {
        clock.increaseTime();
        label.setText(String.valueOf(clock.getTime()));
    }
}