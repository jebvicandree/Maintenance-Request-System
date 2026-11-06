/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Designs;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Path2D;
import java.awt.geom.RoundRectangle2D;
import javax.swing.JPanel;

public class WhiteGreyP  extends JPanel {

    private Color startColor =
            new Color(255, 255, 255);

    private Color middleColor =
            new Color(239, 243, 248);

    private Color endColor =
            new Color(215, 225, 237);

    private int cornerRadius = 25;

    public WhiteGreyP() {
        setOpaque(false);
    }

    public void setGradientColors(
            Color start, Color middle, Color end) {

        startColor = start;
        middleColor = middle;
        endColor = end;

        repaint();
    }

    public void setCornerRadius(int radius) {
        cornerRadius = Math.max(0, radius);
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {

        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g.create();

        g2.setRenderingHint(
            RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON
        );

        int w = getWidth();
        int h = getHeight();

        if (w <= 0 || h <= 0) {
            g2.dispose();
            return;
        }

        // Rounded corners
        g2.clip(new RoundRectangle2D.Double(
            0, 0, w, h,
            cornerRadius, cornerRadius
        ));

        // White to bluish-grey gradient
        g2.setPaint(new GradientPaint(
            0, 0, startColor,
            w, h, endColor
        ));

        g2.fillRect(0, 0, w, h);

        // First soft wave
        Path2D wave1 = new Path2D.Double();

        wave1.moveTo(0, h * 0.72);

        wave1.curveTo(
            w * 0.20, h * 0.48,
            w * 0.34, h * 0.98,
            w * 0.55, h * 0.70
        );

        wave1.curveTo(
            w * 0.75, h * 0.48,
            w * 0.84, h * 0.55,
            w, h * 0.28
        );

        wave1.lineTo(w, h);
        wave1.lineTo(0, h);
        wave1.closePath();

        g2.setColor(new Color(190, 205, 224, 65));
        g2.fill(wave1);

        // Second soft wave
        Path2D wave2 = new Path2D.Double();

        wave2.moveTo(0, h * 0.84);

        wave2.curveTo(
            w * 0.22, h * 0.66,
            w * 0.42, h * 0.91,
            w * 0.62, h * 0.75
        );

        wave2.curveTo(
            w * 0.80, h * 0.61,
            w * 0.91, h * 0.40,
            w, h * 0.43
        );

        wave2.lineTo(w, h);
        wave2.lineTo(0, h);
        wave2.closePath();

        g2.setColor(new Color(215, 225, 237, 100));
        g2.fill(wave2);

        g2.dispose();
    }

}
