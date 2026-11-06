/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Designs;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GradientPaint;
import javax.swing.JPanel;
    

public class GreenP extends JPanel {

    
    private Color startColor = new Color(29, 111, 90);
    private Color endColor = new Color(10, 45, 36);

    public GreenP() {
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g.create();

        GradientPaint gradient = new GradientPaint(
            0, 0, startColor,
            getWidth(), getHeight(), endColor
        );

        g2.setPaint(gradient);
        g2.fillRect(0, 0, getWidth(), getHeight());

        g2.dispose();
    }

    public void setStartColor(Color color) {
        startColor = color;
        repaint();
    }

    public void setEndColor(Color color) {
        endColor = color;
        repaint();
    }

}
