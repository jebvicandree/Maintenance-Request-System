/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Maintenance_Request_System;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Insets;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public class RoundedSearchField extends JTextField {

    public RoundedSearchField() {
        setOpaque(false);
        setBackground(Color.WHITE);
        setForeground(new Color(60, 60, 60));
        setCaretColor(new Color(44, 163, 132));
        setBorder(new EmptyBorder(5, 14, 5, 14));
        setMargin(new Insets(5, 10, 5, 10));
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();

        g2.setRenderingHint(
            RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON
        );

        g2.setColor(getBackground());
        g2.fillRoundRect(
            0, 0, getWidth(), getHeight(), 24, 24
        );

        g2.dispose();
        super.paintComponent(g);
    }

    @Override
    protected void paintBorder(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();

        g2.setRenderingHint(
            RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON
        );

        g2.setColor(new Color(215, 225, 220));
        g2.drawRoundRect(
            0, 0, getWidth() - 1, getHeight() - 1, 24, 24
        );

        g2.dispose();
    }

}
