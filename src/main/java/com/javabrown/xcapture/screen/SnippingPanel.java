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
    private Rectangle selectedRectangularSnippingArea;
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
                    selectedRectangularSnippingArea = selectionRect;
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

        // Draw a border around the panel
        g.setColor(Color.BLACK);
        g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);

        // Convert the screenshot to black and white (grayscale)
        BufferedImage grayscaleImage = convertToGrayscale(initialScreenshot);
        g.drawImage(grayscaleImage, 1, 1, this);

        if (selectionRect != null) {
            g.setColor(new Color(255, 255, 0, 100));
            g.fillRect(selectionRect.x, selectionRect.y, selectionRect.width, selectionRect.height);
        }
    }

    public Rectangle getSelectedRectangularSnippingArea() {
        return this.selectedRectangularSnippingArea;
    }

    private BufferedImage convertToGrayscale(BufferedImage colorImage) {
        BufferedImage grayscaleImage = new BufferedImage(
                colorImage.getWidth(), colorImage.getHeight(), BufferedImage.TYPE_BYTE_GRAY);

        Graphics g = grayscaleImage.getGraphics();
        g.drawImage(colorImage, 0, 0, null);
        g.dispose();

        return grayscaleImage;
    }
}
