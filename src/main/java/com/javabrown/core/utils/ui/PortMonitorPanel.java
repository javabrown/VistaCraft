package com.javabrown.core.utils.ui;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;

public class PortMonitorPanel extends JPanel {

    private JTree portTree;
    private DefaultMutableTreeNode root;

    public PortMonitorPanel() {
        setLayout(new BorderLayout());

        root = new DefaultMutableTreeNode("Ports");
        portTree = new JTree(root);

        JButton refreshButton = new JButton("Refresh");
        JButton killProcessButton = new JButton("Kill Process");

        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                refreshPortList();
            }
        });

        killProcessButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                killSelectedProcess();
            }
        });

        portTree.addTreeSelectionListener(e -> {
            TreePath selectedPath = e.getPath();
            Object selectedNode = selectedPath.getLastPathComponent();
            if (selectedNode instanceof DefaultMutableTreeNode) {
                String portInfo = ((DefaultMutableTreeNode) selectedNode).getUserObject().toString();
                if (portInfo.startsWith("In-use Port: ")) {
                    int port = Integer.parseInt(portInfo.substring("In-use Port: ".length()));
                    displayProcessDetails(port);
                }
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(refreshButton);
        buttonPanel.add(killProcessButton);

        add(new JScrollPane(portTree), BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        refreshPortList();
    }

    private void refreshPortList() {
        root.removeAllChildren();
        for (int port = 1; port <= 65535; port++) {
            if (isPortInUse(port)) {
                root.add(new DefaultMutableTreeNode("In-use Port: " + port));
            }
        }
        ((DefaultTreeModel) portTree.getModel()).reload();
    }

    private boolean isPortInUse(int port) {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            return false; // Port is free
        } catch (IOException e) {
            return true; // Port is in use
        }
    }

    private void killSelectedProcess() {
        TreePath selectedPath = portTree.getSelectionPath();
        if (selectedPath != null) {
            Object selectedNode = selectedPath.getLastPathComponent();
            if (selectedNode instanceof DefaultMutableTreeNode) {
                String portInfo = ((DefaultMutableTreeNode) selectedNode).getUserObject().toString();
                if (portInfo.startsWith("In-use Port: ")) {
                    int port = Integer.parseInt(portInfo.substring("In-use Port: ".length()));
                    try {
                        ProcessBuilder processBuilder = new ProcessBuilder("lsof", "-i", ":" + port, "-t");
                        Process process = processBuilder.start();
                        process.waitFor();

                        // Read the input stream using BufferedReader
                        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                            String processId = reader.readLine();
                            if (processId != null && !processId.isEmpty()) {
                                Process killProcess = new ProcessBuilder("kill", processId).start();
                                killProcess.waitFor();
                                refreshPortList();
                            }
                        }
                    } catch (IOException | InterruptedException ex) {
                        ex.printStackTrace();
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Please select an in-use port to kill the process.",
                            "No In-use Port Selected", JOptionPane.WARNING_MESSAGE);
                }
            }
        }
    }

    private void displayProcessDetails(int port) {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder("lsof", "-i", ":" + port);
            Process process = processBuilder.start();
            process.waitFor();

            // Read the input stream using BufferedReader
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                StringBuilder details = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    details.append(line).append("\n");
                }

                JOptionPane.showMessageDialog(this, details.toString(), "Process Details",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (IOException | InterruptedException ex) {
            ex.printStackTrace();
        }
    }
}
