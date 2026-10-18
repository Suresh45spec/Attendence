package attendance.model;

/**
 * Represents a registered Teacher or Student login account.
 */
public class User {

    public static final String ROLE_TEACHER = "Teacher";
    public static final String ROLE_STUDENT = "Student";

    private String username; // for students this is the Roll Number
    private String password;
    private String role;
    private String fullName;
    private String email;

    public User(String username, String password, String role, String fullName, String email) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.fullName = fullName;
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public boolean isTeacher() {
        return ROLE_TEACHER.equalsIgnoreCase(role);
    }

    public boolean isStudent() {
        return ROLE_STUDENT.equalsIgnoreCase(role);
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public String toFileLine() {
        return username + "|" + password + "|" + role + "|" + fullName + "|" + email;
    }

    public static User fromFileLine(String line) {
        String[] parts = line.split("\\|", -1);
        if (parts.length != 5) {
            throw new IllegalArgumentException("Invalid user record: " + line);
        }
        return new User(parts[0], parts[1], parts[2], parts[3], parts[4]);
    }
}
