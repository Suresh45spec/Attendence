package attendance.servlet;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import attendance.dao.AttendanceDAO;
import attendance.dao.StudentDAO;
import attendance.model.AttendanceRecord;
import attendance.model.Student;
import attendance.model.Teacher;
import attendance.util.Subjects;

/**
 * Teacher flow: pick class (division), subject and date -> mark each
 * student Present/Absent -> save once. Duplicates are rejected and
 * re-submitting corrects earlier entries.
 */
public class MarkAttendanceServlet extends HttpServlet {

    private final StudentDAO studentDAO = new StudentDAO();
    private final AttendanceDAO attendanceDAO = new AttendanceDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            String date = value(request.getParameter("date"));
            if (date.isEmpty()) {
                date = LocalDate.now().toString();
            }
            String subject = value(request.getParameter("subject"));
            if (subject.isEmpty() || !Subjects.isValid(subject)) {
                subject = Subjects.ALL.get(0);
            }
            String division = value(request.getParameter("division"));
            List<String> divisions = studentDAO.findDivisions();
            if (division.isEmpty() && !divisions.isEmpty()) {
                division = divisions.get(0);
            }

            List<Student> roster = division.isEmpty()
                    ? studentDAO.findAll()
                    : studentDAO.findByDivision(division);

            // Pre-select any attendance already stored for this session.
            for (Student student : roster) {
                request.setAttribute("status_" + student.getRollNo(),
                        attendanceDAO.findStatus(student.getRollNo(), subject, date));
            }

            request.setAttribute("roster", roster);
            request.setAttribute("divisions", divisions);
            request.setAttribute("subjects", Subjects.ALL);
            request.setAttribute("selectedDate", date);
            request.setAttribute("selectedSubject", subject);
            request.setAttribute("selectedDivision", division);
            request.setAttribute("savedCount", value(request.getParameter("saved")));
            request.setAttribute("activePage", "attendance");
            request.setAttribute("pageTitle", "Mark Attendance");
            request.getRequestDispatcher("/WEB-INF/views/mark-attendance.jsp").forward(request, response);
        } catch (SQLException e) {
            request.setAttribute("errorMessage", "Could not load the attendance form from MySQL.");
            request.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String date = value(request.getParameter("date"));
        String subject = value(request.getParameter("subject"));
        String division = value(request.getParameter("division"));

        try {
            if (date.isEmpty() || !Subjects.isValid(subject)) {
                request.setAttribute("error", "Please choose a valid date and subject.");
                doGet(request, response);
                return;
            }

            List<Student> roster = division.isEmpty()
                    ? studentDAO.findAll()
                    : studentDAO.findByDivision(division);

            List<AttendanceRecord> records = new ArrayList<AttendanceRecord>();
            for (Student student : roster) {
                String status = request.getParameter("st_" + student.getRollNo());
                if (!AttendanceRecord.PRESENT.equals(status) && !AttendanceRecord.ABSENT.equals(status)) {
                    continue; // untouched row
                }
                records.add(new AttendanceRecord(student.getRollNo(), subject, date, status));
            }

            if (records.isEmpty()) {
                request.setAttribute("error", "Mark at least one student as Present or Absent.");
                doGet(request, response);
                return;
            }

            HttpSession session = request.getSession(false);
            String markedBy = "";
            if (session != null && session.getAttribute("teacher") != null) {
                markedBy = ((Teacher) session.getAttribute("teacher")).getUsername();
            }
            int saved = attendanceDAO.saveBatch(records, markedBy);

            response.sendRedirect(request.getContextPath()
                    + "/attendance?date=" + date
                    + "&subject=" + java.net.URLEncoder.encode(subject, "UTF-8")
                    + "&division=" + java.net.URLEncoder.encode(division, "UTF-8")
                    + "&saved=" + saved);
        } catch (SQLException e) {
            request.setAttribute("errorMessage", "Could not save attendance records to MySQL.");
            request.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(request, response);
        }
    }

    private String value(String text) {
        return text == null ? "" : text.trim();
    }
}
