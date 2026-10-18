package attendance.servlet;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import attendance.dao.AttendanceDAO;
import attendance.dao.StudentDAO;
import attendance.model.Student;
import attendance.util.AttendanceCalculator;

/**
 * Dispatches to the teacher dashboard or the student dashboard
 * depending on who is logged in.
 */
public class DashboardServlet extends HttpServlet {

    private final StudentDAO studentDAO = new StudentDAO();
    private final AttendanceDAO attendanceDAO = new AttendanceDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        boolean teacherIn = session != null && session.getAttribute("teacher") != null;
        boolean studentIn = session != null && session.getAttribute("student") != null;

        if (!teacherIn && !studentIn) {
            response.sendRedirect(request.getContextPath() + "/index.jsp");
            return;
        }

        if (teacherIn) {
            showTeacherDashboard(request, response);
        } else {
            showStudentDashboard(request, response);
        }
    }

    private void showTeacherDashboard(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            List<Student> all = studentDAO.findAll();
            List<Student> critical = studentDAO.findCritical();
            String today = LocalDate.now().toString();

            request.setAttribute("totalStudents", all.size());
            request.setAttribute("presentToday", attendanceDAO.countPresentOn(today));
            int below75 = 0;
            for (Student student : all) {
                if (student.getPercentage() < AttendanceCalculator.ELIGIBLE_PERCENT
                        && student.getTotalClasses() > 0) {
                    below75++;
                }
            }
            request.setAttribute("below75Count", below75);
            request.setAttribute("criticalCount", critical.size());
            request.setAttribute("eligibleCount", studentDAO.countByStatus(AttendanceCalculator.STATUS_ELIGIBLE));
            request.setAttribute("defaulterCount", studentDAO.countByStatus(AttendanceCalculator.STATUS_DEFAULTER));
            request.setAttribute("criticalStudents", critical);
            request.setAttribute("recentStudents", all);
            request.setAttribute("activePage", "dashboard");
            request.setAttribute("pageTitle", "Teacher Dashboard");
            request.getRequestDispatcher("/WEB-INF/views/dashboard.jsp").forward(request, response);
        } catch (SQLException e) {
            request.setAttribute("errorMessage", "Could not load dashboard data from MySQL.");
            request.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(request, response);
        }
    }

    private void showStudentDashboard(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        Student student = (Student) session.getAttribute("student");
        try {
            // Refresh from DB so the numbers are always current.
            Student fresh = studentDAO.findByRollNo(student.getRollNo());
            if (fresh != null) {
                student = fresh;
                session.setAttribute("student", student);
            }
            Map<String, int[]> subjectWise = attendanceDAO.subjectWiseForRoll(student.getRollNo());

            int total = student.getTotalClasses();
            int attended = student.getClassesAttended();
            String status = total > 0
                    ? AttendanceCalculator.classifyStatus(student.getPercentage())
                    : "No classes yet";

            request.setAttribute("totalClasses", total);
            request.setAttribute("classesAttended", attended);
            request.setAttribute("classesMissed", total - attended);
            request.setAttribute("percent", AttendanceCalculator.formatPercentage(student.getPercentage()));
            request.setAttribute("attendanceStatus", status);
            request.setAttribute("classesNeeded", student.getClassesNeededFor75());
            request.setAttribute("subjectWise", subjectWise);
            request.setAttribute("activePage", "dashboard");
            request.setAttribute("pageTitle", "Student Dashboard");
            request.getRequestDispatcher("/WEB-INF/views/student-dashboard.jsp").forward(request, response);
        } catch (SQLException e) {
            request.setAttribute("errorMessage", "Could not load your attendance data.");
            request.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(request, response);
        }
    }
}
