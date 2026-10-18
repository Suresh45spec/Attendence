package attendance.servlet;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import attendance.dao.StudentDAO;
import attendance.dao.TeacherDAO;
import attendance.util.InputValidator;

/**
 * Common registration page for teachers and students.
 */
public class RegisterServlet extends HttpServlet {

    private final TeacherDAO teacherDAO = new TeacherDAO();
    private final StudentDAO studentDAO = new StudentDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String role = value(request.getParameter("role"));
        String fullName = value(request.getParameter("fullName"));
        String email = value(request.getParameter("email"));
        String username = value(request.getParameter("username"));
        String password = value(request.getParameter("password"));
        String confirm = value(request.getParameter("confirmPassword"));

        String error = validate(role, fullName, email, username, password, confirm);
        if (error == null) {
            try {
                if ("teacher".equals(role)) {
                    if (teacherDAO.usernameExists(username)) {
                        error = "This username is already taken.";
                    } else if (teacherDAO.emailExists(email)) {
                        error = "An account with this email already exists.";
                    } else {
                        teacherDAO.register(username, password, fullName, email);
                    }
                } else {
                    if (studentDAO.rollExists(username)) {
                        error = "This roll number is already registered.";
                    } else if (studentDAO.emailExists(email)) {
                        error = "An account with this email already exists.";
                    } else {
                        studentDAO.register(username, fullName, email, password);
                    }
                }
            } catch (SQLException e) {
                error = "Could not save the account. Please try again.";
            }
        }

        if (error != null) {
            request.setAttribute("error", error);
            request.setAttribute("formRole", role);
            request.setAttribute("fullName", fullName);
            request.setAttribute("email", email);
            request.setAttribute("username", username);
            request.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(request, response);
            return;
        }

        // Success: redirect to the matching login page with a confirmation.
        String target = "teacher".equals(role) ? "/index.jsp?registered=1" : "/student-login?registered=1";
        response.sendRedirect(request.getContextPath() + target);
    }

    private String validate(String role, String fullName, String email,
                            String username, String password, String confirm) {
        if (!"teacher".equals(role) && !"student".equals(role)) {
            return "Please choose a role (Teacher or Student).";
        }
        if (InputValidator.isBlank(fullName)) {
            return "Please enter your full name.";
        }
        if (!InputValidator.isValidEmail(email)) {
            return "Please enter a valid email address.";
        }
        if (InputValidator.isBlank(username)) {
            return "teacher".equals(role)
                    ? "Please enter a username."
                    : "Please enter your roll number.";
        }
        if (username.length() > 50) {
            return "Username / roll number is too long (max 50 characters).";
        }
        if (InputValidator.isBlank(password) || InputValidator.isBlank(confirm)) {
            return "Please enter and confirm your password.";
        }
        if (password.length() < 6) {
            return "Password must be at least 6 characters long.";
        }
        if (!password.equals(confirm)) {
            return "Passwords do not match.";
        }
        return null;
    }

    private String value(String text) {
        return text == null ? "" : text.trim();
    }
}
