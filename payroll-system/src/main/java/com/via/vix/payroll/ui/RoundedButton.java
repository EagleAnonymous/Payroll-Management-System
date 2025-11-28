package com.via.vix.payroll.ui;

import javax.swing.*;
import java.awt.*;

/**
 * A custom JButton that is rendered as a rounded rectangle (pill shape).
 */
public class RoundedButton extends JButton {

    private Color hoverBackgroundColor;
    private Color pressedBackgroundColor;

    public RoundedButton(String text) {
        super(text);
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (getModel().isPressed()) {
            g2.setColor(getPressedBackgroundColor());
        } else if (getModel().isRollover()) {
            g2.setColor(getHoverBackgroundColor());
        } else {
            g2.setColor(getBackground());
        }

        // Draw a rounded rectangle. The arc width/height is set to the component's height
        // to create a "pill" shape.
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), getHeight(), getHeight());
        g2.dispose();

        super.paintComponent(g);
    }

    public Color getHoverBackgroundColor() {
        return hoverBackgroundColor == null ? getBackground().brighter() : hoverBackgroundColor;
    }

    public Color getPressedBackgroundColor() {
        return pressedBackgroundColor == null ? getBackground().darker() : pressedBackgroundColor;
    }
}