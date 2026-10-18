package attendance.util;

/**
 * All attendance math lives here so the GUI stays simple.
 * These methods are static because they do not need object state.
 */
public class AttendanceCalculator {

    public static final double ELIGIBLE_PERCENT = 75.0;
    public static final double CRITICAL_PERCENT = 65.0;

    public static final String STATUS_ELIGIBLE = "Eligible";
    public static final String STATUS_DEFAULTER = "Defaulter";
    public static final String STATUS_CRITICAL = "Critical";
    public static final String STATUS_NA = "N/A";

    private AttendanceCalculator() {
        // Utility class - do not create objects of this class
    }

    public static double calculatePercentage(int classesAttended, int totalClasses) {
        if (totalClasses <= 0) {
            return 0.0;
        }
        return (classesAttended * 100.0) / totalClasses;
    }

    /**
     * >= 75% Eligible, 65% to 74.99% Defaulter, below 65% Critical
     */
    public static String classifyStatus(double percentage) {
        if (percentage >= ELIGIBLE_PERCENT) {
            return STATUS_ELIGIBLE;
        }
        if (percentage >= CRITICAL_PERCENT) {
            return STATUS_DEFAULTER;
        }
        return STATUS_CRITICAL;
    }

    /**
     * Finds the smallest number of extra consecutive classes a student must
     * attend so that attendance becomes at least 75%.
     *
     * Formula:
     *   (attended + x) / (total + x) >= 0.75
     *   x >= 3 * total - 4 * attended
     *
     * If the student is already at 75% or more, the answer is 0.
     */
    public static int classesNeededToReach75(int classesAttended, int totalClasses) {
        if (totalClasses <= 0) {
            return 0;
        }
        if (calculatePercentage(classesAttended, totalClasses) >= ELIGIBLE_PERCENT) {
            return 0;
        }

        int needed = (3 * totalClasses) - (4 * classesAttended);
        if (needed < 0) {
            needed = 0;
        }
        return needed;
    }

    public static String formatPercentage(double percentage) {
        return String.format("%.2f%%", percentage);
    }
}
