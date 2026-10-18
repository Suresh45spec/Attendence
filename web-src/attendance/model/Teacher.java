package attendance.model;

/**
 * Logged-in teacher account stored in MySQL.
 */
public class Teacher {

    private int teacherId;
    private String username;
    private String fullName;
    private String email;

    public Teacher(int teacherId, String username, String fullName, String email) {
        this.teacherId = teacherId;
        this.username = username;
        this.fullName = fullName;
        this.email = email == null ? "" : email;
    }

    public int getTeacherId() {
        return teacherId;
    }

    public String getUsername() {
        return username;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
