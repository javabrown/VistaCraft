package com.javabrown;

import com.javabrown.xcapture.XCapture;

import javax.swing.*;

public class App 
{
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new XCapture().initialize());
    }

}
