package com.javabrown.xcapture.screen;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;

public class CaptureFrame extends JFrame {
    private Robot robot;
    private Rectangle selectedAreaRect;

    public CaptureFrame(String title, Robot robot, Rectangle screenRect) {
        super(title);
        this.robot = robot;
        this.selectedAreaRect = screenRect;

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);

        JLabel screenshotLabel = new JLabel();
        getContentPane().add(new JScrollPane(screenshotLabel), BorderLayout.CENTER);

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                getContentPane().validate();
            }
        });

        setLocationRelativeTo(null);
        startContinuousCapture(screenshotLabel);
    }

    private void startContinuousCapture(JLabel screenshotLabel) {
        new Thread(() -> {
            try {
                while (true) {
                    BufferedImage screenshot = robot.createScreenCapture(this.selectedAreaRect);
                    SwingUtilities.invokeLater(() -> screenshotLabel.setIcon(new ImageIcon(screenshot)));
                    Thread.sleep(100);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
}
