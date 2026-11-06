/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Maintenance_Request_System;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JPanel;

public class CustomeLineChart extends JPanel {

    // =========================
    // DATA
    // =========================

    private final List<String> labels = new ArrayList<>();
    private final List<Double> values = new ArrayList<>();

    // =========================
    // COLORS
    // =========================

    private Color chartBackground = Color.WHITE;
    private Color lineColor = new Color(29, 111, 90);
    private Color pointColor = new Color(29, 111, 90);
    private Color gridColor = new Color(225, 225, 225);
    private Color textColor = new Color(90, 90, 90);

    // =========================
    // STYLE
    // =========================

    private int lineThickness = 3;
    private int pointSize = 8;

    private boolean showPoints = true;
    private boolean showGrid = true;
    private boolean showValues = false;

    private String title = "";
    private String yAxisTitle = "";
    private String xAxisTitle = "";

    // =========================
    // CONSTRUCTOR
    // =========================

    public CustomeLineChart() {

        setOpaque(false);

        setBackground(chartBackground);

        setPreferredSize(new Dimension(600, 350));
    }

    // =========================
    // ADD DATA
    // =========================

    public void addData(String label, double value) {

        labels.add(label);
        values.add(value);

        repaint();
    }

    // =========================
    // UPDATE DATA
    // =========================

    public void updateData(String label, double value) {

        int index = labels.indexOf(label);

        if (index >= 0) {

            values.set(index, value);

        } else {

            addData(label, value);
        }

        repaint();
    }

    // =========================
    // CLEAR DATA
    // =========================

    public void clearData() {

        labels.clear();
        values.clear();

        repaint();
    }

    // =========================
    // SET DATA
    // =========================

    public void setData(List<String> newLabels, List<Double> newValues) {

        labels.clear();
        values.clear();

        labels.addAll(newLabels);
        values.addAll(newValues);

        repaint();
    }

    // =========================
    // TITLE
    // =========================

    public void setTitle(String title) {

        this.title = title;

        repaint();
    }

    // =========================
    // AXIS TITLES
    // =========================

    public void setXAxisTitle(String title) {

        this.xAxisTitle = title;

        repaint();
    }

    public void setYAxisTitle(String title) {

        this.yAxisTitle = title;

        repaint();
    }

    // =========================
    // COLORS
    // =========================

    public void setLineColor(Color color) {

        this.lineColor = color;

        repaint();
    }

    public void setPointColor(Color color) {

        this.pointColor = color;

        repaint();
    }

    public void setGridColor(Color color) {

        this.gridColor = color;

        repaint();
    }

    public void setTextColor(Color color) {

        this.textColor = color;

        repaint();
    }

    public void setChartBackground(Color color) {

        this.chartBackground = color;

        repaint();
    }

    // =========================
    // STYLE SETTINGS
    // =========================

    public void setLineThickness(int thickness) {

        this.lineThickness = thickness;

        repaint();
    }

    public void setPointSize(int size) {

        this.pointSize = size;

        repaint();
    }

    public void setShowPoints(boolean show) {

        this.showPoints = show;

        repaint();
    }

    public void setShowGrid(boolean show) {

        this.showGrid = show;

        repaint();
    }

    public void setShowValues(boolean show) {

        this.showValues = show;

        repaint();
    }

    // =========================
    // PAINT
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
        // BACKGROUND
        // =========================

        g2.setColor(chartBackground);

        g2.fillRoundRect(
                0,
                0,
                width,
                height,
                20,
                20
        );

        // =========================
        // TITLE
        // =========================

        int topSpace = 55;

        if (!title.isEmpty()) {

            g2.setColor(textColor);

            g2.setFont(
                    new Font(
                            "SansSerif",
                            Font.BOLD,
                            18
                    )
            );

            g2.drawString(
                    title,
                    25,
                    30
            );
        }

        // =========================
        // NO DATA
        // =========================

        if (values.isEmpty()) {

            g2.setColor(new Color(
                    150,
                    150,
                    150
            ));

            g2.setFont(
                    new Font(
                            "SansSerif",
                            Font.PLAIN,
                            14
                    )
            );

            String message = "No data available";

            int textWidth =
                    g2.getFontMetrics()
                            .stringWidth(message);

            g2.drawString(
                    message,
                    (width - textWidth) / 2,
                    height / 2
            );

            g2.dispose();

            return;
        }

        // =========================
        // CHART AREA
        // =========================

        int left = 60;
        int right = 25;
        int top = topSpace;
        int bottom = 55;

        int chartWidth =
                width - left - right;

        int chartHeight =
                height - top - bottom;

        // =========================
        // FIND MAX VALUE
        // =========================

        double maxValue = 0;

        for (double value : values) {

            if (value > maxValue) {
                maxValue = value;
            }
        }

        if (maxValue <= 0) {
            maxValue = 10;
        }

        // Add some space above highest point

        maxValue =
                Math.ceil(maxValue * 1.2);

        // =========================
        // GRID
        // =========================

        if (showGrid) {

            g2.setColor(gridColor);

            g2.setStroke(
                    new BasicStroke(1)
            );

            int gridLines = 5;

            for (int i = 0; i <= gridLines; i++) {

                int y =
                        top
                        + (chartHeight * i)
                        / gridLines;

                g2.drawLine(
                        left,
                        y,
                        width - right,
                        y
                );

                // Y values

                double value =
                        maxValue
                        - (maxValue * i)
                        / gridLines;

                g2.setColor(textColor);

                g2.setFont(
                        new Font(
                                "SansSerif",
                                Font.PLAIN,
                                11
                        )
                );

                String text =
                        String.valueOf(
                                (int) value
                        );

                g2.drawString(
                        text,
                        20,
                        y + 4
                );

                g2.setColor(gridColor);
            }
        }

        // =========================
        // AXIS
        // =========================

        g2.setColor(gridColor);

        g2.setStroke(
                new BasicStroke(1)
        );

        g2.drawLine(
                left,
                top,
                left,
                height - bottom
        );

        g2.drawLine(
                left,
                height - bottom,
                width - right,
                height - bottom
        );

        // =========================
        // POINT POSITIONS
        // =========================

        int count = values.size();

        double stepX;

        if (count == 1) {

            stepX = 0;

        } else {

            stepX =
                    (double) chartWidth
                    / (count - 1);
        }

        // =========================
        // LINE
        // =========================

        Path2D path = new Path2D.Double();

        for (int i = 0; i < count; i++) {

            double value = values.get(i);

            double x =
                    left + (i * stepX);

            double y =
                    top
                    + chartHeight
                    - ((value / maxValue)
                    * chartHeight);

            if (i == 0) {

                path.moveTo(x, y);

            } else {

                path.lineTo(x, y);
            }
        }

        g2.setColor(lineColor);

        g2.setStroke(
                new BasicStroke(
                        lineThickness,
                        BasicStroke.CAP_ROUND,
                        BasicStroke.JOIN_ROUND
                )
        );

        g2.draw(path);

        // =========================
        // POINTS
        // =========================

        for (int i = 0; i < count; i++) {

            double value = values.get(i);

            double x =
                    left + (i * stepX);

            double y =
                    top
                    + chartHeight
                    - ((value / maxValue)
                    * chartHeight);

            if (showPoints) {

                g2.setColor(chartBackground);

                g2.fillOval(
                        (int) x - pointSize / 2,
                        (int) y - pointSize / 2,
                        pointSize,
                        pointSize
                );

                g2.setColor(pointColor);

                g2.fillOval(
                        (int) x - pointSize / 2,
                        (int) y - pointSize / 2,
                        pointSize,
                        pointSize
                );
            }

            // =========================
            // VALUE
            // =========================

            if (showValues) {

                g2.setColor(textColor);

                g2.setFont(
                        new Font(
                                "SansSerif",
                                Font.PLAIN,
                                11
                        )
                );

                String valueText =
                        String.valueOf(
                                values.get(i).intValue()
                        );

                g2.drawString(
                        valueText,
                        (int) x - 5,
                        (int) y - 10
                );
            }

            // =========================
            // X LABEL
            // =========================

            if (i < labels.size()) {

                g2.setColor(textColor);

                g2.setFont(
                        new Font(
                                "SansSerif",
                                Font.PLAIN,
                                11
                        )
                );

                String label = labels.get(i);

                int labelWidth =
                        g2.getFontMetrics()
                                .stringWidth(label);

                g2.drawString(
                        label,
                        (int) x - labelWidth / 2,
                        height - 30
                );
            }
        }

        // =========================
        // AXIS TITLES
        // =========================

        g2.setColor(textColor);

        g2.setFont(
                new Font(
                        "SansSerif",
                        Font.PLAIN,
                        11
                )
        );

        if (!xAxisTitle.isEmpty()) {

            int textWidth =
                    g2.getFontMetrics()
                            .stringWidth(xAxisTitle);

            g2.drawString(
                    xAxisTitle,
                    left
                    + (chartWidth - textWidth) / 2,
                    height - 8
            );
        }

        if (!yAxisTitle.isEmpty()) {

            g2.drawString(
                    yAxisTitle,
                    5,
                    top - 10
            );
        }

        g2.dispose();
    }

}
