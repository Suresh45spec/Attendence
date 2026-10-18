package attendance.gui.panels;

import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;

import attendance.model.Student;
import attendance.model.User;
import attendance.service.AttendanceService;
import attendance.util.AttendanceCalculator;
import attendance.util.UITheme;

public class MyAttendancePanel extends JPanel implements RefreshablePanel {

    private final AttendanceService attendanceService;
    private final User user;
    private final JPanel card = new JPanel(new GridLayout(0, 1, 8, 8));

    public MyAttendancePanel(AttendanceService attendanceService, User user) {
        this.attendanceService = attendanceService;
        this.user = user;
        setLayout(new BorderLayout());
        setBackground(UITheme.BACKGROUND);
        card.setBackground(UITheme.CARD);
        card.setBorder(UITheme.cardBorder());
        JPanel wrap = new JPanel();
        wrap.setOpaque(false);
        wrap.add(card);
        add(wrap, BorderLayout.NORTH);
        refreshData();
    }

    @Override
    public void refreshData() {
        card.removeAll();
        JLabel title = new JLabel("My Attendance");
        title.setFont(UITheme.HEADING);
        title.setForeground(UITheme.PRIMARY_DARK);
        card.add(title);

        Student student = attendanceService.findByRollNo(user.getUsername());
        if (student == null) {
            card.add(new JLabel("No attendance record found for roll number " + user.getUsername() + "."));
            card.add(new JLabel("Please ask a teacher to add your student details."));
        } else {
            card.add(line("Roll Number", student.getRollNo()));
            card.add(line("Name", student.getName()));
            card.add(line("Division", student.getDivision()));
            card.add(line("Classes Attended", student.getClassesAttended() + " / " + student.getTotalClasses()));
            card.add(line("Percentage", AttendanceCalculator.formatPercentage(student.getPercentage())));
            card.add(line("Status", student.getStatus()));
            card.add(line("Consecutive classes needed to reach 75%", String.valueOf(student.getClassesNeededFor75())));
            if (student.isCritical()) {
                JLabel alert = new JLabel("CRITICAL: Your attendance is below 65%.");
                alert.setFont(UITheme.BOLD);
                alert.setForeground(UITheme.CRITICAL);
                card.add(alert);
            } else if (student.isDefaulter()) {
                JLabel alert = new JLabel("You are on the defaulter list (below 75%).");
                alert.setFont(UITheme.BOLD);
                alert.setForeground(UITheme.DEFAULTER);
                card.add(alert);
            }
        }
        card.revalidate();
        card.repaint();
    }

    private JLabel line(String key, String value) {
        JLabel label = new JLabel(key + ":  " + value);
        label.setFont(UITheme.NORMAL);
        return label;
    }
}
