package attendance.gui.panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import attendance.gui.GuiFactory;
import attendance.model.Student;
import attendance.service.AttendanceService;
import attendance.util.AttendanceCalculator;
import attendance.util.UITheme;

public class ViewStudentsPanel extends JPanel implements RefreshablePanel {

    private final AttendanceService attendanceService;
    private final JTextField searchField = GuiFactory.textField();
    private final DefaultTableModel tableModel;
    private final JTable table;

    public ViewStudentsPanel(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
        setLayout(new BorderLayout(10, 10));
        setBackground(UITheme.BACKGROUND);

        JLabel title = new JLabel("View All Students");
        title.setFont(UITheme.HEADING);
        title.setForeground(UITheme.PRIMARY_DARK);

        JPanel searchRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchRow.setOpaque(false);
        searchField.setPreferredSize(new Dimension(160, 30));
        JButton search = GuiFactory.primaryButton("Search by Roll No", UITheme.PRIMARY);
        search.setPreferredSize(new Dimension(180, 32));
        search.addActionListener(e -> searchStudent());
        JButton showAll = GuiFactory.primaryButton("Show All", UITheme.STUDENT_TEAL);
        showAll.setPreferredSize(new Dimension(120, 32));
        showAll.addActionListener(e -> refreshData());
        searchRow.add(new JLabel("Roll Number:"));
        searchRow.add(searchField);
        searchRow.add(search);
        searchRow.add(showAll);

        JPanel north = new JPanel(new BorderLayout());
        north.setOpaque(false);
        north.add(title, BorderLayout.NORTH);
        north.add(searchRow, BorderLayout.SOUTH);

        tableModel = new DefaultTableModel(new Object[] {
                "Roll No", "Name", "Division", "Total", "Attended", "Percentage", "Status", "Classes to 75%"
        }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        table.setRowHeight(26);
        table.setFont(UITheme.NORMAL);
        table.getTableHeader().setFont(UITheme.BOLD);
        table.getColumnModel().getColumn(6).setCellRenderer(new StatusRenderer());

        add(north, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        refreshData();
    }

    private void searchStudent() {
        String roll = searchField.getText().trim();
        if (roll.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter a roll number.", "Search", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Student student = attendanceService.findByRollNo(roll);
        tableModel.setRowCount(0);
        if (student == null) {
            JOptionPane.showMessageDialog(this, "No student found with roll number " + roll + ".", "Search", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        addRow(student);
    }

    private void addRow(Student student) {
        tableModel.addRow(new Object[] {
                student.getRollNo(),
                student.getName(),
                student.getDivision(),
                student.getTotalClasses(),
                student.getClassesAttended(),
                AttendanceCalculator.formatPercentage(student.getPercentage()),
                student.getStatus(),
                student.getClassesNeededFor75()
        });
    }

    @Override
    public void refreshData() {
        tableModel.setRowCount(0);
        for (Student student : attendanceService.getAllStudents()) {
            addRow(student);
        }
    }

    static class StatusRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component cell = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            String status = String.valueOf(value);
            if (!isSelected) {
                if (AttendanceCalculator.STATUS_ELIGIBLE.equals(status)) {
                    cell.setForeground(UITheme.ELIGIBLE);
                } else if (AttendanceCalculator.STATUS_DEFAULTER.equals(status)) {
                    cell.setForeground(UITheme.DEFAULTER);
                } else {
                    cell.setForeground(UITheme.CRITICAL);
                }
            } else {
                cell.setForeground(Color.WHITE);
            }
            return cell;
        }
    }
}
