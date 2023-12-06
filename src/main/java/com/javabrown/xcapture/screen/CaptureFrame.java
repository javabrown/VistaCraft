package com.javabrown.xcapture.screen;

import com.javabrown.core.utils.VideoConverter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class CaptureFrame extends JFrame {
    private Robot robot;
    private Rectangle selectedAreaRect;
    private JLabel screenshotLabel;

    private final AtomicBoolean isRecording = new AtomicBoolean(false);
    private final List<BufferedImage> recordedFrames = new CopyOnWriteArrayList<>();

    private long recordingStartTime;

    public CaptureFrame(String title, Robot robot, Rectangle screenRect) {
        super(title);
        this.robot = robot;
        this.selectedAreaRect = screenRect;

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(true); // Allow resizing

        // Set initial size based on the provided rectangle
        setSize(screenRect.width, screenRect.height);

        screenshotLabel = new JLabel();
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(new JScrollPane(screenshotLabel), BorderLayout.CENTER);
        getContentPane().add(new CaptureFrameControlPanel(), BorderLayout.NORTH);

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                // Ensure the frame size doesn't exceed the specified rectangle size
                int maxWidth = selectedAreaRect.width;
                int maxHeight = selectedAreaRect.height;

                int newWidth = getWidth() > maxWidth ? maxWidth : getWidth();
                int newHeight = getHeight() > maxHeight ? maxHeight : getHeight();

                setSize(new Dimension(newWidth, newHeight));
                getContentPane().validate();
            }
        });

        setLocationRelativeTo(null);
        startContinuousCapture();
    }

    private void startContinuousCapture() {
        new Thread(() -> {
            try {
                while (true) {
                    BufferedImage screenshot = robot.createScreenCapture(this.selectedAreaRect);
                    SwingUtilities.invokeLater(() -> {
                        screenshotLabel.setIcon(new ImageIcon(screenshot));
                        if (isRecording.get()) {
                            recordedFrames.add(screenshot);
                        }
                    });
                    Thread.sleep(100);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private class CaptureFrameControlPanel extends JPanel {
        private JLabel durationLabel;

        public CaptureFrameControlPanel() {
            setLayout(new FlowLayout());

            JButton startRecordingButton = new JButton("Start Recording");
            JButton stopRecordingButton = new JButton("Stop Recording");
            durationLabel = new JLabel("Recording Duration: 00:00:00");

            startRecordingButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    startRecording();
                }
            });

            stopRecordingButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    stopRecording();
                }
            });

            add(startRecordingButton);
            add(stopRecordingButton);
            add(durationLabel);
        }

        private void startRecording() {
            isRecording.set(true);
            recordedFrames.clear(); // Clear existing frames when starting a new recording
            recordingStartTime = System.currentTimeMillis();
            updateDurationLabel();
        }

        private void stopRecording() {
            isRecording.set(false);
            saveVideo();
        }

        private void saveVideo() {
            // Check if there are frames to save
            if (recordedFrames.isEmpty()) {
                System.out.println("No frames to save.");
                return;
            }

            try {
                // Set the output file path (adjust as needed)
                String outputFilePath = "output.mov";

                // Set the frame rate (adjust as needed)
                int frameRate = 30;

                // Convert frames to video
                convertToVideo(recordedFrames, outputFilePath, frameRate);

                // Clear the recorded frames
                recordedFrames.clear();
                System.out.println("Video saved successfully.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void updateDurationLabel() {
            new Thread(() -> {
                while (isRecording.get()) {
                    long elapsedTime = System.currentTimeMillis() - recordingStartTime;
                    String formattedDuration = formatDuration(elapsedTime);
                    SwingUtilities.invokeLater(() -> durationLabel.setText("Recording Duration: " + formattedDuration));
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }

        private String formatDuration(long millis) {
            long hours = TimeUnit.MILLISECONDS.toHours(millis);
            long minutes = TimeUnit.MILLISECONDS.toMinutes(millis) % 60;
            long seconds = TimeUnit.MILLISECONDS.toSeconds(millis) % 60;
            return String.format("%02d:%02d:%02d", hours, minutes, seconds);
        }

        private void convertToVideo(List<BufferedImage> frames, String outputFilePath, int frameRate)
                throws IOException {
            VideoConverter.write(frames);
        }
    }


}
