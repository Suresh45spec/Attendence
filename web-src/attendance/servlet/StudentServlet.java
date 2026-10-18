package attendance.servlet;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import attendance.dao.StudentDAO;
import attendance.model.Student;
import attendance.util.InputValidator;

public class StudentServlet extends HttpServlet {

    private final StudentDAO studentDAO = new StudentDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = value(request.getParameter("action"));
        try {
            if ("new".equals(action)) {
                request.setAttribute("mode", "add");
                request.setAttribute("activePage", "add");
                request.setAttribute("pageTitle", "Add Student");
                request.getRequestDispatcher("/WEB-INF/views/student-form.jsp").forward(request, response);
                return;
            }
            if ("edit".equals(action)) {
                String rollNo = value(request.getParameter("roll"));
                Student student = studentDAO.findByRollNo(rollNo);
                if (student == null) {
                    request.setAttribute("error", "No student found with roll number " + rollNo + ".");
                    listStudents(request, response, "");
                    return;
                }
                request.setAttribute("mode", "edit");
                request.setAttribute("student", student);
                request.setAttribute("activePage", "students");
                request.setAttribute("pageTitle", "Update Student");
                request.getRequestDispatcher("/WEB-INF/views/student-form.jsp").forward(request, response);
                return;
            }
            listStudents(request, response, value(request.getParameter("q")));
        } catch (SQLException e) {
            request.setAttribute("errorMessage", "Could not load student records from MySQL.");
            request.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String action = value(request.getParameter("action"));
        try {
            if ("add".equals(action)) {
                handleAdd(request, response);
                return;
            }
            if ("update".equals(action)) {
                handleUpdate(request, response);
                return;
            }
            response.sendRedirect(request.getContextPath() + "/students");
        } catch (SQLException e) {
            request.setAttribute("errorMessage", "A database error occurred while saving the student.");
            request.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(request, response);
        }
    }

    private void handleAdd(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, SQLException {
        String rollNo = value(request.getParameter("rollNo"));
        String name = value(request.getParameter("name"));
        String division = value(request.getParameter("division"));
        String email = value(request.getParameter("email"));
        String totalText = value(request.getParameter("totalClasses"));
        String attendedText = value(request.getParameter("classesAttended"));

        String error = InputValidator.validateStudent(rollNo, name, division, totalText, attendedText, true);
        if (error == null && !email.isEmpty() && !InputValidator.isValidEmail(email)) {
            error = "Please enter a valid email address.";
        }
        if (error == null && studentDAO.findByRollNo(rollNo) != null) {
            error = "A student with this roll number already exists.";
        }
        if (error != null) {
            request.setAttribute("error", error);
            request.setAttribute("mode", "add");
            request.setAttribute("activePage", "add");
            request.setAttribute("pageTitle", "Add Student");
            request.setAttribute("student", new Student(rollNo, name, division,
                    parseOrZero(totalText), parseOrZero(attendedText), email));
            request.getRequestDispatcher("/WEB-INF/views/student-form.jsp").forward(request, response);
            return;
        }

        Student student = new Student(
                rollNo.trim(),
                name.trim(),
                division.trim().toUpperCase(),
                Integer.parseInt(totalText.trim()),
                Integer.parseInt(attendedText.trim()),
                email.trim());
        studentDAO.insert(student);
        response.sendRedirect(request.getContextPath() + "/students?msg=added");
    }

    private void handleUpdate(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, SQLException {
        String rollNo = value(request.getParameter("rollNo"));
        String name = value(request.getParameter("name"));
        String division = value(request.getParameter("division"));
        String email = value(request.getParameter("email"));
        String totalText = value(request.getParameter("totalClasses"));
        String attendedText = value(request.getParameter("classesAttended"));

        String error = InputValidator.validateStudent(rollNo, name, division, totalText, attendedText, true);
        if (error == null && !email.isEmpty() && !InputValidator.isValidEmail(email)) {
            error = "Please enter a valid email address.";
        }
        if (error != null) {
            request.setAttribute("error", error);
            request.setAttribute("mode", "edit");
            request.setAttribute("activePage", "students");
            request.setAttribute("pageTitle", "Update Student");
            request.setAttribute("student", new Student(rollNo, name, division,
                    parseOrZero(totalText), parseOrZero(attendedText), email));
            request.getRequestDispatcher("/WEB-INF/views/student-form.jsp").forward(request, response);
            return;
        }

        Student student = new Student(
                rollNo.trim(),
                name.trim(),
                division.trim().toUpperCase(),
                Integer.parseInt(totalText.trim()),
                Integer.parseInt(attendedText.trim()),
                email.trim());
        boolean updated = studentDAO.update(student);
        if (!updated) {
            request.setAttribute("error", "No student found with roll number " + rollNo + ".");
            listStudents(request, response, "");
            return;
        }
        response.sendRedirect(request.getContextPath() + "/students?msg=updated");
    }

    private void listStudents(HttpServletRequest request, HttpServletResponse response, String query)
            throws ServletException, IOException, SQLException {
        String status = value(request.getParameter("status"));
        String sort = value(request.getParameter("sort"));
        String dir = value(request.getParameter("dir"));

        List<Student> students;
        if (!query.isEmpty()) {
            students = studentDAO.search(query);
        } else {
            students = studentDAO.findAll(
                    status.isEmpty() ? null : status,
                    sort.isEmpty() ? "roll" : sort,
                    dir.isEmpty() ? "asc" : dir);
        }
        request.setAttribute("students", students);
        request.setAttribute("query", query);
        request.setAttribute("statusFilter", status.isEmpty() ? "All" : status);
        request.setAttribute("sortKey", sort.isEmpty() ? "roll" : sort);
        request.setAttribute("sortDir", dir.isEmpty() ? "asc" : dir);
        request.setAttribute("activePage", "students");
        request.setAttribute("pageTitle", "Students");
        String msg = value(request.getParameter("msg"));
        if ("added".equals(msg)) {
            request.setAttribute("success", "Student record added.");
        } else if ("updated".equals(msg)) {
            request.setAttribute("success", "Student record updated.");
        }
        request.getRequestDispatcher("/WEB-INF/views/students.jsp").forward(request, response);
    }

    private String value(String text) {
        return text == null ? "" : text.trim();
    }

    private int parseOrZero(String text) {
        try {
            return Integer.parseInt(text.trim());
        } catch (Exception e) {
            return 0;
        }
    }
}
