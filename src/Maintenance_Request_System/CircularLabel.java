/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Maintenance_Request_System;
import java.awt.*;
import javax.swing.*;
/**
 *
 * @author John Manuel
 */
public class CircularLabel extends JLabel {

    public CircularLabel() {
        setOpaque(false);
        setHorizontalAlignment(SwingConstants.CENTER);
        setVerticalAlignment(SwingConstants.CENTER);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();

        g2.setRenderingHint(
            RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON
        );

        // Make the background circular
        g2.setColor(getBackground());
        g2.fillOval(0, 0, getWidth(), getHeight());

        // Clip everything inside the circle
        g2.setClip(new java.awt.geom.Ellipse2D.Float(
            0, 0, getWidth(), getHeight()
        ));

        super.paintComponent(g2);

        g2.dispose();
    }
    
}
