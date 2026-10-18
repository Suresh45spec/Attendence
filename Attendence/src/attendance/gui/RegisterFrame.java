package attendance.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import attendance.model.User;
import attendance.service.AttendanceService;
import attendance.service.AuthService;
import attendance.util.UITheme;

/**
 * Common registration page for Teachers and Students.
 */
public class RegisterFrame extends JFrame {

    private final AuthService authService;
    private final AttendanceService attendanceService;

    private final JTextField nameField = GuiFactory.textField();
    private final JTextField emailField = GuiFactory.textField();
    private final JTextField usernameField = GuiFactory.textField();
    private final JPasswordField passwordField = GuiFactory.passwordField();
    private final JPasswordField confirmField = GuiFactory.passwordField();
    private final JRadioButton teacherRadio = new JRadioButton("Teacher");
    private final JRadioButton studentRadio = new JRadioButton("Student");

    public RegisterFrame(AuthService authService, AttendanceService attendanceService) {
        this.authService = authService;
        this.attendanceService = attendanceService;

        setTitle("Create New Account");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(520, 640);
        setLocationRelativeTo(null);

        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(UITheme.CARD);
        card.setBorder(UITheme.cardBorder());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 8, 6, 8);

        gbc.gridy = 0;
        card.add(GuiFactory.title("Create New Account", UITheme.TEACHER_GREEN), gbc);

        gbc.gridy = 1;
        card.add(labeled("Full Name", nameField), gbc);
        gbc.gridy = 2;
        card.add(labeled("Email ID", emailField), gbc);
        gbc.gridy = 3;
        card.add(labeled("Username / Roll No.", usernameField), gbc);
        gbc.gridy = 4;
        card.add(labeled("Password", passwordField), gbc);
        gbc.gridy = 5;
        card.add(labeled("Confirm Password", confirmField), gbc);

        teacherRadio.setOpaque(false);
        studentRadio.setOpaque(false);
        teacherRadio.setFont(UITheme.NORMAL);
        studentRadio.setFont(UITheme.NORMAL);
        ButtonGroup group = new ButtonGroup();
        group.add(teacherRadio);
        group.add(studentRadio);
        teacherRadio.setSelected(true);

        JPanel roleRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        roleRow.setOpaque(false);
        JLabel roleLabel = new JLabel("Role:");
        roleLabel.setFont(UITheme.BOLD);
        roleRow.add(roleLabel);
        roleRow.add(teacherRadio);
        roleRow.add(studentRadio);
        gbc.gridy = 6;
        card.add(roleRow, gbc);

        JButton register = GuiFactory.primaryButton("REGISTER", UITheme.PRIMARY);
        register.addActionListener(e -> doRegister());
        gbc.gridy = 7;
        card.add(register, gbc);

        JPanel loginRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 0));
        loginRow.setOpaque(false);
        JLabel haveAccount = new JLabel("Already have an account?");
        haveAccount.setFont(UITheme.SMALL);
        JButton login = GuiFactory.linkButton("Login");
        login.addActionListener(e -> goHome());
        loginRow.add(haveAccount);
        loginRow.add(login);
        gbc.gridy = 8;
        card.add(loginRow, gbc);

        JPanel wrap = new JPanel(new GridBagLayout());
        wrap.setBackground(UITheme.BACKGROUND);
        wrap.setBorder(BorderFactory.createEmptyBorder(24, 40, 24, 40));
        wrap.add(card);
        add(wrap);
    }

    private JPanel labeled(String text, java.awt.Component field) {
        JPanel panel = new JPanel(new BorderLayout(0, 4));
        panel.setOpaque(false);
        JLabel label = new JLabel(text);
        label.setFont(UITheme.BOLD);
        panel.add(label, BorderLayout.NORTH);
        panel.add(field, BorderLayout.CENTER);
        return panel;
    }

    private void doRegister() {
        String role = teacherRadio.isSelected() ? User.ROLE_TEACHER : User.ROLE_STUDENT;
        String error = authService.register(
                nameField.getText(),
                emailField.getText(),
                usernameField.getText(),
                new String(passwordField.getPassword()),
                new String(confirmField.getPassword()),
                role
        );

        if (error != null) {
            JOptionPane.showMessageDialog(this, error, "Registration", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JOptionPane.showMessageDialog(this, "Account created successfully. You can now login.", "Registration", JOptionPane.INFORMATION_MESSAGE);
        goHome();
    }

    private void goHome() {
        dispose();
        new WelcomeFrame(authService, attendanceService).setVisible(true);
    }
}
