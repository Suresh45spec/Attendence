package attendance.gui.panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import attendance.gui.GuiFactory;
import attendance.model.Student;
import attendance.model.User;
import attendance.service.AttendanceService;
import attendance.util.AttendanceCalculator;
import attendance.util.UITheme;

public class DashboardHomePanel extends JPanel implements RefreshablePanel {

    private final AttendanceService attendanceService;
    private final User currentUser;
    private final Runnable openMarkAttendance;
    private final Runnable openDefaulters;
    private final Runnable openReports;

    private final JLabel totalLabel = valueLabel();
    private final JLabel presentLabel = valueLabel();
    private final JLabel defaulterLabel = valueLabel();
    private final JLabel criticalLabel = valueLabel();
    private final JPanel alertPanel = new JPanel();

    public DashboardHomePanel(AttendanceService attendanceService, User currentUser,
            Runnable openMarkAttendance, Runnable openDefaulters, Runnable openReports) {
        this.attendanceService = attendanceService;
        this.currentUser = currentUser;
        this.openMarkAttendance = openMarkAttendance;
        this.openDefaulters = openDefaulters;
        this.openReports = openReports;

        setLayout(new BorderLayout(16, 16));
        setBackground(UITheme.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel welcome = new JLabel("Welcome, " + currentUser.getFullName() + " !");
        welcome.setFont(UITheme.TITLE);
        welcome.setForeground(UITheme.PRIMARY_DARK);

        JPanel cards = new JPanel(new GridLayout(1, 4, 12, 12));
        cards.setOpaque(false);
        cards.add(statCard("Total Students", totalLabel, UITheme.CARD_BLUE));
        cards.add(statCard("Present Today", presentLabel, UITheme.CARD_GREEN));
        cards.add(statCard("Attendance < 75%", defaulterLabel, UITheme.CARD_ORANGE));
        cards.add(statCard("Critical (< 65%)", criticalLabel, UITheme.CARD_RED));

        JPanel quick = new JPanel();
        quick.setLayout(new BoxLayout(quick, BoxLayout.Y_AXIS));
        quick.setBackground(UITheme.CARD);
        quick.setBorder(UITheme.cardBorder());
        JLabel quickTitle = new JLabel("Quick Actions");
        quickTitle.setFont(UITheme.HEADING);
        quick.add(quickTitle);
        quick.add(Box.createVerticalStrut(10));

        if (currentUser.isTeacher()) {
            quick.add(actionButton("Mark Today's Attendance", openMarkAttendance));
            quick.add(Box.createVerticalStrut(8));
            quick.add(actionButton("View Defaulter List", openDefaulters));
            quick.add(Box.createVerticalStrut(8));
            quick.add(actionButton("Generate Reports", openReports));
        } else {
            JLabel studentNote = new JLabel("Use the menu to view your attendance and profile.");
            studentNote.setFont(UITheme.NORMAL);
            studentNote.setForeground(UITheme.MUTED);
            quick.add(studentNote);
        }

        alertPanel.setLayout(new BoxLayout(alertPanel, BoxLayout.Y_AXIS));
        alertPanel.setOpaque(false);

        JPanel center = new JPanel(new BorderLayout(12, 12));
        center.setOpaque(false);
        center.add(cards, BorderLayout.NORTH);
        center.add(quick, BorderLayout.CENTER);
        center.add(alertPanel, BorderLayout.SOUTH);

        add(welcome, BorderLayout.NORTH);
        add(center, BorderLayout.CENTER);
        refreshData();
    }

    private JPanel statCard(String title, JLabel value, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(color);
        card.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        JLabel titleLabel = new JLabel(title);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(UITheme.SMALL);
        value.setForeground(Color.WHITE);
        card.add(titleLabel, BorderLayout.NORTH);
        card.add(value, BorderLayout.CENTER);
        card.setPreferredSize(new Dimension(150, 90));
        return card;
    }

    private JLabel valueLabel() {
        JLabel label = new JLabel("0");
        label.setFont(new Font("Segoe UI", Font.BOLD, 28));
        return label;
    }

    private JButton actionButton(String text, Runnable action) {
        JButton button = GuiFactory.primaryButton(text, UITheme.PRIMARY);
        button.setMaximumSize(new Dimension(280, 38));
        button.addActionListener(e -> action.run());
        JPanel wrap = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        wrap.setOpaque(false);
        wrap.add(button);
        return button;
    }

    @Override
    public void refreshData() {
        totalLabel.setText(String.valueOf(attendanceService.getAllStudents().size()));
        presentLabel.setText(String.valueOf(attendanceService.getPresentToday()));
        defaulterLabel.setText(String.valueOf(attendanceService.getDefaulters().size()));
        criticalLabel.setText(String.valueOf(attendanceService.getCriticalStudents().size()));

        alertPanel.removeAll();
        ArrayList<Student> critical = attendanceService.getCriticalStudents();
        if (!critical.isEmpty()) {
            JPanel box = new JPanel(new BorderLayout());
            box.setBackground(new Color(255, 235, 238));
            box.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
            JLabel alert = new JLabel("Critical attendance alert: " + critical.size()
                    + " student(s) are below " + (int) AttendanceCalculator.CRITICAL_PERCENT + "%.");
            alert.setFont(UITheme.BOLD);
            alert.setForeground(UITheme.CRITICAL);
            box.add(alert, BorderLayout.CENTER);
            alertPanel.add(box);
        }
        alertPanel.revalidate();
        alertPanel.repaint();
    }
}
