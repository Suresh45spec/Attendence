package attendance.model;

import attendance.util.AttendanceCalculator;

/**
 * Represents one student and their attendance details.
 * This is a simple POJO (Plain Old Java Object) used throughout the project.
 */
public class Student {

    private String rollNo;
    private String name;
    private String division;
    private int totalClasses;
    private int classesAttended;

    public Student(String rollNo, String name, String division, int totalClasses, int classesAttended) {
        this.rollNo = rollNo;
        this.name = name;
        this.division = division;
        this.totalClasses = totalClasses;
        this.classesAttended = classesAttended;
    }

    public String getRollNo() {
        return rollNo;
    }

    public void setRollNo(String rollNo) {
        this.rollNo = rollNo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDivision() {
        return division;
    }

    public void setDivision(String division) {
        this.division = division;
    }

    public int getTotalClasses() {
        return totalClasses;
    }

    public void setTotalClasses(int totalClasses) {
        this.totalClasses = totalClasses;
    }

    public int getClassesAttended() {
        return classesAttended;
    }

    public void setClassesAttended(int classesAttended) {
        this.classesAttended = classesAttended;
    }

    public double getPercentage() {
        return AttendanceCalculator.calculatePercentage(classesAttended, totalClasses);
    }

    public String getStatus() {
        return AttendanceCalculator.classifyStatus(getPercentage());
    }

    public int getClassesNeededFor75() {
        return AttendanceCalculator.classesNeededToReach75(classesAttended, totalClasses);
    }

    public boolean isDefaulter() {
        return getPercentage() < 75.0;
    }

    public boolean isCritical() {
        return getPercentage() < 65.0;
    }

    /**
     * Converts this student into one line of text so it can be saved in a file.
     * Format: rollNo|name|division|totalClasses|classesAttended
     */
    public String toFileLine() {
        return rollNo + "|" + name + "|" + division + "|" + totalClasses + "|" + classesAttended;
    }

    /**
     * Creates a Student object from one line of the data file.
     */
    public static Student fromFileLine(String line) {
        String[] parts = line.split("\\|", -1);
        if (parts.length != 5) {
            throw new IllegalArgumentException("Invalid student record: " + line);
        }
        return new Student(
                parts[0],
                parts[1],
                parts[2],
                Integer.parseInt(parts[3]),
                Integer.parseInt(parts[4])
        );
    }
}
