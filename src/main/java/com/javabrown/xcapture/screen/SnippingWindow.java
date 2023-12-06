package com.javabrown.xcapture.screen;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class SnippingWindow extends JWindow {
    private Robot robot;
    private CaptureFrame recordingFrame;
    private SnippingPanel selectionPanel;

    public SnippingWindow(Robot robot) {
        this.robot = robot;
        BufferedImage initialScreenshot =
                robot.createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));

        setSize(Toolkit.getDefaultToolkit().getScreenSize());

        JLabel instructionLabel = new JLabel("Select the area to capture");
        instructionLabel.setHorizontalAlignment(JLabel.CENTER);
        getContentPane().add(instructionLabel, BorderLayout.NORTH);
        this.selectionPanel = new SnippingPanel(initialScreenshot, () -> launchRecordingFrame());
        getContentPane().add(this.selectionPanel, BorderLayout.CENTER);
    }

    /**
     * ON SNIPPING PANEL SELECTION COMPLETE
     */
    private void launchRecordingFrame() {
        this.selectionPanel.getSelectedRectengularSnippingArea();
        this.recordingFrame = new CaptureFrame("Recording Screen", this.robot,
                this.selectionPanel.getSelectedRectengularSnippingArea());
        this.recordingFrame.setVisible(true);
        this.dispose();
    }
}