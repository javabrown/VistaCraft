package com.javabrown.xcapture.screen;

import com.javabrown.core.utils.Callback;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

public class SnippingPanel extends JPanel {
    private BufferedImage initialScreenshot;
    private Rectangle selectionRect;
    private Rectangle selectedRectengularSnippingArea;
    private Point startPoint;

    private Callback callbackOnSelectionComplete;

    public SnippingPanel(BufferedImage initialScreenshot, Callback callbackOnSelectionComplete) {
        this.callbackOnSelectionComplete = callbackOnSelectionComplete;
        this.initialScreenshot = initialScreenshot;
        setLayout(null);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                startPoint = e.getPoint();
                selectionRect = null;
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (selectionRect != null) {
                    selectedRectengularSnippingArea = selectionRect;
                    callbackOnSelectionComplete.trigger();
                }
            }
        });

        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                int x = Math.min(startPoint.x, e.getX());
                int y = Math.min(startPoint.y, e.getY());
                int width = Math.abs(startPoint.x - e.getX());
                int height = Math.abs(startPoint.y - e.getY());

                selectionRect = new Rectangle(x, y, width, height);
                repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(initialScreenshot, 0, 0, this);

        if (selectionRect != null) {
            g.setColor(new Color(255, 255, 0, 100));
            g.fillRect(selectionRect.x, selectionRect.y, selectionRect.width, selectionRect.height);
        }
    }

    public Rectangle getSelectedRectengularSnippingArea() {
        return this.selectedRectengularSnippingArea;
    }
}