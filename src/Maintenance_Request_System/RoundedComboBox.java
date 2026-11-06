/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Maintenance_Request_System;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Rectangle;
import java.awt.Dimension;
import java.awt.Insets;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.UIManager;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.border.EmptyBorder;


public class RoundedComboBox extends JComboBox<String> {

    private Color backgroundColor = Color.WHITE;
    private Color borderColor = new Color(139, 135, 232);
    private Color textColor = new Color(75, 46, 131);
    private int cornerRadius = 15;

    public RoundedComboBox() {
        super();

        setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 13));
        setForeground(textColor);
        setBackground(backgroundColor);
        setOpaque(false);
        setFocusable(true);
        setBorder(new EmptyBorder(4, 10, 4, 8));
        setPreferredSize(new Dimension(180, 38));
        setMaximumRowCount(6);

        setUI(new BasicComboBoxUI() {

            @Override
            protected JButton createArrowButton() {
                JButton arrow = new JButton() {

                    @Override
                    protected void paintComponent(Graphics g) {
                        Graphics2D g2 = (Graphics2D) g.create();

                        g2.setRenderingHint(
                            RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON
                        );

                        int cx = getWidth() / 2;
                        int cy = getHeight() / 2;

                        g2.setColor(textColor);
                        g2.setStroke(
                            new java.awt.BasicStroke(
                                2f,
                                java.awt.BasicStroke.CAP_ROUND,
                                java.awt.BasicStroke.JOIN_ROUND
                            )
                        );

                        g2.drawLine(cx - 4, cy - 2, cx, cy + 2);
                        g2.drawLine(cx, cy + 2, cx + 4, cy - 2);

                        g2.dispose();
                    }
                };

                arrow.setBorderPainted(false);
                arrow.setContentAreaFilled(false);
                arrow.setFocusPainted(false);
                arrow.setOpaque(false);
                arrow.setPreferredSize(new Dimension(30, 30));

                return arrow;
            }

            @Override
            public void paintCurrentValueBackground(
                    Graphics g, Rectangle bounds, boolean hasFocus) {
                // The rounded background is painted by this component.
            }
        });

        setRenderer(new javax.swing.DefaultListCellRenderer() {
            @Override
            public java.awt.Component getListCellRendererComponent(
                    javax.swing.JList<?> list,
                    Object value,
                    int index,
                    boolean isSelected,
                    boolean cellHasFocus) {

                super.getListCellRendererComponent(
                    list, value, index, isSelected, cellHasFocus
                );

                setBorder(new EmptyBorder(6, 10, 6, 10));

                if (isSelected) {
                    setBackground(new Color(239, 236, 255));
                    setForeground(textColor);
                } else {
                    setBackground(Color.WHITE);
                    setForeground(textColor);
                }

                return this;
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

        g2.setColor(backgroundColor);
        g2.fillRoundRect(
            0, 0, getWidth() - 1, getHeight() - 1,
            cornerRadius, cornerRadius
        );

        g2.dispose();

        super.paintComponent(g);

        g2 = (Graphics2D) g.create();

        g2.setRenderingHint(
            RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON
        );

        g2.setColor(borderColor);
        g2.drawRoundRect(
            0, 0, getWidth() - 1, getHeight() - 1,
            cornerRadius, cornerRadius
        );

        g2.dispose();
    }

    public void setBackgroundColor(Color color) {
        backgroundColor = color;
        repaint();
    }

    public void setBorderColor(Color color) {
        borderColor = color;
        repaint();
    }

    public void setTextColor(Color color) {
        textColor = color;
        setForeground(color);
        repaint();
    }

    public void setCornerRadius(int radius) {
        cornerRadius = Math.max(0, radius);
        repaint();
    }


}
