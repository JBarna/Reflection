package com.reflection;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamEvent;
import com.github.sarxos.webcam.WebcamListener;
import com.github.sarxos.webcam.WebcamPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

/**
 * Created by JBarna on 11/29/2016.
 */
public class Display {

    private Webcam webcam;
    private InstantStart instantStart;
    private ArrayList<DisplayWindow> displays;
    private Timer timer;

    public Display() {
        init();
    }

    public void start() {

        // check number of monitors / create classes
        updateDisplays();
        instantStart.start();

        for (DisplayWindow d : displays) {
            d.start();
        }

        timer.restart();
    }

    private void init() {

        instantStart = new InstantStart();
        displays = new ArrayList<DisplayWindow>();
        timer = new Timer(Main.SHOW, new TimerListener());

        // should be laptop webcam
        webcam = Webcam.getDefault();

        // the last view size is the largest.
        webcam.setViewSize(webcam.getViewSizes()[webcam.getViewSizes().length - 1]);
    }

    private void updateDisplays() {

        GraphicsDevice[] screenDevices = GraphicsEnvironment
                .getLocalGraphicsEnvironment()
                .getScreenDevices();

        System.out.println("Screen devices length" + screenDevices.length);

        // we check to see if any monitors / displays have been added or removed
        // doesn't work if someone removes a display and then attaches another
        // but it's good enough
        if (screenDevices.length != displays.size()) {
            displays.clear();
            System.out.println("Adding devices");
            for (GraphicsDevice d : screenDevices) {
                System.out.println("sd length " + screenDevices.length + ". Display length " + displays.size());
                displays.add(new DisplayWindow(d));
            }

        }
    }

    private class DisplayWindow extends JWindow {

        private WebcamPanel wPanel;

        public DisplayWindow(GraphicsDevice gD) {
            buildGUI(gD.getDefaultConfiguration().getBounds());
        }

        private void buildGUI(Rectangle bounds) {

            this.setLayout(new BorderLayout());

            wPanel = new WebcamPanel(webcam, false);
            wPanel.setFitArea(true);

            final JButton shutdown = new JButton("SHUTDOWN");

            shutdown.setPreferredSize(new Dimension((int) (bounds.getWidth() / 4), (int) bounds.getHeight()));
            shutdown.setForeground(Color.WHITE);
            shutdown.setBackground(Color.BLACK);
            shutdown.setBorderPainted(false);

            shutdown.addMouseListener(new MouseListener() {

                public void mouseClicked(MouseEvent e) {
                    shutdown.setText("WE'RE GOING DOWN!");
                    Main.shutdown();
                }

                public void mousePressed(MouseEvent e) {
                }

                public void mouseReleased(MouseEvent e) {
                }

                public void mouseEntered(MouseEvent e) {
                    shutdown.setBackground(Color.DARK_GRAY);
                }

                public void mouseExited(MouseEvent e) {
                    shutdown.setBackground(Color.BLACK);
                }
            });

            this.getContentPane().add(wPanel, BorderLayout.CENTER);
            this.getContentPane().add(shutdown, BorderLayout.EAST);

            this.setLocation(bounds.x, bounds.y);
            this.setSize((int) bounds.getWidth(), (int) bounds.getHeight());

            this.setAlwaysOnTop(true);
        }

        public void start() {
            wPanel.start();
        }

        public void display() {
            this.setVisible(true);
        }

        public void stop() {
            wPanel.stop();
            this.setVisible(false);
        }
    }

    private class InstantStart implements WebcamListener {

        public void start() {
            webcam.addWebcamListener(this);
        }

        public void webcamOpen(WebcamEvent webcamEvent) {
        }

        public void webcamClosed(WebcamEvent webcamEvent) {

        }

        public void webcamDisposed(WebcamEvent webcamEvent) {

        }

        public void webcamImageObtained(WebcamEvent webcamEvent) {
            for (DisplayWindow d : displays) {
                d.display();
            }
            webcam.removeWebcamListener(this);
        }
    }

    private class TimerListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            for (DisplayWindow d : displays) {
                d.stop();
            }
            timer.stop();
        }
    }
}
