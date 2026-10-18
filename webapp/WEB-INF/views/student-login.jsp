<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="attendance.model.Student" %>
<%@ page import="attendance.util.HtmlEscape" %>
<%
    Student loggedIn = (Student) session.getAttribute("student");
    if (loggedIn != null) {
        response.sendRedirect(request.getContextPath() + "/dashboard");
        return;
    }
    String error = (String) request.getAttribute("error");
    String rollNo = (String) request.getAttribute("rollNo");
    if (rollNo == null) {
        rollNo = "";
    }
    javax.servlet.http.Cookie[] cookies = request.getCookies();
    if (cookies != null && rollNo.isEmpty()) {
        for (javax.servlet.http.Cookie cookie : cookies) {
            if ("studentRoll".equals(cookie.getName())) {
                rollNo = cookie.getValue();
            }
        }
    }
    boolean registered = "1".equals(request.getParameter("registered"));
    boolean reset = "1".equals(request.getParameter("reset"));
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Student Login | Attendance</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/style.css">
</head>
<body class="login-body">
    <form class="login-card" method="post" action="<%= request.getContextPath() %>/student-login" onsubmit="return validateStudentLoginForm();">
        <p class="brand-kicker">College mini project</p>
        <h1>Student Login</h1>
        <p class="muted">Check your attendance anytime</p>
        <% if (registered) { %>
            <div class="alert alert-ok">Registration successful! Please sign in.</div>
        <% } %>
        <% if (reset) { %>
            <div class="alert alert-ok">Password reset. Sign in with your new password.</div>
        <% } %>
        <% if (error != null) { %>
            <div class="alert alert-error"><%= HtmlEscape.e(error) %></div>
        <% } %>
        <div id="jsError" class="alert alert-error hidden"></div>
        <label for="rollNo">Roll Number</label>
        <input id="rollNo" name="rollNo" type="text" value="<%= HtmlEscape.e(rollNo) %>" autocomplete="username">
        <label for="password">Password</label>
        <input id="password" name="password" type="password" autocomplete="current-password">
        <label class="check-label">
            <input type="checkbox" name="remember" <%= !rollNo.isEmpty() ? "checked" : "" %>> Remember Me
        </label>
        <button class="btn btn-primary" type="submit">Login</button>
        <div class="link-row">
            <a href="<%= request.getContextPath() %>/forgot-password?role=student">Forgot Password?</a>
            <a href="<%= request.getContextPath() %>/index.jsp">Teacher Login</a>
            <a href="<%= request.getContextPath() %>/register">Register</a>
        </div>
        <p class="hint">Demo account: roll <strong>101</strong> / <strong>student123</strong></p>
    </form>
    <script src="<%= request.getContextPath() %>/js/app.js"></script>
</body>
</html>
