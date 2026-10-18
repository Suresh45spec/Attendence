package attendance.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import attendance.dao.StudentDAO;
import attendance.model.Student;
import attendance.util.AttendanceCalculator;

/**
 * Defaulter list: everyone below 75%, with filters, sorting and CSV export.
 */
public class DefaulterServlet extends HttpServlet {

    private final StudentDAO studentDAO = new StudentDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String statusFilter = value(request.getParameter("status"));
        if (statusFilter.isEmpty()) {
            statusFilter = "All";
        }
        final String statusFilterFinal = statusFilter;
        String sort = value(request.getParameter("sort"));
        if (sort.isEmpty()) {
            sort = "percent";
        }
        String dir = value(request.getParameter("dir"));
        if (dir.isEmpty()) {
            dir = "asc";
        }

        try {
            List<Student> defaulters = studentDAO.findBelow75();
            if ("Critical".equals(statusFilterFinal) || "Defaulter".equals(statusFilterFinal)) {
                defaulters.removeIf(s -> !statusFilterFinal.equals(s.getStatus()));
            }
            StudentSorter.sort(defaulters, sort, dir);

            if ("csv".equals(value(request.getParameter("export")))) {
                writeCsv(response, defaulters);
                return;
            }

            request.setAttribute("students", defaulters);
            request.setAttribute("statusFilter", statusFilter);
            request.setAttribute("sortKey", sort);
            request.setAttribute("sortDir", dir);
            request.setAttribute("activePage", "defaulters");
            request.setAttribute("pageTitle", "Defaulter List");
            request.getRequestDispatcher("/WEB-INF/views/defaulters.jsp").forward(request, response);
        } catch (SQLException e) {
            request.setAttribute("errorMessage", "Could not generate the defaulter list.");
            request.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(request, response);
        }
    }

    private void writeCsv(HttpServletResponse response, List<Student> defaulters) throws IOException {
        response.setContentType("text/csv; charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=\"defaulter-list.csv\"");
        PrintWriter out = response.getWriter();
        out.println("Roll No,Name,Division,Total,Attended,Percentage,Status,Classes to 75%");
        for (Student student : defaulters) {
            out.println(csv(student.getRollNo()) + "," + csv(student.getName()) + ","
                    + csv(student.getDivision()) + "," + student.getTotalClasses() + ","
                    + student.getClassesAttended() + ","
                    + String.format("%.2f", student.getPercentage()) + ","
                    + student.getStatus() + "," + student.getClassesNeededFor75());
        }
    }

    private String csv(String value) {
        if (value == null) {
            return "";
        }
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }

    private String value(String text) {
        return text == null ? "" : text.trim();
    }

    /** Small helper so sorting logic stays out of the request handler. */
    private static final class StudentSorter {
        static void sort(List<Student> students, String key, String dir) {
            boolean desc = "desc".equalsIgnoreCase(dir);
            students.sort((a, b) -> {
                int cmp;
                if ("name".equals(key)) {
                    cmp = a.getName().compareToIgnoreCase(b.getName());
                } else if ("roll".equals(key)) {
                    cmp = a.getRollNo().compareToIgnoreCase(b.getRollNo());
                } else {
                    cmp = Double.compare(a.getPercentage(), b.getPercentage());
                }
                return desc ? -cmp : cmp;
            });
        }
    }
}
