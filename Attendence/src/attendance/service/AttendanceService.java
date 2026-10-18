package attendance.service;

import java.io.IOException;
import java.util.ArrayList;

import attendance.model.Student;
import attendance.util.AttendanceCalculator;

/**
 * Business logic for adding, searching and updating students.
 * Student records are kept in an ArrayList in memory and also saved to a file.
 */
public class AttendanceService {

    private final FileStorage storage;
    private ArrayList<Student> students;

    public AttendanceService(FileStorage storage) {
        this.storage = storage;
        this.students = new ArrayList<Student>();
        loadStudents();
        createDemoStudentsIfEmpty();
    }

    private void loadStudents() {
        try {
            students = storage.loadStudents();
        } catch (IOException e) {
            students = new ArrayList<Student>();
        }
    }

    private void createDemoStudentsIfEmpty() {
        if (!students.isEmpty()) {
            return;
        }
        students.add(new Student("101", "Aarav Sharma", "A", 40, 36));
        students.add(new Student("102", "Diya Patel", "A", 40, 28));
        students.add(new Student("103", "Rohan Mehta", "B", 40, 24));
        students.add(new Student("104", "Isha Kulkarni", "B", 40, 32));
        students.add(new Student("105", "Kabir Khan", "A", 40, 20));
        saveQuietly();
    }

    private void saveQuietly() {
        try {
            storage.saveStudents(students);
        } catch (IOException e) {
            System.out.println("Could not save students: " + e.getMessage());
        }
    }

    public ArrayList<Student> getAllStudents() {
        return students;
    }

    public Student findByRollNo(String rollNo) {
        for (Student student : students) {
            if (student.getRollNo().equalsIgnoreCase(rollNo.trim())) {
                return student;
            }
        }
        return null;
    }

    public String addStudent(String rollNo, String name, String division, String totalText, String attendedText) {
        if (isBlank(rollNo) || isBlank(name) || isBlank(division) || isBlank(totalText) || isBlank(attendedText)) {
            return "Please fill all student details.";
        }
        if (findByRollNo(rollNo) != null) {
            return "A student with this roll number already exists.";
        }

        int total;
        int attended;
        try {
            total = Integer.parseInt(totalText.trim());
            attended = Integer.parseInt(attendedText.trim());
        } catch (NumberFormatException e) {
            return "Total Classes and Classes Attended must be whole numbers.";
        }

        if (total < 0 || attended < 0) {
            return "Class counts cannot be negative.";
        }
        if (attended > total) {
            return "Classes Attended cannot be more than Total Classes.";
        }

        students.add(new Student(rollNo.trim(), name.trim(), division.trim().toUpperCase(), total, attended));
        try {
            storage.saveStudents(students);
        } catch (IOException e) {
            return "Student added in memory, but file save failed: " + e.getMessage();
        }
        return null;
    }

    public String updateAttendance(String rollNo, String totalText, String attendedText) {
        Student student = findByRollNo(rollNo);
        if (student == null) {
            return "No student found with roll number " + rollNo + ".";
        }

        int total;
        int attended;
        try {
            total = Integer.parseInt(totalText.trim());
            attended = Integer.parseInt(attendedText.trim());
        } catch (NumberFormatException e) {
            return "Total Classes and Classes Attended must be whole numbers.";
        }

        if (total < 0 || attended < 0) {
            return "Class counts cannot be negative.";
        }
        if (attended > total) {
            return "Classes Attended cannot be more than Total Classes.";
        }

        student.setTotalClasses(total);
        student.setClassesAttended(attended);
        try {
            storage.saveStudents(students);
        } catch (IOException e) {
            return "Attendance updated in memory, but file save failed: " + e.getMessage();
        }
        return null;
    }

    /**
     * Marks one extra class for every student.
     * Students in presentRolls get +1 attended as well.
     */
    public String markTodayAttendance(ArrayList<String> presentRolls) {
        if (students.isEmpty()) {
            return "No students available to mark attendance.";
        }

        for (Student student : students) {
            student.setTotalClasses(student.getTotalClasses() + 1);
            if (containsIgnoreCase(presentRolls, student.getRollNo())) {
                student.setClassesAttended(student.getClassesAttended() + 1);
            }
        }

        try {
            storage.saveStudents(students);
            storage.saveTodayPresentCount(presentRolls.size());
        } catch (IOException e) {
            return "Attendance marked in memory, but file save failed: " + e.getMessage();
        }
        return null;
    }

    public ArrayList<Student> getDefaulters() {
        ArrayList<Student> defaulters = new ArrayList<Student>();
        for (Student student : students) {
            if (student.isDefaulter()) {
                defaulters.add(student);
            }
        }
        return defaulters;
    }

    public ArrayList<Student> getCriticalStudents() {
        ArrayList<Student> critical = new ArrayList<Student>();
        for (Student student : students) {
            if (student.isCritical()) {
                critical.add(student);
            }
        }
        return critical;
    }

    public int getPresentToday() {
        return storage.loadTodayPresentCount();
    }

    public int countEligible() {
        int count = 0;
        for (Student student : students) {
            if (AttendanceCalculator.STATUS_ELIGIBLE.equals(student.getStatus())) {
                count++;
            }
        }
        return count;
    }

    private boolean containsIgnoreCase(ArrayList<String> list, String value) {
        for (String item : list) {
            if (item.equalsIgnoreCase(value)) {
                return true;
            }
        }
        return false;
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
