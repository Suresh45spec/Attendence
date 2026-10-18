package attendance.servlet;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import attendance.dao.AttendanceDAO;
import attendance.model.AttendanceRecord;
import attendance.model.Student;
import attendance.util.AttendanceCalculator;
import attendance.util.Subjects;

/**
 * Student-only pages: /my-attendance (summary + subject-wise)
 * and /history (full record with filters).
 */
public class StudentAttendanceServlet extends HttpServlet {

    private final AttendanceDAO attendanceDAO = new AttendanceDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        Student student = session != null ? (Student) session.getAttribute("student") : null;
        if (student == null) {
            response.sendRedirect(request.getContextPath() + "/student-login");
            return;
        }

        String path = request.getRequestURI().substring(request.getContextPath().length());
        try {
            if ("/history".equals(path)) {
                String subject = value(request.getParameter("subject"));
                String from = value(request.getParameter("from"));
                String to = value(request.getParameter("to"));

                List<AttendanceRecord> records = attendanceDAO.historyForRoll(student.getRollNo());
                if (!subject.isEmpty()) {
                    records.removeIf(r -> !subject.equals(r.getSubject()));
                }
                if (!from.isEmpty()) {
                    records.removeIf(r -> r.getClassDate().compareTo(from) < 0);
                }
                if (!to.isEmpty()) {
                    records.removeIf(r -> r.getClassDate().compareTo(to) > 0);
                }

                request.setAttribute("records", records);
                request.setAttribute("filterSubject", subject);
                request.setAttribute("filterFrom", from);
                request.setAttribute("filterTo", to);
                request.setAttribute("subjects", Subjects.ALL);
                request.setAttribute("activePage", "history");
                request.setAttribute("pageTitle", "Attendance History");
                request.getRequestDispatcher("/WEB-INF/views/history.jsp").forward(request, response);
            } else {
                Map<String, int[]> subjectWise = attendanceDAO.subjectWiseForRoll(student.getRollNo());
                request.setAttribute("subjectWise", subjectWise);
                request.setAttribute("percent",
                        AttendanceCalculator.formatPercentage(student.getPercentage()));
                request.setAttribute("activePage", "my-attendance");
                request.setAttribute("pageTitle", "My Attendance");
                request.getRequestDispatcher("/WEB-INF/views/my-attendance.jsp").forward(request, response);
            }
        } catch (SQLException e) {
            request.setAttribute("errorMessage", "Could not load your attendance records.");
            request.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(request, response);
        }
    }

    private String value(String text) {
        return text == null ? "" : text.trim();
    }
}
