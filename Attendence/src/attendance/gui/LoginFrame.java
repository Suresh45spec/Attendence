package attendance.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import attendance.model.User;
import attendance.service.AttendanceService;
import attendance.service.AuthService;
import attendance.service.FileStorage;
import attendance.util.UITheme;

/**
 * Login page for Teachers (username) and Students (roll number).
 */
public class LoginFrame extends JFrame {

    private final AuthService authService;
    private final AttendanceService attendanceService;
    private final FileStorage storage;
    private final String role;

    private final JTextField usernameField = GuiFactory.textField();
    private final JPasswordField passwordField = GuiFactory.passwordField();
    private final JCheckBox rememberMe = new JCheckBox("Remember me");

    public LoginFrame(AuthService authService, AttendanceService attendanceService, String role) {
        this.authService = authService;
        this.attendanceService = attendanceService;
        this.storage = new FileStorage();
        this.role = role;

        boolean teacher = User.ROLE_TEACHER.equals(role);
        Color accent = teacher ? UITheme.TEACHER_GREEN : UITheme.STUDENT_TEAL;

        setTitle(role + " Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(480, 520);
        setLocationRelativeTo(null);
        getContentPane().setBackground(UITheme.BACKGROUND);

        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(UITheme.CARD);
        card.setBorder(UITheme.cardBorder());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 8, 8, 8);

        JLabel title = GuiFactory.title(role + " Login", accent);
        gbc.gridy = 0;
        card.add(title, gbc);

        JLabel hint = new JLabel(teacher ? "Enter username and password" : "Enter roll number and password", JLabel.CENTER);
        hint.setFont(UITheme.SMALL);
        hint.setForeground(UITheme.MUTED);
        gbc.gridy = 1;
        card.add(hint, gbc);

        gbc.gridy = 2;
        card.add(labeled(teacher ? "Username" : "Roll Number", usernameField), gbc);

        gbc.gridy = 3;
        card.add(labeled("Password", passwordField), gbc);

        JPanel options = new JPanel(new BorderLayout());
        options.setOpaque(false);
        rememberMe.setFont(UITheme.SMALL);
        rememberMe.setOpaque(false);
        JButton forgot = GuiFactory.linkButton("Forgot Password?");
        forgot.addActionListener(e -> new ForgotPasswordDialog(this, authService).setVisible(true));
        options.add(rememberMe, BorderLayout.WEST);
        options.add(forgot, BorderLayout.EAST);
        gbc.gridy = 4;
        card.add(options, gbc);

        JButton login = GuiFactory.primaryButton("LOGIN", UITheme.PRIMARY);
        login.addActionListener(e -> doLogin());
        gbc.gridy = 5;
        card.add(login, gbc);

        JPanel registerRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 0));
        registerRow.setOpaque(false);
        JLabel noAccount = new JLabel("Don't have an account?");
        noAccount.setFont(UITheme.SMALL);
        JButton register = GuiFactory.linkButton("Register");
        register.addActionListener(e -> {
            dispose();
            new RegisterFrame(authService, attendanceService).setVisible(true);
        });
        registerRow.add(noAccount);
        registerRow.add(register);
        gbc.gridy = 6;
        card.add(registerRow, gbc);

        JButton back = GuiFactory.linkButton("Back to Home");
        back.addActionListener(e -> {
            dispose();
            new WelcomeFrame(authService, attendanceService).setVisible(true);
        });
        gbc.gridy = 7;
        card.add(back, gbc);

        String remembered = storage.loadRememberedUsername(role);
        if (!remembered.isEmpty()) {
            usernameField.setText(remembered);
            rememberMe.setSelected(true);
        }

        JPanel wrap = new JPanel(new GridBagLayout());
        wrap.setBackground(UITheme.BACKGROUND);
        wrap.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        wrap.add(card);
        add(wrap);

        getRootPane().setDefaultButton(login);
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

    private void doLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter both " + (User.ROLE_TEACHER.equals(role) ? "username" : "roll number") + " and password.", "Login", JOptionPane.WARNING_MESSAGE);
            return;
        }

        User user = authService.login(username, password, role);
        if (user == null) {
            JOptionPane.showMessageDialog(this, "Invalid login details. Please try again.", "Login Failed", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            if (rememberMe.isSelected()) {
                storage.rememberUsername(role, username);
            } else {
                storage.clearRememberedUsername(role);
            }
        } catch (Exception e) {
            // Remember-me is optional, so login can still continue
        }

        dispose();
        new DashboardFrame(authService, attendanceService, user).setVisible(true);
    }
}
