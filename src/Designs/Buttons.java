/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Designs;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JButton;

public class Buttons extends JButton {

    private int cornerRadius = 25;
    private Color normalColor = new Color(29,111,90);
    private Color hoverColor = new Color(55, 137, 114);
    private Color currentColor = normalColor;

    public Buttons() {
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setOpaque(false);

        setForeground(Color.WHITE);
        setBackground(normalColor);
        setCursor(new java.awt.Cursor(
            java.awt.Cursor.HAND_CURSOR
        ));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                currentColor = hoverColor;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                currentColor = normalColor;
                repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();

        g2.setRenderingHint(
            RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON
        );

        currentColor = getModel().isPressed()
                ? hoverColor
                : currentColor;

        g2.setColor(isEnabled()
                ? currentColor
                : Color.LIGHT_GRAY);

        g2.fillRoundRect(
            0, 0, getWidth(), getHeight(),
            cornerRadius, cornerRadius
        );

        g2.dispose();

        super.paintComponent(g);
    }

    public void setCornerRadius(int radius) {
        this.cornerRadius = radius;
        repaint();
    }

    public int getCornerRadius() {
        return cornerRadius;
    }

    public void setNormalColor(Color color) {
        this.normalColor = color;
        this.currentColor = color;
        setBackground(color);
        repaint();
    }

    public void setHoverColor(Color color) {
        this.hoverColor = color;
    }
}

