package attendance.gui.panels;

import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;

import attendance.model.User;
import attendance.util.UITheme;

public class ProfilePanel extends JPanel implements RefreshablePanel {

    public ProfilePanel(User user) {
        setLayout(new BorderLayout());
        setBackground(UITheme.BACKGROUND);

        JPanel card = new JPanel(new GridLayout(0, 1, 8, 8));
        card.setBackground(UITheme.CARD);
        card.setBorder(UITheme.cardBorder());

        card.add(GuiFactoryTitle("My Profile"));
        card.add(line("Full Name", user.getFullName()));
        card.add(line(user.isTeacher() ? "Username" : "Roll Number", user.getUsername()));
        card.add(line("Email ID", user.getEmail()));
        card.add(line("Role", user.getRole()));

        JPanel wrap = new JPanel();
        wrap.setOpaque(false);
        wrap.add(card);
        add(wrap, BorderLayout.NORTH);
    }

    private JLabel GuiFactoryTitle(String text) {
        JLabel label = new JLabel(text);
        label.setFont(UITheme.HEADING);
        label.setForeground(UITheme.PRIMARY_DARK);
        return label;
    }

    private JLabel line(String key, String value) {
        JLabel label = new JLabel(key + ":  " + value);
        label.setFont(UITheme.NORMAL);
        return label;
    }

    @Override
    public void refreshData() {
    }
}
