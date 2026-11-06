/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Designs;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.GradientPaint;
import javax.swing.JPanel;

//trash
public class DesignPanels extends JPanel {

    

    private int cornerRadius = 25;

    public DesignPanels() {
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g.create();

        g2.setRenderingHint(
            RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON
        );

        GradientPaint gradient = new GradientPaint(
            0, 0, Color.WHITE,
            getWidth(), getHeight(),
            new Color(190, 205, 220)
        );

        g2.setPaint(gradient);

        g2.fillRoundRect(
            0, 0,
            getWidth(), getHeight(),
            cornerRadius, cornerRadius
        );

        g2.dispose();
    }

    public void setCornerRadius(int radius) {
        this.cornerRadius = radius;
        repaint();
    }

    public int getCornerRadius() {
        return cornerRadius;
    }


}
