/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Maintenance_Request_System;


import java.awt.*;
import java.util.ArrayList;
import javax.swing.JPanel;

public class CustomBarChart extends JPanel {

    // Stores all bars
    private final ArrayList<BarItem> bars = new ArrayList<>();

    // Chart settings
    private String chartTitle = "Bar Chart";

    private Color chartBackground = Color.WHITE;
    private Color titleColor = new Color(50, 50, 50);
    private Color labelColor = new Color(90, 90, 90);
    private Color gridColor = new Color(225, 225, 225);

    private int cornerRadius = 25;
    private int barRadius = 10;

    public CustomBarChart() {
        setOpaque(false);
        setPreferredSize(new Dimension(600, 350));
    }

    // =========================
    // ADD A BAR
    // =========================
    public void addBar(String name, int value, Color color) {
        bars.add(new BarItem(name, value, color));
        repaint();
    }

    // =========================
    // REMOVE ALL BARS
    // =========================
    public void clearBars() {
        bars.clear();
        repaint();
    }

    // =========================
    // SET TITLE
    // =========================
    public void setChartTitle(String title) {
        this.chartTitle = title;
        repaint();
    }

    // =========================
    // SET BACKGROUND COLOR
    // =========================
    public void setChartBackground(Color color) {
        this.chartBackground = color;
        repaint();
    }

    // =========================
    // SET TITLE COLOR
    // =========================
    public void setTitleColor(Color color) {
        this.titleColor = color;
        repaint();
    }

    // =========================
    // SET LABEL COLOR
    // =========================
    public void setLabelColor(Color color) {
        this.labelColor = color;
        repaint();
    }

    // =========================
    // SET GRID COLOR
    // =========================
    public void setGridColor(Color color) {
        this.gridColor = color;
        repaint();
    }

    // =========================
    // SET CORNER RADIUS
    // =========================
    public void setCornerRadius(int radius) {
        this.cornerRadius = radius;
        repaint();
    }

    // =========================
    // PAINT CHART
    // =========================
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

        // =========================
        // CHART BACKGROUND
        // =========================

        g2.setColor(chartBackground);

        g2.fillRoundRect(
                0,
                0,
                width,
                height,
                cornerRadius,
                cornerRadius
        );

        // =========================
        // TITLE
        // =========================

        g2.setColor(titleColor);

        g2.setFont(new Font(
                "Segoe UI",
                Font.BOLD,
                18
        ));

        g2.drawString(
                chartTitle,
                25,
                35
        );

        // No bars
        if (bars.isEmpty()) {
            g2.setColor(labelColor);

            g2.setFont(new Font(
                    "Segoe UI",
                    Font.PLAIN,
                    14
            ));

            g2.drawString(
                    "No data available",
                    25,
                    height / 2
            );

            g2.dispose();
            return;
        }

        // =========================
        // CHART AREA
        // =========================

        int left = 55;
        int right = 25;
        int top = 60;
        int bottom = 55;

        int chartWidth = width - left - right;
        int chartHeight = height - top - bottom;

        // Find highest value
        int maxValue = 0;

        for (BarItem bar : bars) {
            if (bar.value > maxValue) {
                maxValue = bar.value;
            }
        }

        if (maxValue == 0) {
            maxValue = 1;
        }

        // =========================
        // GRID LINES
        // =========================

        g2.setStroke(new BasicStroke(1));

        g2.setColor(gridColor);

        int gridLines = 5;

        for (int i = 0; i <= gridLines; i++) {

            int y = top
                    + chartHeight
                    - (i * chartHeight / gridLines);

            g2.drawLine(
                    left,
                    y,
                    width - right,
                    y
            );

            // Y-axis number
            int value =
                    maxValue * i / gridLines;

            g2.setColor(labelColor);

            g2.setFont(new Font(
                    "Segoe UI",
                    Font.PLAIN,
                    11
            ));

            g2.drawString(
                    String.valueOf(value),
                    15,
                    y + 4
            );

            g2.setColor(gridColor);
        }

        // =========================
        // BAR CALCULATIONS
        // =========================

        int numberOfBars = bars.size();

        int spacing = 20;

        int totalSpacing =
                spacing * (numberOfBars + 1);

        int barWidth =
                (chartWidth - totalSpacing)
                / numberOfBars;

        if (barWidth < 10) {
            barWidth = 10;
        }

        // =========================
        // DRAW BARS
        // =========================

        for (int i = 0; i < numberOfBars; i++) {

            BarItem bar = bars.get(i);

            int barHeight =
                    (int) (
                            ((double) bar.value
                                    / maxValue)
                                    * chartHeight
                    );

            int x =
                    left
                    + spacing
                    + i * (barWidth + spacing);

            int y =
                    top
                    + chartHeight
                    - barHeight;

            // Bar
            g2.setColor(bar.color);

            g2.fillRoundRect(
                    x,
                    y,
                    barWidth,
                    barHeight,
                    barRadius,
                    barRadius
            );

            // =========================
            // VALUE ABOVE BAR
            // =========================

            g2.setColor(titleColor);

            g2.setFont(new Font(
                    "Segoe UI",
                    Font.BOLD,
                    11
            ));

            String valueText =
                    String.valueOf(bar.value);

            FontMetrics fm =
                    g2.getFontMetrics();

            int valueX =
                    x
                    + (barWidth
                    - fm.stringWidth(valueText))
                    / 2;

            int valueY =
                    y - 7;

            g2.drawString(
                    valueText,
                    valueX,
                    valueY
            );

            // =========================
            // LABEL
            // =========================

            g2.setColor(labelColor);

            g2.setFont(new Font(
                    "Segoe UI",
                    Font.PLAIN,
                    11
            ));

            String label = bar.name;

            int labelX =
                    x
                    + (barWidth
                    - g2.getFontMetrics()
                    .stringWidth(label))
                    / 2;

            int labelY =
                    top
                    + chartHeight
                    + 25;

            g2.drawString(
                    label,
                    labelX,
                    labelY
            );
        }

        g2.dispose();
    }

    // =========================
    // BAR ITEM CLASS
    // =========================

    private static class BarItem {

        String name;
        int value;
        Color color;

        BarItem(
                String name,
                int value,
                Color color
        ) {
            this.name = name;
            this.value = value;
            this.color = color;
        }
    }

}
