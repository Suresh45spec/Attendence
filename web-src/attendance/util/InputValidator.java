package attendance.util;

/**
 * Shared field checks for add/update student forms.
 * Mirrors the rules used by the original Swing AttendanceService.
 */
public class InputValidator {

    private InputValidator() {
    }

    public static String validateStudent(
            String rollNo,
            String name,
            String division,
            String totalText,
            String attendedText,
            boolean requireRoll) {

        if (requireRoll && isBlank(rollNo)) {
            return "Please enter a roll number.";
        }
        if (isBlank(name) || isBlank(division) || isBlank(totalText) || isBlank(attendedText)) {
            return "Please fill all student details.";
        }
        if (requireRoll && rollNo.trim().length() > 20) {
            return "Roll number is too long (max 20 characters).";
        }
        if (name.trim().length() > 100) {
            return "Name is too long (max 100 characters).";
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
        return null;
    }

    public static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    /** Simple email format check: something@something.domain */
    public static boolean isValidEmail(String email) {
        if (isBlank(email)) {
            return false;
        }
        String trimmed = email.trim();
        return trimmed.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    }
}
