package attendance.servlet;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import attendance.dao.StudentDAO;
import attendance.model.Student;
import attendance.util.InputValidator;

/**
 * Student login: roll number + password.
 */
public class StudentLoginServlet extends HttpServlet {

    private final StudentDAO studentDAO = new StudentDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/views/student-login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String rollNo = request.getParameter("rollNo");
        String password = request.getParameter("password");
        boolean remember = "on".equals(request.getParameter("remember"));

        if (InputValidator.isBlank(rollNo) || InputValidator.isBlank(password)) {
            request.setAttribute("error", "Please enter both roll number and password.");
            request.getRequestDispatcher("/WEB-INF/views/student-login.jsp").forward(request, response);
            return;
        }

        try {
            Student student = studentDAO.login(rollNo, password);
            if (student == null) {
                request.setAttribute("error", "Invalid roll number or password.");
                request.setAttribute("rollNo", rollNo);
                request.getRequestDispatcher("/WEB-INF/views/student-login.jsp").forward(request, response);
                return;
            }
            HttpSession session = request.getSession(true);
            session.setAttribute("student", student);
            if (remember) {
                StudentLoginServlet.setRememberCookie(response, "studentRoll", rollNo);
            } else {
                StudentLoginServlet.clearRememberCookie(response, "studentRoll");
            }
            response.sendRedirect(request.getContextPath() + "/dashboard");
        } catch (SQLException e) {
            request.setAttribute("error",
                    "Could not connect to MySQL. Check that the server is running and WEB-INF/db.properties is correct.");
            request.getRequestDispatcher("/WEB-INF/views/student-login.jsp").forward(request, response);
        }
    }

    static void setRememberCookie(HttpServletResponse response, String name, String value) {
        Cookie cookie = new Cookie(name, value);
        cookie.setMaxAge(30 * 24 * 60 * 60);
        cookie.setPath("/");
        response.addCookie(cookie);
    }

    static void clearRememberCookie(HttpServletResponse response, String name) {
        Cookie cookie = new Cookie(name, "");
        cookie.setMaxAge(0);
        cookie.setPath("/");
        response.addCookie(cookie);
    }
}
