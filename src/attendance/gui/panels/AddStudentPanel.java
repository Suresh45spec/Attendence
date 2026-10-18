package attendance.gui.panels;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import attendance.gui.GuiFactory;
import attendance.service.AttendanceService;
import attendance.util.UITheme;

public class AddStudentPanel extends JPanel implements RefreshablePanel {

    private final AttendanceService attendanceService;
    private final Runnable onChanged;

    private final JTextField rollField = GuiFactory.textField();
    private final JTextField nameField = GuiFactory.textField();
    private final JTextField divisionField = GuiFactory.textField();
    private final JTextField totalField = GuiFactory.textField();
    private final JTextField attendedField = GuiFactory.textField();

    public AddStudentPanel(AttendanceService attendanceService, Runnable onChanged) {
        this.attendanceService = attendanceService;
        this.onChanged = onChanged;

        setLayout(new GridBagLayout());
        setBackground(UITheme.BACKGROUND);

        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(UITheme.CARD);
        card.setBorder(UITheme.cardBorder());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 8, 6, 8);

        gbc.gridy = 0;
        card.add(GuiFactory.title("Add Student Details", UITheme.PRIMARY_DARK), gbc);
        gbc.gridy = 1;
        card.add(labeled("Roll Number", rollField), gbc);
        gbc.gridy = 2;
        card.add(labeled("Name", nameField), gbc);
        gbc.gridy = 3;
        card.add(labeled("Division", divisionField), gbc);
        gbc.gridy = 4;
        card.add(labeled("Total Classes", totalField), gbc);
        gbc.gridy = 5;
        card.add(labeled("Classes Attended", attendedField), gbc);

        JButton save = GuiFactory.primaryButton("ADD STUDENT", UITheme.PRIMARY);
        save.addActionListener(e -> addStudent());
        gbc.gridy = 6;
        card.add(save, gbc);

        add(card);
    }

    private JPanel labeled(String text, JTextField field) {
        JPanel panel = new JPanel(new BorderLayout(0, 4));
        panel.setOpaque(false);
        JLabel label = new JLabel(text);
        label.setFont(UITheme.BOLD);
        panel.add(label, BorderLayout.NORTH);
        panel.add(field, BorderLayout.CENTER);
        return panel;
    }

    private void addStudent() {
        String error = attendanceService.addStudent(
                rollField.getText(),
                nameField.getText(),
                divisionField.getText(),
                totalField.getText(),
                attendedField.getText()
        );
        if (error != null) {
            JOptionPane.showMessageDialog(this, error, "Add Student", JOptionPane.WARNING_MESSAGE);
            return;
        }
        JOptionPane.showMessageDialog(this, "Student added and saved to file.", "Add Student", JOptionPane.INFORMATION_MESSAGE);
        rollField.setText("");
        nameField.setText("");
        divisionField.setText("");
        totalField.setText("");
        attendedField.setText("");
        onChanged.run();
    }

    @Override
    public void refreshData() {
        // Form panel does not need to reload a table
    }
}
