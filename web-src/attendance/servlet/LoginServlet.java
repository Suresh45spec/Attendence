package attendance.servlet;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import attendance.dao.TeacherDAO;
import attendance.model.Teacher;
import attendance.util.InputValidator;

public class LoginServlet extends HttpServlet {

    private final TeacherDAO teacherDAO = new TeacherDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendRedirect(request.getContextPath() + "/index.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        boolean remember = "on".equals(request.getParameter("remember"));

        if (InputValidator.isBlank(username) || InputValidator.isBlank(password)) {
            request.setAttribute("error", "Please enter both username and password.");
            request.getRequestDispatcher("/index.jsp").forward(request, response);
            return;
        }

        try {
            Teacher teacher = teacherDAO.login(username, password);
            if (teacher == null) {
                request.setAttribute("error", "Invalid username or password.");
                request.setAttribute("username", username);
                request.getRequestDispatcher("/index.jsp").forward(request, response);
                return;
            }
            HttpSession session = request.getSession(true);
            session.setAttribute("teacher", teacher);
            if (remember) {
                StudentLoginServlet.setRememberCookie(response, "teacherUser", username.trim());
            } else {
                StudentLoginServlet.clearRememberCookie(response, "teacherUser");
            }
            response.sendRedirect(request.getContextPath() + "/dashboard");
        } catch (SQLException e) {
            request.setAttribute("error",
                    "Could not connect to MySQL. Check that the server is running and WEB-INF/db.properties is correct.");
            request.getRequestDispatcher("/index.jsp").forward(request, response);
        }
    }
}
