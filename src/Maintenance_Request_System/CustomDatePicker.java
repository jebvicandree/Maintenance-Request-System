
package Maintenance_Request_System;

import java.awt.*;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class CustomDatePicker extends JPanel {

    private final Color GREEN = new Color(39, 120, 83);
    private final Color LIGHT_GREEN = new Color(232, 245, 238);
    private final Color WHITE = Color.WHITE;
    private final Color TEXT = new Color(45, 55, 50);
    private final Color BORDER = new Color(210, 220, 214);

    private LocalDate selectedDate = LocalDate.now();
    private YearMonth displayedMonth = YearMonth.now();

    private final JTextField dateField;
    private final JButton calendarButton;

    private JWindow calendarWindow;
    private final JComboBox<String> monthBox;
    private final JComboBox<Integer> yearBox;
    private final JPanel daysPanel;

    private final DateTimeFormatter formatter =
            DateTimeFormatter.ofPattern("MM/dd/yyyy");

    private final String[] months = {
        "January", "February", "March", "April",
        "May", "June", "July", "August",
        "September", "October", "November", "December"
    };

    public CustomDatePicker() {

        setLayout(new BorderLayout(6, 0));
        setOpaque(false);

        // DATE FIELD
        dateField = new JTextField(
                selectedDate.format(formatter));

        dateField.setEditable(false);
        dateField.setFocusable(false);
        dateField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        dateField.setForeground(TEXT);
        dateField.setBackground(WHITE);

        dateField.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(BORDER),
                        new EmptyBorder(8, 10, 8, 10)
                )
        );

        // CALENDAR BUTTON
        calendarButton = new JButton("\u25A6");

        calendarButton.setFont(
                new Font("Segoe UI", Font.PLAIN, 17));

        calendarButton.setForeground(WHITE);
        calendarButton.setBackground(GREEN);
        calendarButton.setFocusPainted(false);
        calendarButton.setBorderPainted(false);
        calendarButton.setCursor(
                Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        calendarButton.setPreferredSize(
                new Dimension(42, 38));

        calendarButton.setToolTipText("Choose date");

        add(dateField, BorderLayout.CENTER);
        add(calendarButton, BorderLayout.EAST);

        // CALENDAR WINDOW
        calendarWindow = new JWindow();

        JPanel calendar = new JPanel(
                new BorderLayout(0, 10));

        calendar.setBackground(WHITE);

        calendar.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(BORDER),
                        new EmptyBorder(12, 12, 12, 12)
                )
        );

        // MONTH AND YEAR HEADER
        JPanel header = new JPanel(
                new GridLayout(1, 2, 8, 0));

        header.setBackground(GREEN);
        header.setBorder(
                new EmptyBorder(10, 8, 10, 8));

        monthBox = new JComboBox<>(months);
        yearBox = new JComboBox<>();

        for (int year = 1900; year <= 2100; year++) {
            yearBox.addItem(year);
        }

        styleCombo(monthBox);
        styleCombo(yearBox);

        header.add(monthBox);
        header.add(yearBox);

        // CALENDAR DAYS
        daysPanel = new JPanel(
                new GridLayout(0, 7, 4, 4));

        daysPanel.setBackground(WHITE);

        calendar.add(header, BorderLayout.NORTH);
        calendar.add(daysPanel, BorderLayout.CENTER);

        calendarWindow.setContentPane(calendar);
        calendarWindow.setFocusableWindowState(true);

        // INITIAL MONTH AND YEAR
        monthBox.setSelectedIndex(
                selectedDate.getMonthValue() - 1);

        yearBox.setSelectedItem(
                selectedDate.getYear());

        // KEEP WINDOW OPEN WHEN CHANGING MONTH OR YEAR
        monthBox.addActionListener(e -> updateCalendar());
        yearBox.addActionListener(e -> updateCalendar());

        // OPEN CALENDAR
        calendarButton.addActionListener(e -> {

            if (calendarWindow.isVisible()) {
                calendarWindow.setVisible(false);
            } else {
                updateCalendar();
                showCalendar();
            }
        });

        // INITIALIZE CALENDAR
        updateCalendar();
    }

    // STYLE MONTH AND YEAR DROPDOWNS
    private void styleCombo(JComboBox<?> combo) {

        combo.setFont(
                new Font("Segoe UI", Font.PLAIN, 13));

        combo.setBackground(WHITE);
        combo.setForeground(GREEN);
        combo.setFocusable(false);
    }

    // SHOW CALENDAR BELOW THE DATE FIELD
    private void showCalendar() {

        Window owner = SwingUtilities.getWindowAncestor(this);

        if (owner != null) {
            calendarWindow.setFocusableWindowState(true);
        }

        Point location = calendarButton.getLocationOnScreen();

        calendarWindow.pack();

        calendarWindow.setLocation(
                location.x,
                location.y + calendarButton.getHeight()
        );

        calendarWindow.setVisible(true);
        calendarWindow.toFront();
    }

    // UPDATE CALENDAR DISPLAY
    private void updateCalendar() {

        if (monthBox.getSelectedIndex() < 0
                || yearBox.getSelectedItem() == null) {
            return;
        }

        int month = monthBox.getSelectedIndex() + 1;
        int year = (Integer) yearBox.getSelectedItem();

        displayedMonth = YearMonth.of(year, month);

        daysPanel.removeAll();

        // WEEKDAY HEADINGS
        String[] weekdays = {
            "Sun", "Mon", "Tue", "Wed",
            "Thu", "Fri", "Sat"
        };

        for (String weekday : weekdays) {

            JLabel label = new JLabel(
                    weekday, SwingConstants.CENTER);

            label.setFont(
                    new Font("Segoe UI", Font.BOLD, 11));

            label.setForeground(GREEN);

            daysPanel.add(label);
        }

        // FIRST DAY OFFSET
        int offset = displayedMonth.atDay(1)
                .getDayOfWeek().getValue() % 7;

        for (int i = 0; i < offset; i++) {
            daysPanel.add(new JLabel(""));
        }

        // MONTH DATES
        for (int day = 1;
                day <= displayedMonth.lengthOfMonth();
                day++) {

            final int chosenDay = day;

            LocalDate date = displayedMonth.atDay(day);

            JButton button = new JButton(
                    String.valueOf(day));

            button.setFont(
                    new Font("Segoe UI", Font.PLAIN, 12));

            button.setFocusPainted(false);
            button.setBorderPainted(false);
            button.setOpaque(true);

            button.setCursor(
                    Cursor.getPredefinedCursor(
                            Cursor.HAND_CURSOR));

            button.setMargin(
                    new Insets(5, 5, 5, 5));

            // SELECTED DATE
            if (date.equals(selectedDate)) {

                button.setBackground(GREEN);
                button.setForeground(WHITE);

            } else if (date.equals(LocalDate.now())) {

                button.setBackground(LIGHT_GREEN);
                button.setForeground(GREEN);

            } else {

                button.setBackground(WHITE);
                button.setForeground(TEXT);
            }

            // SELECT A DATE AND CLOSE WINDOW
            button.addActionListener(e -> {

                selectedDate =
                        displayedMonth.atDay(chosenDay);

                dateField.setText(
                        selectedDate.format(formatter));

                calendarWindow.setVisible(false);

                updateCalendar();
            });

            daysPanel.add(button);
        }

        daysPanel.revalidate();
        daysPanel.repaint();

        calendarWindow.pack();
    }

    // GET SELECTED DATE AS LOCALDATE
    public LocalDate getDate() {
        return selectedDate;
    }

    // GET SELECTED DATE AS MM/DD/YYYY
    public String getDateString() {
        return selectedDate.format(formatter);
    }

    // SET DATE PROGRAMMATICALLY
    public void setDate(LocalDate date) {

        if (date == null) {
            throw new IllegalArgumentException(
                    "Date cannot be null.");
        }

        selectedDate = date;

        monthBox.setSelectedIndex(
                date.getMonthValue() - 1);

        yearBox.setSelectedItem(
                date.getYear());

        dateField.setText(
                date.format(formatter));

        updateCalendar();
    }
}
