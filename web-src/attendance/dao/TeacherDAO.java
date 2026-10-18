package attendance.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import attendance.model.Teacher;
import attendance.util.PasswordHasher;

public class TeacherDAO {

    /** Verifies credentials against the stored SHA-256 hash. */
    public Teacher login(String username, String password) throws SQLException {
        String sql = "SELECT teacher_id, username, full_name, email, password FROM teachers WHERE username = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next() && PasswordHasher.matches(password, rs.getString("password"))) {
                    return map(rs);
                }
            }
        }
        return null;
    }

    public Teacher findByUsername(String username) throws SQLException {
        String sql = "SELECT teacher_id, username, full_name, email FROM teachers WHERE username = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return map(rs);
                }
            }
        }
        return null;
    }

    /** Used by the forgot-password flow: username must match the email on file. */
    public Teacher findByUsernameAndEmail(String username, String email) throws SQLException {
        String sql = "SELECT teacher_id, username, full_name, email FROM teachers WHERE username = ? AND email = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username.trim());
            ps.setString(2, email.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return map(rs);
                }
            }
        }
        return null;
    }

    public boolean usernameExists(String username) throws SQLException {
        return findByUsername(username) != null;
    }

    public boolean emailExists(String email) throws SQLException {
        String sql = "SELECT 1 FROM teachers WHERE email = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email.trim());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public void register(String username, String password, String fullName, String email) throws SQLException {
        String sql = "INSERT INTO teachers (username, password, full_name, email) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username.trim());
            ps.setString(2, PasswordHasher.hash(password));
            ps.setString(3, fullName.trim());
            ps.setString(4, email.trim());
            ps.executeUpdate();
        }
    }

    public boolean updateProfile(String username, String fullName, String email) throws SQLException {
        String sql = "UPDATE teachers SET full_name = ?, email = ? WHERE username = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, fullName.trim());
            ps.setString(2, email.trim());
            ps.setString(3, username.trim());
            return ps.executeUpdate() == 1;
        }
    }

    public boolean updatePassword(String username, String newPassword) throws SQLException {
        String sql = "UPDATE teachers SET password = ? WHERE username = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, PasswordHasher.hash(newPassword));
            ps.setString(2, username.trim());
            return ps.executeUpdate() == 1;
        }
    }

    private Teacher map(ResultSet rs) throws SQLException {
        return new Teacher(
                rs.getInt("teacher_id"),
                rs.getString("username"),
                rs.getString("full_name"),
                rs.getString("email"));
    }
}
