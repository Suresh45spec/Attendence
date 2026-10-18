package attendance.gui.panels;

import java.awt.BorderLayout;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import attendance.model.Student;
import attendance.service.AttendanceService;
import attendance.util.AttendanceCalculator;
import attendance.util.UITheme;

public class DefaulterListPanel extends JPanel implements RefreshablePanel {

    private final AttendanceService attendanceService;
    private final DefaultTableModel tableModel;
    private final JLabel summary = new JLabel();

    public DefaulterListPanel(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
        setLayout(new BorderLayout(10, 10));
        setBackground(UITheme.BACKGROUND);

        JLabel title = new JLabel("Defaulter List (Attendance below 75%)");
        title.setFont(UITheme.HEADING);
        title.setForeground(UITheme.DEFAULTER);

        summary.setFont(UITheme.BOLD);
        summary.setForeground(UITheme.CRITICAL);
        summary.setBorder(BorderFactory.createEmptyBorder(6, 0, 6, 0));

        tableModel = new DefaultTableModel(new Object[] {
                "Roll No", "Name", "Division", "Percentage", "Status", "Classes needed for 75%"
        }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable table = new JTable(tableModel);
        table.setRowHeight(26);
        table.setFont(UITheme.NORMAL);
        table.getTableHeader().setFont(UITheme.BOLD);

        JPanel north = new JPanel(new BorderLayout());
        north.setOpaque(false);
        north.add(title, BorderLayout.NORTH);
        north.add(summary, BorderLayout.SOUTH);

        add(north, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        refreshData();
    }

    @Override
    public void refreshData() {
        tableModel.setRowCount(0);
        ArrayList<Student> defaulters = attendanceService.getDefaulters();
        ArrayList<Student> critical = attendanceService.getCriticalStudents();

        for (Student student : defaulters) {
            tableModel.addRow(new Object[] {
                    student.getRollNo(),
                    student.getName(),
                    student.getDivision(),
                    AttendanceCalculator.formatPercentage(student.getPercentage()),
                    student.getStatus(),
                    student.getClassesNeededFor75()
            });
        }

        if (critical.isEmpty()) {
            summary.setText("Critical alert: none. Total defaulters: " + defaulters.size());
            summary.setForeground(UITheme.DEFAULTER);
        } else {
            summary.setText("CRITICAL ALERT: " + critical.size()
                    + " student(s) below 65%. Total defaulters (< 75%): " + defaulters.size());
            summary.setForeground(UITheme.CRITICAL);
        }
    }
}
