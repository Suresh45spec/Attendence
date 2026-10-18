package attendance.gui;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import attendance.service.AuthService;
import attendance.util.UITheme;

/**
 * Basic forgot-password dialog: username + email, then set a new password.
 */
public class ForgotPasswordDialog extends JDialog {

    public ForgotPasswordDialog(JFrame parent, AuthService authService) {
        super(parent, "Reset Password", true);
        setSize(420, 360);
        setLocationRelativeTo(parent);

        JTextField username = GuiFactory.textField();
        JTextField email = GuiFactory.textField();
        JPasswordField password = GuiFactory.passwordField();
        JPasswordField confirm = GuiFactory.passwordField();

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(UITheme.CARD);
        form.setBorder(UITheme.cardBorder());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 6, 6, 6);

        gbc.gridy = 0;
        form.add(GuiFactory.title("Forgot Password", UITheme.PRIMARY_DARK), gbc);
        gbc.gridy = 1;
        form.add(labeled("Username / Roll No.", username), gbc);
        gbc.gridy = 2;
        form.add(labeled("Registered Email ID", email), gbc);
        gbc.gridy = 3;
        form.add(labeled("New Password", password), gbc);
        gbc.gridy = 4;
        form.add(labeled("Confirm Password", confirm), gbc);

        JButton reset = GuiFactory.primaryButton("RESET PASSWORD", UITheme.PRIMARY);
        reset.addActionListener(e -> {
            String error = authService.resetPassword(
                    username.getText(),
                    email.getText(),
                    new String(password.getPassword()),
                    new String(confirm.getPassword())
            );
            if (error != null) {
                JOptionPane.showMessageDialog(this, error, "Reset Password", JOptionPane.WARNING_MESSAGE);
                return;
            }
            JOptionPane.showMessageDialog(this, "Password updated. Please login with the new password.", "Reset Password", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        });
        gbc.gridy = 5;
        form.add(reset, gbc);

        add(form, BorderLayout.CENTER);
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
}
