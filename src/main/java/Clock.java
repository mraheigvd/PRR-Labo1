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

    @Override
    public String toString() {
        return String.valueOf(time);
    }
}
