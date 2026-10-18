package attendance.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import attendance.model.Student;
import attendance.util.AttendanceCalculator;
import attendance.util.PasswordHasher;

public class StudentDAO {

    private static final String BASE_COLUMNS =
            "roll_no, name, division, email, total_classes, classes_attended";

    public List<Student> findAll() throws SQLException {
        return findAll(null, "roll_no", "asc");
    }

    /** Optional status filter (Eligible / Defaulter / Critical) and sort applied in Java. */
    public List<Student> findAll(String statusFilter, String sortKey, String sortDir) throws SQLException {
        String sql = "SELECT " + BASE_COLUMNS + " FROM students ORDER BY roll_no";
        List<Student> students = new ArrayList<Student>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Student student = map(rs);
                if (statusFilter != null && !statusFilter.isEmpty() && !statusFilter.equals("All")
                        && !statusFilter.equals(student.getStatus())) {
                    continue;
                }
                students.add(student);
            }
        }
        sort(students, sortKey, sortDir);
        return students;
    }

    public Student findByRollNo(String rollNo) throws SQLException {
        String sql = "SELECT " + BASE_COLUMNS + " FROM students WHERE roll_no = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, rollNo.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return map(rs);
                }
            }
        }
        return null;
    }

    public List<Student> search(String query) throws SQLException {
        String sql = "SELECT " + BASE_COLUMNS + " FROM students "
                + "WHERE roll_no LIKE ? OR name LIKE ? ORDER BY roll_no";
        String like = "%" + query.trim() + "%";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, like);
            ps.setString(2, like);
            try (ResultSet rs = ps.executeQuery()) {
                List<Student> students = new ArrayList<Student>();
                while (rs.next()) {
                    students.add(map(rs));
                }
                return students;
            }
        }
    }

    /** Students of one division, used by Mark Attendance. */
    public List<Student> findByDivision(String division) throws SQLException {
        String sql = "SELECT " + BASE_COLUMNS + " FROM students WHERE division = ? ORDER BY roll_no";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, division);
            try (ResultSet rs = ps.executeQuery()) {
                List<Student> students = new ArrayList<Student>();
                while (rs.next()) {
                    students.add(map(rs));
                }
                return students;
            }
        }
    }

    public List<String> findDivisions() throws SQLException {
        String sql = "SELECT DISTINCT division FROM students ORDER BY division";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            List<String> divisions = new ArrayList<String>();
            while (rs.next()) {
                divisions.add(rs.getString(1));
            }
            return divisions;
        }
    }

    /** Checks roll number AND password for student login. */
    public Student login(String rollNo, String password) throws SQLException {
        String sql = "SELECT " + BASE_COLUMNS + ", password FROM students WHERE roll_no = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, rollNo.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next() && PasswordHasher.matches(password, rs.getString("password"))) {
                    return map(rs);
                }
            }
        }
        return null;
    }

    public boolean rollExists(String rollNo) throws SQLException {
        return findByRollNo(rollNo) != null;
    }

    public boolean emailExists(String email) throws SQLException {
        String sql = "SELECT 1 FROM students WHERE email = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email.trim());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public Student findByRollAndEmail(String rollNo, String email) throws SQLException {
        String sql = "SELECT " + BASE_COLUMNS + " FROM students WHERE roll_no = ? AND email = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, rollNo.trim());
            ps.setString(2, email.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return map(rs);
                }
            }
        }
        return null;
    }

    public void register(String rollNo, String name, String email, String password) throws SQLException {
        String sql = "INSERT INTO students (roll_no, name, division, email, password, total_classes, classes_attended) "
                + "VALUES (?, ?, ?, ?, ?, 0, 0)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, rollNo.trim());
            ps.setString(2, name.trim());
            ps.setString(3, "NA");
            ps.setString(4, email.trim());
            ps.setString(5, PasswordHasher.hash(password));
            ps.executeUpdate();
        }
    }

    public void insert(Student student) throws SQLException {
        String sql = "INSERT INTO students (roll_no, name, division, email, total_classes, classes_attended) "
                + "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, student.getRollNo());
            ps.setString(2, student.getName());
            ps.setString(3, student.getDivision());
            ps.setString(4, student.getEmail());
            ps.setInt(5, student.getTotalClasses());
            ps.setInt(6, student.getClassesAttended());
            ps.executeUpdate();
        }
    }

    public boolean update(Student student) throws SQLException {
        String sql = "UPDATE students SET name = ?, division = ?, email = ?, total_classes = ?, classes_attended = ? "
                + "WHERE roll_no = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, student.getName());
            ps.setString(2, student.getDivision());
            ps.setString(3, student.getEmail());
            ps.setInt(4, student.getTotalClasses());
            ps.setInt(5, student.getClassesAttended());
            ps.setString(6, student.getRollNo());
            return ps.executeUpdate() == 1;
        }
    }

    public boolean updateProfile(String rollNo, String name, String email, String division) throws SQLException {
        String sql = "UPDATE students SET name = ?, email = ?, division = ? WHERE roll_no = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name.trim());
            ps.setString(2, email.trim());
            ps.setString(3, division.trim().toUpperCase());
            ps.setString(4, rollNo.trim());
            return ps.executeUpdate() == 1;
        }
    }

    public boolean updatePassword(String rollNo, String newPassword) throws SQLException {
        String sql = "UPDATE students SET password = ? WHERE roll_no = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, PasswordHasher.hash(newPassword));
            ps.setString(2, rollNo.trim());
            return ps.executeUpdate() == 1;
        }
    }

    public List<Student> findBelow75() throws SQLException {
        List<Student> result = new ArrayList<Student>();
        for (Student student : findAll()) {
            if (student.getPercentage() < AttendanceCalculator.ELIGIBLE_PERCENT) {
                result.add(student);
            }
        }
        return result;
    }

    public List<Student> findCritical() throws SQLException {
        List<Student> result = new ArrayList<Student>();
        for (Student student : findAll()) {
            if (student.isCritical()) {
                result.add(student);
            }
        }
        return result;
    }

    public int countByStatus(String status) throws SQLException {
        int count = 0;
        for (Student student : findAll()) {
            if (status.equals(student.getStatus())) {
                count++;
            }
        }
        return count;
    }

    private void sort(List<Student> students, String sortKey, String sortDir) {
        if (sortKey == null || sortKey.isEmpty()) {
            return;
        }
        boolean desc = "desc".equalsIgnoreCase(sortDir);
        students.sort((a, b) -> {
            int cmp;
            if ("name".equals(sortKey)) {
                cmp = a.getName().compareToIgnoreCase(b.getName());
            } else if ("percent".equals(sortKey)) {
                cmp = Double.compare(a.getPercentage(), b.getPercentage());
            } else if ("total".equals(sortKey)) {
                cmp = Integer.compare(a.getTotalClasses(), b.getTotalClasses());
            } else if ("attended".equals(sortKey)) {
                cmp = Integer.compare(a.getClassesAttended(), b.getClassesAttended());
            } else {
                cmp = a.getRollNo().compareToIgnoreCase(b.getRollNo());
            }
            return desc ? -cmp : cmp;
        });
    }

    private Student map(ResultSet rs) throws SQLException {
        return new Student(
                rs.getString("roll_no"),
                rs.getString("name"),
                rs.getString("division"),
                rs.getInt("total_classes"),
                rs.getInt("classes_attended"),
                rs.getString("email"));
    }
}
