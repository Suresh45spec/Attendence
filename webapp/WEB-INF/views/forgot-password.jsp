<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="attendance.util.HtmlEscape" %>
<%
    String error = (String) request.getAttribute("error");
    String formRole = (String) request.getAttribute("formRole");
    String paramRole = request.getParameter("role");
    if (formRole == null) {
        formRole = "student".equals(paramRole) ? "student" : "teacher";
    }
    String username = request.getAttribute("username") != null ? (String) request.getAttribute("username") : "";
    String email = request.getAttribute("email") != null ? (String) request.getAttribute("email") : "";
    boolean studentRole = "student".equals(formRole);
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Forgot Password | Attendance</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/style.css">
</head>
<body class="login-body">
    <form class="login-card" method="post" action="<%= request.getContextPath() %>/forgot-password" onsubmit="return validateForgotForm();">
        <p class="brand-kicker">Account recovery</p>
        <h1>Forgot Password</h1>
        <p class="muted">Verify your account details and set a new password</p>
        <% if (error != null) { %>
            <div class="alert alert-error"><%= HtmlEscape.e(error) %></div>
        <% } %>
        <div id="jsError" class="alert alert-error hidden"></div>

        <fieldset class="radio-set">
            <legend>Account type</legend>
            <label class="check-label">
                <input type="radio" name="role" value="teacher" <%= !studentRole ? "checked" : "" %>
                       onchange="toggleRoleFields(this)"> Teacher
            </label>
            <label class="check-label">
                <input type="radio" name="role" value="student" <%= studentRole ? "checked" : "" %>
                       onchange="toggleRoleFields(this)"> Student
            </label>
        </fieldset>

        <label for="username" id="usernameLabel"><%= studentRole ? "Roll Number" : "Username" %></label>
        <input id="username" name="username" type="text" value="<%= HtmlEscape.e(username) %>">

        <label for="email">Email ID on file</label>
        <input id="email" name="email" type="email" value="<%= HtmlEscape.e(email) %>">

        <label for="password">New Password</label>
        <input id="password" name="password" type="password" autocomplete="new-password">

        <label for="confirmPassword">Confirm New Password</label>
        <input id="confirmPassword" name="confirmPassword" type="password" autocomplete="new-password">

        <button class="btn btn-primary" type="submit">Reset Password</button>
        <div class="link-row">
            <a href="<%= request.getContextPath() %>/index.jsp">Back to Teacher Login</a>
            <a href="<%= request.getContextPath() %>/student-login">Student Login</a>
        </div>
    </form>
    <script src="<%= request.getContextPath() %>/js/app.js"></script>
</body>
</html>
