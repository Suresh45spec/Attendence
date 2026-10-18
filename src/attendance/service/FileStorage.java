package attendance.service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;

import attendance.model.Student;
import attendance.model.User;

/**
 * Saves and loads data using plain text files (no database).
 * Files are stored in the "data" folder of the project.
 */
public class FileStorage {

    private static final String DATA_FOLDER = "data";
    private static final String STUDENTS_FILE = DATA_FOLDER + File.separator + "students.txt";
    private static final String USERS_FILE = DATA_FOLDER + File.separator + "users.txt";
    private static final String TODAY_FILE = DATA_FOLDER + File.separator + "today.txt";
    private static final String REMEMBER_TEACHER = DATA_FOLDER + File.separator + "remember_teacher.txt";
    private static final String REMEMBER_STUDENT = DATA_FOLDER + File.separator + "remember_student.txt";

    public FileStorage() {
        File folder = new File(DATA_FOLDER);
        if (!folder.exists()) {
            folder.mkdirs();
        }
    }

    public ArrayList<Student> loadStudents() throws IOException {
        ArrayList<Student> students = new ArrayList<Student>();
        File file = new File(STUDENTS_FILE);
        if (!file.exists()) {
            return students;
        }

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }
                students.add(Student.fromFileLine(line));
            }
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
        return students;
    }

    public void saveStudents(ArrayList<Student> students) throws IOException {
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(STUDENTS_FILE));
            writer.write("# rollNo|name|division|totalClasses|classesAttended");
            writer.newLine();
            for (Student student : students) {
                writer.write(student.toFileLine());
                writer.newLine();
            }
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }

    public ArrayList<User> loadUsers() throws IOException {
        ArrayList<User> users = new ArrayList<User>();
        File file = new File(USERS_FILE);
        if (!file.exists()) {
            return users;
        }

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }
                users.add(User.fromFileLine(line));
            }
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
        return users;
    }

    public void saveUsers(ArrayList<User> users) throws IOException {
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(USERS_FILE));
            writer.write("# username|password|role|fullName|email");
            writer.newLine();
            for (User user : users) {
                writer.write(user.toFileLine());
                writer.newLine();
            }
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }

    public void saveTodayPresentCount(int presentCount) throws IOException {
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(TODAY_FILE));
            writer.write(LocalDate.now().toString() + "|" + presentCount);
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }

    public int loadTodayPresentCount() {
        File file = new File(TODAY_FILE);
        if (!file.exists()) {
            return 0;
        }
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String line = reader.readLine();
            if (line == null || line.trim().isEmpty()) {
                return 0;
            }
            String[] parts = line.split("\\|");
            if (parts.length != 2) {
                return 0;
            }
            if (!LocalDate.now().toString().equals(parts[0])) {
                return 0;
            }
            return Integer.parseInt(parts[1]);
        } catch (Exception e) {
            return 0;
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException ignored) {
            }
        }
    }

    public void rememberUsername(String role, String username) throws IOException {
        String path = User.ROLE_TEACHER.equalsIgnoreCase(role) ? REMEMBER_TEACHER : REMEMBER_STUDENT;
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(path));
            writer.write(username);
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }

    public void clearRememberedUsername(String role) {
        String path = User.ROLE_TEACHER.equalsIgnoreCase(role) ? REMEMBER_TEACHER : REMEMBER_STUDENT;
        File file = new File(path);
        if (file.exists()) {
            file.delete();
        }
    }

    public String loadRememberedUsername(String role) {
        String path = User.ROLE_TEACHER.equalsIgnoreCase(role) ? REMEMBER_TEACHER : REMEMBER_STUDENT;
        File file = new File(path);
        if (!file.exists()) {
            return "";
        }
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String line = reader.readLine();
            return line == null ? "" : line.trim();
        } catch (IOException e) {
            return "";
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException ignored) {
            }
        }
    }
}
