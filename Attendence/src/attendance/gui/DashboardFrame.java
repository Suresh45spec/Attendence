package attendance.gui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import attendance.gui.panels.AddStudentPanel;
import attendance.gui.panels.DashboardHomePanel;
import attendance.gui.panels.DefaulterListPanel;
import attendance.gui.panels.MarkAttendancePanel;
import attendance.gui.panels.MyAttendancePanel;
import attendance.gui.panels.ProfilePanel;
import attendance.gui.panels.RefreshablePanel;
import attendance.gui.panels.ReportsPanel;
import attendance.gui.panels.ViewStudentsPanel;
import attendance.model.User;
import attendance.service.AttendanceService;
import attendance.service.AuthService;
import attendance.util.UITheme;

/**
 * Main window after login. Teachers and students see different menus.
 */
public class DashboardFrame extends JFrame {

    private final CardLayout cards = new CardLayout();
    private final JPanel content = new JPanel(cards);
    private final ArrayList<RefreshablePanel> refreshablePanels = new ArrayList<RefreshablePanel>();
    private JButton[] menuButtons;

    public DashboardFrame(AuthService authService, AttendanceService attendanceService, User user) {
        setTitle("Defaulter List Generator - Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 680);
        setLocationRelativeTo(null);

        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(UITheme.SIDEBAR);
        sidebar.setPreferredSize(new Dimension(210, 0));
        sidebar.setBorder(BorderFactory.createEmptyBorder(16, 0, 16, 0));

        JLabel brand = new JLabel("  Attendance");
        brand.setFont(UITheme.HEADING);
        brand.setForeground(Color.WHITE);
        JLabel role = new JLabel("  " + user.getRole());
        role.setFont(UITheme.SMALL);
        role.setForeground(new Color(176, 190, 197));
        sidebar.add(brand);
        sidebar.add(role);
        sidebar.add(Box.createVerticalStrut(20));

        AddStudentPanel addStudentPanel = new AddStudentPanel(attendanceService, this::refreshAll);
        MarkAttendancePanel markPanel = new MarkAttendancePanel(attendanceService, this::refreshAll);
        ViewStudentsPanel viewPanel = new ViewStudentsPanel(attendanceService);
        DefaulterListPanel defaulterPanel = new DefaulterListPanel(attendanceService);
        ReportsPanel reportsPanel = new ReportsPanel(attendanceService);
        ProfilePanel profilePanel = new ProfilePanel(user);
        MyAttendancePanel myAttendancePanel = new MyAttendancePanel(attendanceService, user);
        DashboardHomePanel homePanel = new DashboardHomePanel(
                attendanceService,
                user,
                () -> showCard("mark", findButton("Mark Attendance")),
                () -> showCard("defaulters", findButton("Defaulter List")),
                () -> showCard("reports", findButton("Reports"))
        );

        addCard("home", homePanel);
        addCard("add", addStudentPanel);
        addCard("mark", markPanel);
        addCard("view", viewPanel);
        addCard("defaulters", defaulterPanel);
        addCard("reports", reportsPanel);
        addCard("mine", myAttendancePanel);
        addCard("profile", profilePanel);

        ArrayList<JButton> buttons = new ArrayList<JButton>();
        buttons.add(menu(sidebar, "Dashboard", () -> showCard("home", buttons.get(0))));

        if (user.isTeacher()) {
            buttons.add(menu(sidebar, "Add Student", () -> showCard("add", findNamed(buttons, "Add Student"))));
            buttons.add(menu(sidebar, "Mark Attendance", () -> showCard("mark", findNamed(buttons, "Mark Attendance"))));
            buttons.add(menu(sidebar, "View Students", () -> showCard("view", findNamed(buttons, "View Students"))));
            buttons.add(menu(sidebar, "Defaulter List", () -> showCard("defaulters", findNamed(buttons, "Defaulter List"))));
            buttons.add(menu(sidebar, "Reports", () -> showCard("reports", findNamed(buttons, "Reports"))));
        } else {
            buttons.add(menu(sidebar, "My Attendance", () -> showCard("mine", findNamed(buttons, "My Attendance"))));
            buttons.add(menu(sidebar, "Defaulter List", () -> showCard("defaulters", findNamed(buttons, "Defaulter List"))));
        }

        buttons.add(menu(sidebar, "Profile", () -> showCard("profile", findNamed(buttons, "Profile"))));
        sidebar.add(Box.createVerticalGlue());
        buttons.add(menu(sidebar, "Logout", () -> {
            dispose();
            new WelcomeFrame(authService, attendanceService).setVisible(true);
        }));

        menuButtons = buttons.toArray(new JButton[0]);
        GuiFactory.highlightSidebar(menuButtons[0], menuButtons);

        JPanel main = new JPanel(new BorderLayout());
        main.setBackground(UITheme.BACKGROUND);
        main.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        main.add(content, BorderLayout.CENTER);

        add(sidebar, BorderLayout.WEST);
        add(main, BorderLayout.CENTER);
        cards.show(content, "home");
    }

    private void addCard(String name, JPanel panel) {
        content.add(panel, name);
        if (panel instanceof RefreshablePanel) {
            refreshablePanels.add((RefreshablePanel) panel);
        }
    }

    private JButton menu(JPanel sidebar, String text, Runnable action) {
        JButton button = GuiFactory.sidebarButton(text);
        button.addActionListener(e -> action.run());
        sidebar.add(button);
        sidebar.add(Box.createVerticalStrut(4));
        return button;
    }

    private void showCard(String name, JButton selected) {
        refreshAll();
        cards.show(content, name);
        if (selected != null) {
            GuiFactory.highlightSidebar(selected, menuButtons);
        }
    }

    private void refreshAll() {
        for (RefreshablePanel panel : refreshablePanels) {
            panel.refreshData();
        }
    }

    private JButton findButton(String text) {
        if (menuButtons == null) {
            return null;
        }
        for (JButton button : menuButtons) {
            if (text.equals(button.getText())) {
                return button;
            }
        }
        return null;
    }

    private JButton findNamed(ArrayList<JButton> buttons, String text) {
        for (JButton button : buttons) {
            if (text.equals(button.getText())) {
                return button;
            }
        }
        return null;
    }
}
