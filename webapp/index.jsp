<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="attendance.model.Teacher" %>
<%@ page import="attendance.util.HtmlEscape" %>
<%
    Teacher loggedIn = (Teacher) session.getAttribute("teacher");
    if (loggedIn != null) {
        response.sendRedirect(request.getContextPath() + "/dashboard");
        return;
    }
    String error = (String) request.getAttribute("error");
    String username = (String) request.getAttribute("username");
    if (username == null) {
        username = "";
    }
    // Remember Me: prefill from cookie
    javax.servlet.http.Cookie[] cookies = request.getCookies();
    if (cookies != null && username.isEmpty()) {
        for (javax.servlet.http.Cookie cookie : cookies) {
            if ("teacherUser".equals(cookie.getName())) {
                username = cookie.getValue();
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
    <title>Teacher Login | Attendance</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/style.css">
</head>
<body class="login-body">
    <form class="login-card" method="post" action="<%= request.getContextPath() %>/login" onsubmit="return validateLoginForm();">
        <p class="brand-kicker">College mini project</p>
        <h1>Teacher Login</h1>
        <p class="muted">Student Attendance Management System</p>
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
        <label for="username">Username</label>
        <input id="username" name="username" type="text" value="<%= HtmlEscape.e(username) %>" autocomplete="username">
        <label for="password">Password</label>
        <input id="password" name="password" type="password" autocomplete="current-password">
        <label class="check-label">
            <input type="checkbox" name="remember" <%= !username.isEmpty() ? "checked" : "" %>> Remember Me
        </label>
        <button class="btn btn-primary" type="submit">Login</button>
        <div class="link-row">
            <a href="<%= request.getContextPath() %>/forgot-password?role=teacher">Forgot Password?</a>
            <a href="<%= request.getContextPath() %>/student-login">Student Login</a>
            <a href="<%= request.getContextPath() %>/register">Register</a>
        </div>
        <p class="hint">Demo account: <strong>teacher</strong> / <strong>teacher123</strong></p>
    </form>
    <script src="<%= request.getContextPath() %>/js/app.js"></script>
</body>
</html>
