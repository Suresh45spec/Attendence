package attendance.util;

import java.awt.Color;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.UIManager;
import javax.swing.border.Border;

/**
 * Shared colours and fonts so every screen looks consistent.
 */
public class UITheme {

    public static final Color PRIMARY = new Color(30, 136, 229);
    public static final Color PRIMARY_DARK = new Color(21, 101, 192);
    public static final Color TEACHER_GREEN = new Color(46, 125, 50);
    public static final Color STUDENT_TEAL = new Color(0, 121, 107);
    public static final Color BACKGROUND = new Color(245, 247, 250);
    public static final Color CARD = Color.WHITE;
    public static final Color SIDEBAR = new Color(25, 42, 86);
    public static final Color SIDEBAR_HOVER = new Color(36, 59, 117);
    public static final Color TEXT = new Color(33, 37, 41);
    public static final Color MUTED = new Color(108, 117, 125);
    public static final Color ELIGIBLE = new Color(46, 125, 50);
    public static final Color DEFAULTER = new Color(245, 124, 0);
    public static final Color CRITICAL = new Color(198, 40, 40);
    public static final Color CARD_BLUE = new Color(30, 136, 229);
    public static final Color CARD_GREEN = new Color(67, 160, 71);
    public static final Color CARD_ORANGE = new Color(251, 140, 0);
    public static final Color CARD_RED = new Color(229, 57, 53);

    public static final Font TITLE = new Font("Segoe UI", Font.BOLD, 22);
    public static final Font HEADING = new Font("Segoe UI", Font.BOLD, 18);
    public static final Font NORMAL = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font BOLD = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font SMALL = new Font("Segoe UI", Font.PLAIN, 12);

    private UITheme() {
    }

    public static void applyLookAndFeel() {
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception ignored) {
            // If Nimbus is not available, Java will use the default look
        }
    }

    public static Border cardBorder() {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(222, 226, 230)),
                BorderFactory.createEmptyBorder(16, 16, 16, 16)
        );
    }
}
