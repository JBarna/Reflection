package com.reflection;

import javax.swing.*;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class Main {

    // static variables
    public static int WAIT = 20 * 60 * 1000;
    public static int SHOW = 40 * 1000;

    private Display display;
    private java.util.Timer timer;

    public Main() {

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                display = new Display();
            }
        });

        timer = new Timer();

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        display.start();
                    }
                });
            }
        }, WAIT, WAIT);
    }

    public static void main(String[] args) {
        new Main();
    }

    public static void shutdown() {
        String shutdownCommand = "";
        String operatingSystem = System.getProperty("os.name").toLowerCase();

        if (operatingSystem.contains("nix") || operatingSystem.contains("mac")) {
            shutdownCommand = "shutdown -h now";
        } else if (operatingSystem.contains("win")) {
            shutdownCommand = "shutdown.exe -s -t 0";
        }

        if (shutdownCommand.length() > 0) {
            try {
                Runtime.getRuntime().exec(shutdownCommand);
            } catch (IOException e) {
                System.err.println("Could not issue shutdown command");
            }
        }
        System.exit(0);
    }


}
