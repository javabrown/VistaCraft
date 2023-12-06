package com.javabrown.core.utils.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

public class DisplayMonitorPanel extends JPanel {

    private static final String MESSAGE = "Select Display Monitor for Recording";

    public DisplayMonitorPanel() {
        setLayout(new BorderLayout());

        // Add JLabel at the top of the panel
        JLabel messageLabel = new JLabel(MESSAGE, SwingConstants.CENTER);
        messageLabel.setFont(new Font("Arial", Font.BOLD, 16));
        add(messageLabel, BorderLayout.NORTH);

        JPanel buttonsPanel = new JPanel(new FlowLayout());

        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] screens = ge.getScreenDevices();

        for (GraphicsDevice screen : screens) {
            JButton button = createScreenButton(screen);
            buttonsPanel.add(button);
        }

        add(buttonsPanel, BorderLayout.CENTER);
    }

    private JButton createScreenButton(GraphicsDevice screen) {
        JButton button = new JButton();
        button.setPreferredSize(new Dimension(120, 120));
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showScreenResolutionDialog(screen);
            }
        });

        // Capture screenshot
        Rectangle screenBounds = screen.getDefaultConfiguration().getBounds();
        BufferedImage screenshot = null;
        try {
            screenshot = new Robot().createScreenCapture(screenBounds);
        } catch (AWTException ex) {
            ex.printStackTrace();
        }
        ImageIcon icon = new ImageIcon(screenshot.getScaledInstance(100, 100, Image.SCALE_SMOOTH));
        button.setIcon(icon);

        return button;
    }

    private void showScreenResolutionDialog(GraphicsDevice screen) {
        DisplayMode mode = screen.getDisplayMode();
        String message = "Screen Resolution: " + mode.getWidth() + "x" + mode.getHeight();
        JOptionPane.showMessageDialog(this, message, "Screen Resolution", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Display Monitor Panel");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(600, 400);
            frame.add(new DisplayMonitorPanel());
            frame.setVisible(true);
        });
    }
}
