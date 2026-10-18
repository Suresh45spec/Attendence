package attendance.filter;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Role-based access control.
 * - Public: login pages, registration, forgot password, static files.
 * - Teacher-only: student management, marking, defaulters, reports.
 * - Student-only: my-attendance, history.
 * - Shared: dashboard and profile (each role sees its own view).
 */
public class AuthFilter implements Filter {

    private static final Set<String> PUBLIC = new HashSet<String>(Arrays.asList(
            "/",
            "/index.jsp",
            "/login",
            "/student-login",
            "/register",
            "/forgot-password"));

    private static final Set<String> TEACHER_ONLY = new HashSet<String>(Arrays.asList(
            "/students",
            "/attendance",
            "/defaulters",
            "/reports"));

    private static final Set<String> STUDENT_ONLY = new HashSet<String>(Arrays.asList(
            "/my-attendance",
            "/history"));

    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        String path = req.getRequestURI().substring(req.getContextPath().length());

        if (isPublic(path) || path.startsWith("/css/") || path.startsWith("/js/")
                || path.equals("/favicon.ico")) {
            chain.doFilter(request, response);
            return;
        }

        HttpSession session = req.getSession(false);
        boolean teacherIn = session != null && session.getAttribute("teacher") != null;
        boolean studentIn = session != null && session.getAttribute("student") != null;

        if (!teacherIn && !studentIn) {
            boolean studentPage = path.equals("/student-login") || path.equals("/register");
            resp.sendRedirect(req.getContextPath() + (studentPage ? "/student-login" : "/index.jsp"));
            return;
        }

        if (TEACHER_ONLY.contains(path) && !teacherIn) {
            resp.sendRedirect(req.getContextPath() + "/dashboard");
            return;
        }
        if (STUDENT_ONLY.contains(path) && !studentIn) {
            resp.sendRedirect(req.getContextPath() + "/dashboard");
            return;
        }

        chain.doFilter(request, response);
    }

    private boolean isPublic(String path) {
        return PUBLIC.contains(path);
    }

    @Override
    public void destroy() {
    }
}
