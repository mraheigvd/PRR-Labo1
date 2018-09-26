package main.java;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class ClockFrame extends JFrame {
    private JLabel label;
    private Clock clock;

    public ClockFrame(Clock clock, String name) {
        this.clock = clock;
        setTitle(name);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        label = new JLabel(String.valueOf(clock.toString()));
        add(label);
        setSize(250, 70);
        setVisible(true);

        // todo prettier
        if(clock instanceof LocalClock) {
            setSize(350, 70);
            setLayout(new FlowLayout());

            JButton timeButton = new JButton("reset Time");
            timeButton.addActionListener(new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    clock.setTime(0);
                }
            });
            add(timeButton);

            JButton corrButton = new JButton("reset Corr");
            corrButton.addActionListener(new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    ((LocalClock) clock).setDelai(0);
                    ((LocalClock) clock).setEcart(0);
                }
            });
            add(corrButton);
        }
    }

    public void update() {
        clock.increaseTime();
        label.setText(String.valueOf(clock.toString()));
    }
}
