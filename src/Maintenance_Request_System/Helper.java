package Maintenance_Request_System;

import java.awt.Component;
import java.awt.Image;
import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

/**
 * Helper = small things that several screens need.
 * Keeping them here means we write them only once.
 */
public class Helper {

    // The same lists are used by Sign Up AND the Profile page,
    // so what the student picks at sign up always matches the profile.
    public static final String[] DEPARTMENTS = {
        "STEM", "HUMMS", "GAS", "TVL", "ICT", "SPORTS", "ARTS AND DESIGN",
        "BSIT", "COA", "CBA", "CEA", "CAS", "BSBA", "BSA", "BSN", "BSCE", "BSCRIM", "CAHS"
    };

    public static final String[] YEAR_LEVELS = {
        "Grade 11", "Grade 12", "1st Year", "2nd Year", "3rd Year", "4th Year"
    };

    // Returns a copy of the list with "SELECT" added at the top (for Sign Up)
    public static String[] withSelect(String[] items) {
        String[] result = new String[items.length + 1];
        result[0] = "SELECT";
        System.arraycopy(items, 0, result, 1, items.length);
        return result;
    }

    // Today's date as text, e.g. "October 15, 2026"
    public static String todayText() {
        return LocalDate.now().format(DateTimeFormatter.ofPattern("MMMM d, yyyy"));
    }

    // Loads an image file and resizes it. Returns null if the file is missing.
    public static ImageIcon scaledIcon(String path, int width, int height) {
        if (path == null || !new File(path).exists()) {
            return null;
        }
        Image image = new ImageIcon(path).getImage();
        return new ImageIcon(image.getScaledInstance(width, height, Image.SCALE_SMOOTH));
    }

    // One popup for every database error
    public static void showError(Component parent, Exception e) {
        JOptionPane.showMessageDialog(parent,
                "Database error: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        e.printStackTrace();
    }
}
