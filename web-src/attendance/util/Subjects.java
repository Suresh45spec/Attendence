package attendance.util;

import java.util.Arrays;
import java.util.List;

/**
 * Subjects offered for attendance. Kept as a fixed list so that
 * mark-attendance, reports, and history all stay consistent.
 */
public final class Subjects {

    public static final List<String> ALL = Arrays.asList(
            "Mathematics",
            "Science",
            "English",
            "History",
            "Computer Science",
            "Physical Education");

    private Subjects() {
    }

    public static boolean isValid(String subject) {
        return subject != null && ALL.contains(subject);
    }
}
