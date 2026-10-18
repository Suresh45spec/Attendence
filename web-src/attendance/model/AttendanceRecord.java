package attendance.model;

/**
 * One attendance row: which student, which subject, which date, P or A.
 */
public class AttendanceRecord {

    public static final String PRESENT = "PRESENT";
    public static final String ABSENT = "ABSENT";

    private int recordId;
    private String rollNo;
    private String studentName;
    private String subject;
    private String classDate;
    private String status;

    public AttendanceRecord() {
    }

    public AttendanceRecord(String rollNo, String subject, String classDate, String status) {
        this.rollNo = rollNo;
        this.subject = subject;
        this.classDate = classDate;
        this.status = status;
    }

    public int getRecordId() {
        return recordId;
    }

    public void setRecordId(int recordId) {
        this.recordId = recordId;
    }

    public String getRollNo() {
        return rollNo;
    }

    public void setRollNo(String rollNo) {
        this.rollNo = rollNo;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getClassDate() {
        return classDate;
    }

    public void setClassDate(String classDate) {
        this.classDate = classDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isPresent() {
        return PRESENT.equals(status);
    }
}
