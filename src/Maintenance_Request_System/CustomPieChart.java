/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Maintenance_Request_System;


import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Arc2D;
import java.util.ArrayList;
import javax.swing.JPanel;

public class CustomPieChart extends JPanel {



    // =====================================================
    // PIE CHART DATA
    // =====================================================

    private final ArrayList<PieItem> items = new ArrayList<>();

    // =====================================================
    // CHART SETTINGS
    // =====================================================

    private String chartTitle = "Pie Chart";

    private Color chartBackground = Color.WHITE;
    private Color borderColor = new Color(230, 230, 230);
    private Color textColor = new Color(60, 60, 60);

    private int cornerRadius = 20;

    // =====================================================
    // CONSTRUCTOR
    // =====================================================

    public CustomPieChart() {

        setOpaque(false);
        setBackground(Color.WHITE);

    }

    // =====================================================
    // PAINT CHART
    // =====================================================

    @Override
    protected void paintComponent(Graphics g) {

        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g.create();

        g2.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
        );

        int width = getWidth();
        int height = getHeight();

        // =================================================
        // BACKGROUND
        // =================================================

        g2.setColor(chartBackground);

        g2.fillRoundRect(
                0,
                0,
                width - 1,
                height - 1,
                cornerRadius,
                cornerRadius
        );

        // =================================================
        // BORDER
        // =================================================

        g2.setColor(borderColor);

        g2.setStroke(
                new BasicStroke(1)
        );

        g2.drawRoundRect(
                0,
                0,
                width - 1,
                height - 1,
                cornerRadius,
                cornerRadius
        );

        // =================================================
        // TITLE
        // =================================================

        g2.setColor(textColor);

        g2.setFont(
                new Font(
                        "SansSerif",
                        Font.BOLD,
                        18
                )
        );

        int titleWidth =
                g2.getFontMetrics()
                        .stringWidth(chartTitle);

        g2.drawString(
                chartTitle,
                (width - titleWidth) / 2,
                30
        );

        // =================================================
        // CHECK DATA
        // =================================================

        if (items.isEmpty()) {

            g2.setColor(Color.GRAY);

            g2.setFont(
                    new Font(
                            "SansSerif",
                            Font.PLAIN,
                            14
                    )
            );

            String message = "No data available";

            int messageWidth =
                    g2.getFontMetrics()
                            .stringWidth(message);

            g2.drawString(
                    message,
                    (width - messageWidth) / 2,
                    height / 2
            );

            g2.dispose();
            return;
        }

        // =================================================
        // CALCULATE TOTAL
        // =================================================

        double total = 0;

        for (PieItem item : items) {

            if (item.value > 0) {
                total += item.value;
            }
        }

        if (total <= 0) {

            g2.setColor(Color.GRAY);

            g2.setFont(
                    new Font(
                            "SansSerif",
                            Font.PLAIN,
                            14
                    )
            );

            String message = "No data available";

            int messageWidth =
                    g2.getFontMetrics()
                            .stringWidth(message);

            g2.drawString(
                    message,
                    (width - messageWidth) / 2,
                    height / 2
            );

            g2.dispose();
            return;
        }

        // =================================================
        // PIE SIZE
        // =================================================

        int legendHeight = 55;

        int availableWidth =
                width - 40;

        int availableHeight =
                height - 75 - legendHeight;

        int pieSize =
                Math.min(
                        availableWidth,
                        availableHeight
                );

        // Prevent negative size
        if (pieSize < 10) {
            g2.dispose();
            return;
        }

        int x =
                (width - pieSize) / 2;

        int y = 45;

        // =================================================
        // DRAW PIE
        // =================================================

        double startAngle = 90;

        for (PieItem item : items) {

            if (item.value <= 0) {
                continue;
            }

            double angle =
                    (item.value / total) * 360;

            g2.setColor(item.color);

            g2.fill(
                    new Arc2D.Double(
                            x,
                            y,
                            pieSize,
                            pieSize,
                            startAngle,
                            -angle,
                            Arc2D.PIE
                    )
            );

            startAngle -= angle;
        }

        // =================================================
        // PIE OUTLINE
        // =================================================

        g2.setColor(Color.WHITE);

        g2.setStroke(
                new BasicStroke(2)
        );

        g2.drawOval(
                x,
                y,
                pieSize,
                pieSize
        );

        // =================================================
        // LEGEND
        // =================================================

        drawLegend(
                g2,
                width,
                height
        );

        g2.dispose();
    }

    // =====================================================
    // DRAW LEGEND
    // =====================================================

    private void drawLegend(
            Graphics2D g2,
            int width,
            int height) {

        g2.setFont(
                new Font(
                        "SansSerif",
                        Font.PLAIN,
                        12
                )
        );

        int legendY = height - 35;

        int currentX = 15;

        for (PieItem item : items) {

            // Color circle
            g2.setColor(item.color);

            g2.fillOval(
                    currentX,
                    legendY - 9,
                    10,
                    10
            );

            // Text
            g2.setColor(textColor);

            g2.drawString(
                    item.name,
                    currentX + 15,
                    legendY
            );

            // Move to next item
            int textWidth =
                    g2.getFontMetrics()
                            .stringWidth(item.name);

            currentX +=
                    textWidth + 35;

            // If legend becomes too wide,
            // start another row
            if (currentX > width - 80) {

                legendY += 20;

                currentX = 15;
            }
        }
    }

    // =====================================================
    // ADD DATA
    // =====================================================

    public void addData(
            String name,
            double value,
            Color color) {

        items.add(
                new PieItem(
                        name,
                        value,
                        color
                )
        );

        repaint();
    }

    // =====================================================
    // REMOVE DATA
    // =====================================================

    public void removeData(String name) {

        items.removeIf(
                item -> item.name.equals(name)
        );

        repaint();
    }

    // =====================================================
    // CLEAR ALL DATA
    // =====================================================

    public void clearData() {

        items.clear();

        repaint();
    }

    // =====================================================
    // CHANGE EXISTING VALUE
    // =====================================================

    public void setValue(
            String name,
            double value) {

        for (PieItem item : items) {

            if (item.name.equals(name)) {

                item.value = value;

                repaint();

                return;
            }
        }
    }

    // =====================================================
    // CHANGE EXISTING COLOR
    // =====================================================

    public void setColor(
            String name,
            Color color) {

        for (PieItem item : items) {

            if (item.name.equals(name)) {

                item.color = color;

                repaint();

                return;
            }
        }
    }

    // =====================================================
    // CHANGE TITLE
    // =====================================================

    public void setChartTitle(
            String title) {

        this.chartTitle = title;

        repaint();
    }

    // =====================================================
    // CHANGE BACKGROUND
    // =====================================================

    public void setChartBackground(
            Color color) {

        this.chartBackground = color;

        repaint();
    }

    // =====================================================
    // CHANGE TEXT COLOR
    // =====================================================

    public void setTextColor(
            Color color) {

        this.textColor = color;

        repaint();
    }

    // =====================================================
    // CHANGE BORDER COLOR
    // =====================================================

    public void setBorderColor(
            Color color) {

        this.borderColor = color;

        repaint();
    }

    // =====================================================
    // CHANGE CORNER RADIUS
    // =====================================================

    public void setCornerRadius(
            int radius) {

        this.cornerRadius = radius;

        repaint();
    }

    // =====================================================
    // PIE ITEM CLASS
    // =====================================================

    private static class PieItem {

        String name;
        double value;
        Color color;

        PieItem(
                String name,
                double value,
                Color color) {

            this.name = name;
            this.value = value;
            this.color = color;
        }
    }

}
