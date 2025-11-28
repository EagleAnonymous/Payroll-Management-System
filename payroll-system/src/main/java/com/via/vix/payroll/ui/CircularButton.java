package com.via.vix.payroll.ui;

import javax.swing.*;
import java.awt.*;

/**
 * A custom JButton that is rendered as a circle.
 */
public class CircularButton extends JButton {

    private Color hoverBackgroundColor;
    private Color pressedBackgroundColor;

    public CircularButton(String text) {
        super(text);
        // Make the button transparent to allow for custom painting
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Determine the color based on the button's state (pressed, hover, or normal)
        if (getModel().isPressed()) {
            g2.setColor(getPressedBackgroundColor());
        } else if (getModel().isRollover()) {
            g2.setColor(getHoverBackgroundColor());
        } else {
            g2.setColor(getBackground());
        }

        // Draw the circular background
        // Ensure it's a circle by using the smaller of width/height as diameter
        int diameter = Math.min(getWidth(), getHeight());
        int x = (getWidth() - diameter) / 2;
        int y = (getHeight() - diameter) / 2;
        g2.fillOval(x, y, diameter, diameter);

        // --- Manually paint the text in the center ---
        g2.setColor(getForeground());
        g2.setFont(getFont());
        FontMetrics metrics = g2.getFontMetrics(getFont());
        // Determine the X coordinate for the text
        int textX = (getWidth() - metrics.stringWidth(getText())) / 2;
        // Determine the Y coordinate for the text
        int textY = ((getHeight() - metrics.getHeight()) / 2) + metrics.getAscent();
        g2.drawString(getText(), textX, textY);

        g2.dispose();
    }

    // Ensure the button is always a circle by making its bounds a square
    @Override
    public Dimension getPreferredSize() {
        Dimension size = super.getPreferredSize();
        // Use the larger of width/height to make a square
        int max = Math.max(size.width, size.height);
        return new Dimension(max, max);
    }

    // Getters and setters for hover/pressed colors to create visual feedback
    public Color getHoverBackgroundColor() {
        return hoverBackgroundColor == null ? getBackground().brighter() : hoverBackgroundColor;
    }

    public Color getPressedBackgroundColor() {
        return pressedBackgroundColor == null ? getBackground().darker() : pressedBackgroundColor;
    }
}