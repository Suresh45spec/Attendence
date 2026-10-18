package attendance.gui.panels;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import attendance.gui.GuiFactory;
import attendance.model.Student;
import attendance.service.AttendanceService;
import attendance.util.AttendanceCalculator;
import attendance.util.UITheme;

public class MarkAttendancePanel extends JPanel implements RefreshablePanel {

    private final AttendanceService attendanceService;
    private final Runnable onChanged;

    private final JTextField rollField = GuiFactory.textField();
    private final JTextField totalField = GuiFactory.textField();
    private final JTextField attendedField = GuiFactory.textField();
    private final DefaultTableModel tableModel;
    private final JTable table;

    public MarkAttendancePanel(AttendanceService attendanceService, Runnable onChanged) {
        this.attendanceService = attendanceService;
        this.onChanged = onChanged;
        setLayout(new BorderLayout(12, 12));
        setBackground(UITheme.BACKGROUND);

        JLabel title = new JLabel("Update Attendance / Mark Today");
        title.setFont(UITheme.HEADING);
        title.setForeground(UITheme.PRIMARY_DARK);

        JPanel updateCard = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        updateCard.setBackground(UITheme.CARD);
        updateCard.setBorder(UITheme.cardBorder());
        updateCard.add(new JLabel("Roll No:"));
        rollField.setPreferredSize(new Dimension(90, 30));
        updateCard.add(rollField);
        updateCard.add(new JLabel("Total Classes:"));
        totalField.setPreferredSize(new Dimension(70, 30));
        updateCard.add(totalField);
        updateCard.add(new JLabel("Attended:"));
        attendedField.setPreferredSize(new Dimension(70, 30));
        updateCard.add(attendedField);

        JButton search = GuiFactory.primaryButton("Search", UITheme.PRIMARY);
        search.setPreferredSize(new Dimension(110, 32));
        search.addActionListener(e -> searchStudent());
        JButton update = GuiFactory.primaryButton("Update", UITheme.TEACHER_GREEN);
        update.setPreferredSize(new Dimension(110, 32));
        update.addActionListener(e -> updateStudent());
        updateCard.add(search);
        updateCard.add(update);

        tableModel = new DefaultTableModel(new Object[] {"Present", "Roll No", "Name", "Division", "Attended / Total", "%"}, 0) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return columnIndex == 0 ? Boolean.class : String.class;
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 0;
            }
        };
        table = new JTable(tableModel);
        table.setRowHeight(26);
        table.setFont(UITheme.NORMAL);
        table.getTableHeader().setFont(UITheme.BOLD);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bottom.setOpaque(false);
        JCheckBox selectAll = new JCheckBox("Select all present");
        selectAll.setOpaque(false);
        selectAll.addActionListener(e -> {
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                tableModel.setValueAt(selectAll.isSelected(), i, 0);
            }
        });
        JButton markToday = GuiFactory.primaryButton("Mark Today's Attendance", UITheme.PRIMARY);
        markToday.setPreferredSize(new Dimension(240, 36));
        markToday.addActionListener(e -> markToday());
        bottom.add(selectAll);
        bottom.add(markToday);

        JPanel north = new JPanel(new BorderLayout(8, 8));
        north.setOpaque(false);
        north.add(title, BorderLayout.NORTH);
        north.add(updateCard, BorderLayout.CENTER);

        add(north, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);
        refreshData();
    }

    private void searchStudent() {
        String roll = rollField.getText().trim();
        if (roll.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter a roll number to search.", "Search", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Student student = attendanceService.findByRollNo(roll);
        if (student == null) {
            JOptionPane.showMessageDialog(this, "No student found with roll number " + roll + ".", "Search", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        totalField.setText(String.valueOf(student.getTotalClasses()));
        attendedField.setText(String.valueOf(student.getClassesAttended()));
        JOptionPane.showMessageDialog(this,
                student.getName() + " (" + student.getRollNo() + ")\n"
                        + "Percentage: " + AttendanceCalculator.formatPercentage(student.getPercentage()) + "\n"
                        + "Status: " + student.getStatus() + "\n"
                        + "Classes needed to reach 75%: " + student.getClassesNeededFor75(),
                "Student Found", JOptionPane.INFORMATION_MESSAGE);
    }

    private void updateStudent() {
        String error = attendanceService.updateAttendance(
                rollField.getText(),
                totalField.getText(),
                attendedField.getText()
        );
        if (error != null) {
            JOptionPane.showMessageDialog(this, error, "Update Attendance", JOptionPane.WARNING_MESSAGE);
            return;
        }
        JOptionPane.showMessageDialog(this, "Attendance updated and saved.", "Update Attendance", JOptionPane.INFORMATION_MESSAGE);
        onChanged.run();
    }

    private void markToday() {
        if (tableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "No students to mark.", "Mark Attendance", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this,
                "This will add 1 class to every student.\nPresent students will also get +1 attended.\nContinue?",
                "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        ArrayList<String> present = new ArrayList<String>();
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            Boolean checked = (Boolean) tableModel.getValueAt(i, 0);
            if (Boolean.TRUE.equals(checked)) {
                present.add(String.valueOf(tableModel.getValueAt(i, 1)));
            }
        }
        String error = attendanceService.markTodayAttendance(present);
        if (error != null) {
            JOptionPane.showMessageDialog(this, error, "Mark Attendance", JOptionPane.WARNING_MESSAGE);
            return;
        }
        JOptionPane.showMessageDialog(this, "Today's attendance saved. Present: " + present.size(), "Mark Attendance", JOptionPane.INFORMATION_MESSAGE);
        onChanged.run();
    }

    @Override
    public void refreshData() {
        tableModel.setRowCount(0);
        for (Student student : attendanceService.getAllStudents()) {
            tableModel.addRow(new Object[] {
                    Boolean.FALSE,
                    student.getRollNo(),
                    student.getName(),
                    student.getDivision(),
                    student.getClassesAttended() + " / " + student.getTotalClasses(),
                    AttendanceCalculator.formatPercentage(student.getPercentage())
            });
        }
    }
}
