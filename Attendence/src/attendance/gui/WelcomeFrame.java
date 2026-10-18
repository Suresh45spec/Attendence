package attendance.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import attendance.service.AttendanceService;
import attendance.service.AuthService;
import attendance.util.UITheme;

/**
 * First screen: choose Teacher Login, Student Login or Registration.
 */
public class WelcomeFrame extends JFrame {

    public WelcomeFrame(AuthService authService, AttendanceService attendanceService) {
        setTitle("Defaulter List Generator - Student Attendance Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(720, 480);
        setLocationRelativeTo(null);
        getContentPane().setBackground(UITheme.BACKGROUND);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(UITheme.BACKGROUND);
        root.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        JLabel heading = new JLabel("Student Attendance Management System", JLabel.CENTER);
        heading.setFont(UITheme.TITLE);
        heading.setForeground(UITheme.PRIMARY_DARK);

        JLabel sub = new JLabel("Defaulter List Generator", JLabel.CENTER);
        sub.setFont(UITheme.HEADING);
        sub.setForeground(UITheme.MUTED);

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.add(heading, BorderLayout.NORTH);
        header.add(sub, BorderLayout.SOUTH);

        JPanel cards = new JPanel(new GridBagLayout());
        cards.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 12, 10, 12);

        gbc.gridx = 0;
        cards.add(roleCard("Teacher Login", "Username and password", UITheme.TEACHER_GREEN, () -> {
            dispose();
            new LoginFrame(authService, attendanceService, "Teacher").setVisible(true);
        }), gbc);

        gbc.gridx = 1;
        cards.add(roleCard("Student Login", "Roll number and password", UITheme.STUDENT_TEAL, () -> {
            dispose();
            new LoginFrame(authService, attendanceService, "Student").setVisible(true);
        }), gbc);

        JPanel footer = new JPanel(new FlowLayout());
        footer.setOpaque(false);
        JLabel noAccount = new JLabel("Don't have an account?");
        noAccount.setFont(UITheme.NORMAL);
        JButton register = GuiFactory.linkButton("Register");
        register.addActionListener(e -> {
            dispose();
            new RegisterFrame(authService, attendanceService).setVisible(true);
        });
        footer.add(noAccount);
        footer.add(register);

        root.add(header, BorderLayout.NORTH);
        root.add(cards, BorderLayout.CENTER);
        root.add(footer, BorderLayout.SOUTH);
        add(root);
    }

    private JPanel roleCard(String title, String hint, Color color, Runnable onClick) {
        JPanel card = new JPanel(new BorderLayout(0, 10));
        card.setBackground(UITheme.CARD);
        card.setBorder(UITheme.cardBorder());
        card.setPreferredSize(new Dimension(250, 180));

        JLabel titleLabel = GuiFactory.title(title, color);
        JLabel hintLabel = new JLabel(hint, JLabel.CENTER);
        hintLabel.setFont(UITheme.SMALL);
        hintLabel.setForeground(UITheme.MUTED);

        JButton button = GuiFactory.primaryButton("OPEN", color);
        button.setPreferredSize(new Dimension(180, 36));
        button.addActionListener(e -> onClick.run());

        JPanel buttonWrap = new JPanel();
        buttonWrap.setOpaque(false);
        buttonWrap.add(button);

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(hintLabel, BorderLayout.CENTER);
        card.add(buttonWrap, BorderLayout.SOUTH);
        return card;
    }
}
