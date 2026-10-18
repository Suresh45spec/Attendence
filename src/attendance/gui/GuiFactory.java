package attendance.gui;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import attendance.util.UITheme;

/**
 * Small factory methods that build common Swing controls.
 */
public class GuiFactory {

    private GuiFactory() {
    }

    public static JTextField textField() {
        JTextField field = new JTextField();
        field.setFont(UITheme.NORMAL);
        field.setPreferredSize(new Dimension(260, 34));
        return field;
    }

    public static JPasswordField passwordField() {
        JPasswordField field = new JPasswordField();
        field.setFont(UITheme.NORMAL);
        field.setPreferredSize(new Dimension(260, 34));
        return field;
    }

    public static JButton primaryButton(String text, Color background) {
        JButton button = new JButton(text);
        button.setFont(UITheme.BOLD);
        button.setBackground(background);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(260, 38));
        return button;
    }

    public static JButton linkButton(String text) {
        JButton button = new JButton(text);
        button.setFont(UITheme.SMALL);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setForeground(UITheme.PRIMARY);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setFocusPainted(false);
        return button;
    }

    public static JLabel title(String text, Color color) {
        JLabel label = new JLabel(text, JLabel.CENTER);
        label.setFont(UITheme.TITLE);
        label.setForeground(color);
        return label;
    }

    public static JButton sidebarButton(String text) {
        JButton button = new JButton(text);
        button.setFont(UITheme.BOLD);
        button.setForeground(Color.WHITE);
        button.setBackground(UITheme.SIDEBAR);
        button.setBorder(new EmptyBorder(12, 18, 12, 18));
        button.setHorizontalAlignment(JButton.LEFT);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        return button;
    }

    public static void highlightSidebar(JButton selected, JButton[] all) {
        for (JButton button : all) {
            button.setBackground(UITheme.SIDEBAR);
            button.setBorder(new EmptyBorder(12, 18, 12, 18));
        }
        selected.setBackground(UITheme.SIDEBAR_HOVER);
        selected.setBorder(new LineBorder(UITheme.PRIMARY, 0));
    }
}
