package com.javabrown.xcapture;

import com.javabrown.core.utils.ui.PortMonitorPanel;
import com.javabrown.xcapture.screen.SnippingWindow;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class XCapture {
    public SnippingWindow snippingWindow;
    private Robot robot;
    private SystemTrayManager systemTrayManager;

    public void initialize() {
        try {
            robot = new Robot();
        } catch (AWTException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error initializing Robot.");
            System.exit(1);
        }
        systemTrayManager = new SystemTrayManager(this);
    }

    public void showSelectionWindow() {
        if(snippingWindow != null) {
            snippingWindow.setVisible(false);
            snippingWindow = null;
        }

        snippingWindow = new SnippingWindow(robot);
        snippingWindow.setVisible(true);
    }
}

class SystemTrayManager {
    private XCapture app;

    public SystemTrayManager(XCapture app) {
        this.app = app;
        this.addSystemTrayIcon();
    }

    private void addSystemTrayIcon() {
        SystemTray systemTray = SystemTray.getSystemTray();
        TrayIconManager trayIconManager = new TrayIconManager(app);
        TrayIcon trayIcon = trayIconManager.createTrayIcon();

        try {
            systemTray.add(trayIcon);
        } catch (AWTException e) {
            e.printStackTrace();
        }

        trayIcon.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1) {
                    app.snippingWindow.setVisible(true);
                }
            }
        });
    }
}

class TrayIconManager {
    private XCapture app;

    public TrayIconManager(XCapture app) {
        this.app = app;
    }

    public TrayIcon createTrayIcon() {
        Image icon;
        String iconPath = "path/to/icon.png"; // Replace with your icon path
        File iconFile = new File(iconPath);

        if (iconFile.exists()) {
            icon = Toolkit.getDefaultToolkit().getImage(iconPath);
        } else {
            Icon defaultIcon = UIManager.getIcon("OptionPane.informationIcon");
            BufferedImage bufferedImage = new BufferedImage(defaultIcon.getIconWidth(), defaultIcon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
            Graphics g = bufferedImage.createGraphics();
            defaultIcon.paintIcon(null, g, 0, 0);
            g.dispose();
            icon = bufferedImage;
        }

        PopupMenuManager popupMenuManager = new PopupMenuManager(app);
        PopupMenu popupMenu = popupMenuManager.createPopupMenu();
        TrayIcon trayIcon = new TrayIcon(icon, "XCapture", popupMenu);
        trayIcon.setImageAutoSize(true);

        return trayIcon;
    }
}

class PopupMenuManager {
    private XCapture app;

    public PopupMenuManager(XCapture app) {
        this.app = app;
    }

    public PopupMenu createPopupMenu() {
        PopupMenu popupMenu = new PopupMenu();
        MenuItem captureItem = new MenuItem("Capture Screen");
        captureItem.addActionListener(e -> {
            app.showSelectionWindow();
            //app.startContinuousCapture(new JLabel());
            System.out.println("Capture Screen");
        });
        popupMenu.add(captureItem);

        MenuItem stopItem = new MenuItem("Stop Capture");
        stopItem.addActionListener(e -> {
            System.out.println("Stop Capture");
        });
        popupMenu.add(stopItem);

        MenuItem portUsesItem = new MenuItem("Port Uses");
        portUsesItem.addActionListener(e -> {
            SwingUtilities.invokeLater(() -> {
                JFrame frame = new JFrame("Port Monitor Panel");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setSize(400, 300);
                frame.add(new PortMonitorPanel());
                frame.setVisible(true);
            });
            JOptionPane.showMessageDialog(null, "XCapture v1.0", "About", JOptionPane.INFORMATION_MESSAGE);
        });
        popupMenu.add(portUsesItem);

        MenuItem aboutItem = new MenuItem("About");
        aboutItem.addActionListener(e -> {
            JOptionPane.showMessageDialog(null, "XCapture v1.0", "About", JOptionPane.INFORMATION_MESSAGE);
        });
        popupMenu.add(aboutItem);

        popupMenu.addSeparator();

        MenuItem exitItem = new MenuItem("Exit");
        exitItem.addActionListener(e -> {
            System.exit(0);
        });
        popupMenu.add(exitItem);

        return popupMenu;
    }
}