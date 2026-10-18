package attendance.servlet;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import attendance.dao.StudentDAO;
import attendance.dao.TeacherDAO;
import attendance.model.Student;
import attendance.model.Teacher;
import attendance.util.InputValidator;

/**
 * Basic password reset: role + username/roll + email on file -> new password.
 */
public class ForgotPasswordServlet extends HttpServlet {

    private final TeacherDAO teacherDAO = new TeacherDAO();
    private final StudentDAO studentDAO = new StudentDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/views/forgot-password.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String role = value(request.getParameter("role"));
        String username = value(request.getParameter("username"));
        String email = value(request.getParameter("email"));
        String password = value(request.getParameter("password"));
        String confirm = value(request.getParameter("confirmPassword"));

        String error = null;
        if (InputValidator.isBlank(username) || InputValidator.isBlank(email)
                || InputValidator.isBlank(password) || InputValidator.isBlank(confirm)) {
            error = "Please fill in all fields.";
        } else if (!InputValidator.isValidEmail(email)) {
            error = "Please enter a valid email address.";
        } else if (password.length() < 6) {
            error = "New password must be at least 6 characters long.";
        } else if (!password.equals(confirm)) {
            error = "Passwords do not match.";
        }

        if (error == null) {
            try {
                if ("teacher".equals(role)) {
                    Teacher teacher = teacherDAO.findByUsernameAndEmail(username, email);
                    if (teacher == null) {
                        error = "No teacher account matches that username and email.";
                    } else {
                        teacherDAO.updatePassword(username, password);
                    }
                } else {
                    Student student = studentDAO.findByRollAndEmail(username, email);
                    if (student == null) {
                        error = "No student account matches that roll number and email.";
                    } else {
                        studentDAO.updatePassword(username, password);
                    }
                }
            } catch (SQLException e) {
                error = "Could not reset the password. Please try again.";
            }
        }

        if (error != null) {
            request.setAttribute("error", error);
            request.setAttribute("formRole", role);
            request.setAttribute("username", username);
            request.setAttribute("email", email);
            request.getRequestDispatcher("/WEB-INF/views/forgot-password.jsp").forward(request, response);
            return;
        }

        String target = "teacher".equals(role) ? "/index.jsp?reset=1" : "/student-login?reset=1";
        response.sendRedirect(request.getContextPath() + target);
    }

    private String value(String text) {
        return text == null ? "" : text.trim();
    }
}
