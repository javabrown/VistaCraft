//package com.javabrown.xcapture.screen;
//
//import javax.swing.*;
//import java.awt.event.ActionEvent;
//import java.awt.event.ActionListener;
//
//public class CaptureFrameControlPanel extends JPanel {
//    private JButton recordButton;
//    private JButton stopButton;
//
//    public CaptureFrameControlPanel() {
//        initializeComponents();
//        setupLayout();
//        setupListeners();
//    }
//
//    private void initializeComponents() {
//        recordButton = new JButton("Record");
//        stopButton = new JButton("Stop Record");
//    }
//
//    private void setupLayout() {
//        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
//        add(recordButton);
//        add(Box.createHorizontalStrut(10)); // Add some space between buttons
//        add(stopButton);
//    }
//
//    private void setupListeners() {
//        recordButton.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                // Handle record button click
//                System.out.println("Record button clicked");
//            }
//        });
//
//        stopButton.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                // Handle stop button click
//                System.out.println("Stop Record button clicked");
//            }
//        });
//    }
//}