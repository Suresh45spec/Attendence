package attendance.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import attendance.model.AttendanceRecord;

/**
 * Stores one row per student + subject + date and keeps the
 * students.total_classes / classes_attended counters in sync.
 */
public class AttendanceDAO {

    /**
     * Saves a whole roster for one class session.
     * - New (roll, subject, date): inserts the record and increments counters.
     * - Existing with a different status: corrects the record and adjusts attended.
     * - Existing with the same status: skipped (duplicate prevention).
     * Runs inside one transaction.
     */
    public int saveBatch(List<AttendanceRecord> records, String markedBy) throws SQLException {
        if (records.isEmpty()) {
            return 0;
        }
        String findSql = "SELECT status FROM attendance_records "
                + "WHERE roll_no = ? AND subject = ? AND class_date = ?";
        String insertSql = "INSERT INTO attendance_records (roll_no, subject, class_date, status, marked_by) "
                + "VALUES (?, ?, ?, ?, ?)";
        String updateRecordSql = "UPDATE attendance_records SET status = ? "
                + "WHERE roll_no = ? AND subject = ? AND class_date = ?";
        String insertCounterSql = "UPDATE students SET total_classes = total_classes + 1, "
                + "classes_attended = classes_attended + ? WHERE roll_no = ?";
        String adjustCounterSql = "UPDATE students SET classes_attended = classes_attended + ? WHERE roll_no = ?";

        int saved = 0;
        Connection conn = DBConnection.getConnection();
        try {
            conn.setAutoCommit(false);

            try (PreparedStatement find = conn.prepareStatement(findSql);
                 PreparedStatement insert = conn.prepareStatement(insertSql);
                 PreparedStatement updateRecord = conn.prepareStatement(updateRecordSql);
                 PreparedStatement insertCounter = conn.prepareStatement(insertCounterSql);
                 PreparedStatement adjustCounter = conn.prepareStatement(adjustCounterSql)) {

                for (AttendanceRecord record : records) {
                    find.setString(1, record.getRollNo());
                    find.setString(2, record.getSubject());
                    find.setString(3, record.getClassDate());
                    String existingStatus = null;
                    try (ResultSet rs = find.executeQuery()) {
                        if (rs.next()) {
                            existingStatus = rs.getString(1);
                        }
                    }

                    boolean present = record.isPresent();

                    if (existingStatus == null) {
                        insert.setString(1, record.getRollNo());
                        insert.setString(2, record.getSubject());
                        insert.setString(3, record.getClassDate());
                        insert.setString(4, record.getStatus());
                        insert.setString(5, markedBy);
                        insert.executeUpdate();

                        insertCounter.setInt(1, present ? 1 : 0);
                        insertCounter.setString(2, record.getRollNo());
                        insertCounter.executeUpdate();
                        saved++;
                    } else if (!existingStatus.equals(record.getStatus())) {
                        // Authorized correction of an earlier entry.
                        updateRecord.setString(1, record.getStatus());
                        updateRecord.setString(2, record.getRollNo());
                        updateRecord.setString(3, record.getSubject());
                        updateRecord.setString(4, record.getClassDate());
                        updateRecord.executeUpdate();

                        adjustCounter.setInt(1, present ? 1 : -1);
                        adjustCounter.setString(2, record.getRollNo());
                        adjustCounter.executeUpdate();
                        saved++;
                    }
                    // else: identical duplicate - ignored on purpose.
                }
            }
            conn.commit();
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(true);
            conn.close();
        }
        return saved;
    }

    /** Existing status for one slot, or null if never marked. */
    public String findStatus(String rollNo, String subject, String classDate) throws SQLException {
        String sql = "SELECT status FROM attendance_records "
                + "WHERE roll_no = ? AND subject = ? AND class_date = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, rollNo);
            ps.setString(2, subject);
            ps.setString(3, classDate);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString(1);
                }
            }
        }
        return null;
    }

    /** Full history for one student, newest first. */
    public List<AttendanceRecord> historyForRoll(String rollNo) throws SQLException {
        String sql = "SELECT r.roll_no, s.name, r.subject, r.class_date, r.status "
                + "FROM attendance_records r JOIN students s ON s.roll_no = r.roll_no "
                + "WHERE r.roll_no = ? ORDER BY r.class_date DESC, r.subject";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, rollNo);
            try (ResultSet rs = ps.executeQuery()) {
                return mapAll(rs);
            }
        }
    }

    /**
     * All records with optional filters (null or empty = no filter).
     * Used by reports and CSV export.
     */
    public List<AttendanceRecord> findAll(String subject, String fromDate, String toDate) throws SQLException {
        StringBuilder sql = new StringBuilder(
                "SELECT r.roll_no, s.name, r.subject, r.class_date, r.status "
                + "FROM attendance_records r JOIN students s ON s.roll_no = r.roll_no WHERE 1=1");
        List<Object> params = new ArrayList<Object>();
        if (subject != null && !subject.isEmpty()) {
            sql.append(" AND r.subject = ?");
            params.add(subject);
        }
        if (fromDate != null && !fromDate.isEmpty()) {
            sql.append(" AND r.class_date >= ?");
            params.add(fromDate);
        }
        if (toDate != null && !toDate.isEmpty()) {
            sql.append(" AND r.class_date <= ?");
            params.add(toDate);
        }
        sql.append(" ORDER BY r.class_date DESC, s.name");

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                return mapAll(rs);
            }
        }
    }

    /** How many distinct students were marked PRESENT on the given date. */
    public int countPresentOn(String classDate) throws SQLException {
        String sql = "SELECT COUNT(DISTINCT roll_no) FROM attendance_records "
                + "WHERE status = 'PRESENT' AND class_date = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(classDate));
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        }
    }

    /** subject -> {conducted, attended} for one student. */
    public Map<String, int[]> subjectWiseForRoll(String rollNo) throws SQLException {
        String sql = "SELECT subject, COUNT(*) AS conducted, "
                + "SUM(CASE WHEN status = 'PRESENT' THEN 1 ELSE 0 END) AS attended "
                + "FROM attendance_records WHERE roll_no = ? GROUP BY subject ORDER BY subject";
        Map<String, int[]> result = new LinkedHashMap<String, int[]>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, rollNo);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.put(rs.getString("subject"),
                            new int[] { rs.getInt("conducted"), rs.getInt("attended") });
                }
            }
        }
        return result;
    }

    private List<AttendanceRecord> mapAll(ResultSet rs) throws SQLException {
        List<AttendanceRecord> records = new ArrayList<AttendanceRecord>();
        while (rs.next()) {
            AttendanceRecord record = new AttendanceRecord();
            record.setRollNo(rs.getString("roll_no"));
            record.setStudentName(rs.getString("name"));
            record.setSubject(rs.getString("subject"));
            record.setClassDate(rs.getString("class_date"));
            record.setStatus(rs.getString("status"));
            records.add(record);
        }
        return records;
    }
}
