/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Maintenance_Request_System;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;

public class RoundedTextArea extends JPanel {

    private JTextArea textArea;
    private JScrollPane scrollPane;

    private Color borderColor = new Color(210, 210, 220);
    private Color backgroundColor = Color.WHITE;
    private int cornerRadius = 20;

    public RoundedTextArea() {
        setLayout(new java.awt.BorderLayout());
        setOpaque(false);

        textArea = new JTextArea();
        textArea.setFont(new Font("Arial", Font.PLAIN, 14));
        textArea.setForeground(Color.BLACK);
        textArea.setBackground(backgroundColor);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setBorder(new EmptyBorder(10, 12, 10, 12));
        textArea.setOpaque(false);

        scrollPane = new JScrollPane(textArea);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setHorizontalScrollBarPolicy(
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        add(scrollPane, java.awt.BorderLayout.CENTER);

        setPreferredSize(new Dimension(300, 120));
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();

        g2.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(backgroundColor);
        g2.fillRoundRect(
                0, 0, getWidth() - 1, getHeight() - 1,
                cornerRadius, cornerRadius);

        g2.setColor(borderColor);
        g2.drawRoundRect(
                0, 0, getWidth() - 1, getHeight() - 1,
                cornerRadius, cornerRadius);

        g2.dispose();
        super.paintComponent(g);
    }

    public String getText() {
        return textArea.getText();
    }

    public void setText(String text) {
        textArea.setText(text);
    }

    public JTextArea getTextArea() {
        return textArea;
    }

    public void setCornerRadius(int radius) {
        this.cornerRadius = radius;
        repaint();
    }

    public void setBorderColor(Color color) {
        this.borderColor = color;
        repaint();
    }

    public void setTextAreaBackground(Color color) {
        this.backgroundColor = color;
        textArea.setBackground(color);
        repaint();
    }

    public void setTextFont(Font font) {
        textArea.setFont(font);
    }

    public void setTextForeground(Color color) {
        textArea.setForeground(color);
    }

}
