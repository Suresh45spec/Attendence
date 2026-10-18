package attendance.servlet;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import attendance.dao.StudentDAO;
import attendance.dao.TeacherDAO;
import attendance.model.Student;
import attendance.model.Teacher;
import attendance.util.InputValidator;

/**
 * Profile page shared by both roles: view / update details, change password.
 */
public class ProfileServlet extends HttpServlet {

    private final TeacherDAO teacherDAO = new TeacherDAO();
    private final StudentDAO studentDAO = new StudentDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setAttribute("activePage", "profile");
        request.setAttribute("pageTitle", "My Profile");
        request.getRequestDispatcher("/WEB-INF/views/profile.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String action = value(request.getParameter("action"));
        HttpSession session = request.getSession(false);
        Teacher teacher = session != null ? (Teacher) session.getAttribute("teacher") : null;
        Student student = session != null ? (Student) session.getAttribute("student") : null;

        if (teacher == null && student == null) {
            response.sendRedirect(request.getContextPath() + "/index.jsp");
            return;
        }

        try {
            if ("password".equals(action)) {
                handleChangePassword(request, response, session, teacher, student);
            } else {
                handleUpdateProfile(request, response, session, teacher, student);
            }
        } catch (SQLException e) {
            request.setAttribute("error", "A database error occurred. Please try again.");
            request.setAttribute("activePage", "profile");
            request.setAttribute("pageTitle", "My Profile");
            request.getRequestDispatcher("/WEB-INF/views/profile.jsp").forward(request, response);
        }
    }

    private void handleUpdateProfile(HttpServletRequest request, HttpServletResponse response,
                                     HttpSession session, Teacher teacher, Student student)
            throws ServletException, IOException, SQLException {
        String fullName = value(request.getParameter("fullName"));
        String email = value(request.getParameter("email"));
        String division = value(request.getParameter("division"));

        String error = null;
        if (InputValidator.isBlank(fullName)) {
            error = "Please enter your name.";
        } else if (!InputValidator.isValidEmail(email)) {
            error = "Please enter a valid email address.";
        }

        if (error == null) {
            if (teacher != null) {
                teacherDAO.updateProfile(teacher.getUsername(), fullName, email);
                teacher.setFullName(fullName);
                teacher.setEmail(email);
                session.setAttribute("teacher", teacher);
            } else {
                studentDAO.updateProfile(student.getRollNo(), fullName, email,
                        InputValidator.isBlank(division) ? student.getDivision() : division);
                student.setName(fullName);
                student.setEmail(email);
                if (!InputValidator.isBlank(division)) {
                    student.setDivision(division.trim().toUpperCase());
                }
                session.setAttribute("student", student);
            }
            request.setAttribute("success", "Profile updated.");
        } else {
            request.setAttribute("error", error);
        }
        doGet(request, response);
    }

    private void handleChangePassword(HttpServletRequest request, HttpServletResponse response,
                                      HttpSession session, Teacher teacher, Student student)
            throws ServletException, IOException, SQLException {
        String current = value(request.getParameter("currentPassword"));
        String password = value(request.getParameter("password"));
        String confirm = value(request.getParameter("confirmPassword"));

        String error = null;
        if (InputValidator.isBlank(current) || InputValidator.isBlank(password) || InputValidator.isBlank(confirm)) {
            error = "Please fill in all password fields.";
        } else if (password.length() < 6) {
            error = "New password must be at least 6 characters long.";
        } else if (!password.equals(confirm)) {
            error = "New passwords do not match.";
        }

        if (error == null) {
            boolean currentOk;
            if (teacher != null) {
                currentOk = teacherDAO.login(teacher.getUsername(), current) != null;
                if (currentOk) {
                    teacherDAO.updatePassword(teacher.getUsername(), password);
                }
            } else {
                currentOk = studentDAO.login(student.getRollNo(), current) != null;
                if (currentOk) {
                    studentDAO.updatePassword(student.getRollNo(), password);
                }
            }
            error = currentOk ? null : "Current password is incorrect.";
        }

        if (error == null) {
            request.setAttribute("success", "Password changed successfully.");
        } else {
            request.setAttribute("error", error);
        }
        doGet(request, response);
    }

    private String value(String text) {
        return text == null ? "" : text.trim();
    }
}
