package attendance.gui.panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.DefaultTableModel;

import attendance.gui.GuiFactory;
import attendance.model.Student;
import attendance.service.AttendanceService;
import attendance.util.AttendanceCalculator;
import attendance.util.UITheme;

public class ReportsPanel extends JPanel implements RefreshablePanel {

    private final AttendanceService attendanceService;
    private final DefaultTableModel tableModel;
    private final JTextArea summaryArea = new JTextArea(5, 40);

    public ReportsPanel(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
        setLayout(new BorderLayout(10, 10));
        setBackground(UITheme.BACKGROUND);

        JLabel title = new JLabel("Attendance Reports");
        title.setFont(UITheme.HEADING);
        title.setForeground(UITheme.PRIMARY_DARK);

        tableModel = new DefaultTableModel(new Object[] {
                "Roll No", "Name", "Division", "Attended", "Total", "Percentage", "Status", "Need for 75%"
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

        summaryArea.setEditable(false);
        summaryArea.setFont(UITheme.NORMAL);
        summaryArea.setBackground(Color.WHITE);
        summaryArea.setBorder(UITheme.cardBorder());

        JButton refresh = GuiFactory.primaryButton("Refresh Report", UITheme.PRIMARY);
        refresh.addActionListener(e -> refreshData());
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.setOpaque(false);
        top.add(title);
        top.add(refresh);

        add(top, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(summaryArea, BorderLayout.SOUTH);
        refreshData();
    }

    @Override
    public void refreshData() {
        tableModel.setRowCount(0);
        for (Student student : attendanceService.getAllStudents()) {
            tableModel.addRow(new Object[] {
                    student.getRollNo(),
                    student.getName(),
                    student.getDivision(),
                    student.getClassesAttended(),
                    student.getTotalClasses(),
                    AttendanceCalculator.formatPercentage(student.getPercentage()),
                    student.getStatus(),
                    student.getClassesNeededFor75()
            });
        }

        int total = attendanceService.getAllStudents().size();
        int eligible = attendanceService.countEligible();
        int defaulters = attendanceService.getDefaulters().size();
        int critical = attendanceService.getCriticalStudents().size();
        summaryArea.setText(
                "Report Summary\n"
                        + "Total students: " + total + "\n"
                        + "Eligible (>= 75%): " + eligible + "\n"
                        + "Defaulters (65% to 74.99% plus critical): " + defaulters + "\n"
                        + "Critical (< 65%): " + critical + "\n"
                        + "Present today: " + attendanceService.getPresentToday()
        );
    }
}
