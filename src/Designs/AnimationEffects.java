/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Designs;

import java.awt.Color;
import java.awt.Window;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;


public class AnimationEffects {
    



    // 1. FADE IN: JFrame or JDialog
    public static void fadeIn(Window window) {
        window.setVisible(true);

        try {
            window.setOpacity(0.0f);

            Timer timer = new Timer(15, null);

            timer.addActionListener(e -> {
                float opacity = window.getOpacity();

                if (opacity >= 0.95f) {
                    window.setOpacity(1.0f);
                    timer.stop();
                } else {
                    window.setOpacity(
                        Math.min(1.0f, opacity + 0.05f)
                    );
                }
            });

            timer.start();

        } catch (UnsupportedOperationException
                 | java.awt.IllegalComponentStateException ex) {
            // If fading is unsupported, keep the window visible.
            window.setVisible(true);
        }
    }

    // 2. FADE OUT, THEN RUN AN ACTION
    public static void fadeOut(
            Window window, Runnable afterFade) {

        final float[] opacity = {1.0f};

        Timer timer = new Timer(15, null);

        timer.addActionListener(e -> {
            opacity[0] -= 0.08f;

            if (opacity[0] <= 0.0f) {
                timer.stop();
                window.setVisible(false);
                window.setOpacity(1.0f);
                afterFade.run();
            } else {
                try {
                    window.setOpacity(opacity[0]);
                } catch (UnsupportedOperationException ex) {
                    timer.stop();
                    window.setVisible(false);
                    afterFade.run();
                }
            }
        });

        timer.start();
    }

    // 3. BUTTON HOVER EFFECT
    public static void addButtonHover(
            JButton button,
            Color normalColor,
            Color hoverColor) {

        button.setBackground(normalColor);
        button.setOpaque(true);

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(hoverColor);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(normalColor);
            }
        });
    }

    // 4. PANEL HOVER EFFECT
    public static void addPanelHover(
            JPanel panel,
            Color normalBorder,
            Color hoverBorder) {

        Border normal = new LineBorder(normalBorder, 1, true);
        Border hover = new LineBorder(hoverBorder, 2, true);

        panel.setBorder(normal);

        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                panel.setBorder(hover);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                panel.setBorder(normal);
            }
        });
    }
//buttons effect
public static void switchScreen(Window current, Window next) {

    next.setLocationRelativeTo(null);

    // Make the next screen transparent before showing it
    try {
        next.setOpacity(0.0f);
    } catch (UnsupportedOperationException
             | java.awt.IllegalComponentStateException ex) {
        // Opacity may not be supported on this system
    }

    next.setVisible(true);

    // Fade out the current screen
    final float[] opacity = {1.0f};

    Timer timer = new Timer(15, null);

    timer.addActionListener(e -> {
        opacity[0] -= 0.08f;

        if (opacity[0] <= 0.0f) {
            timer.stop();

            current.dispose();

            // Fade in the next screen
            fadeIn(next);

        } else {
            try {
                current.setOpacity(opacity[0]);
            } catch (UnsupportedOperationException
                     | java.awt.IllegalComponentStateException ex) {
                timer.stop();
                current.dispose();
                fadeIn(next);
            }
        }
    });

    timer.start();
}

}
