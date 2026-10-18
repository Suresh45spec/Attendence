<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="attendance.util.HtmlEscape" %>
<%
    String error = (String) request.getAttribute("error");
    String formRole = (String) request.getAttribute("formRole");
    if (formRole == null) {
        formRole = "teacher";
    }
    String fullName = request.getAttribute("fullName") != null ? (String) request.getAttribute("fullName") : "";
    String email = request.getAttribute("email") != null ? (String) request.getAttribute("email") : "";
    String username = request.getAttribute("username") != null ? (String) request.getAttribute("username") : "";
    boolean studentRole = "student".equals(formRole);
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Register | Attendance</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/style.css">
</head>
<body class="login-body">
    <form class="login-card" method="post" action="<%= request.getContextPath() %>/register" onsubmit="return validateRegisterForm();">
        <p class="brand-kicker">College mini project</p>
        <h1>Create Account</h1>
        <p class="muted">One registration page for teachers and students</p>
        <% if (error != null) { %>
            <div class="alert alert-error"><%= HtmlEscape.e(error) %></div>
        <% } %>
        <div id="jsError" class="alert alert-error hidden"></div>

        <label for="fullName">Full Name</label>
        <input id="fullName" name="fullName" type="text" value="<%= HtmlEscape.e(fullName) %>">

        <label for="email">Email ID</label>
        <input id="email" name="email" type="email" value="<%= HtmlEscape.e(email) %>">

        <label for="username" id="usernameLabel"><%= studentRole ? "Roll Number" : "Username" %></label>
        <input id="username" name="username" type="text" value="<%= HtmlEscape.e(username) %>">

        <label for="password">Password</label>
        <input id="password" name="password" type="password" autocomplete="new-password">

        <label for="confirmPassword">Confirm Password</label>
        <input id="confirmPassword" name="confirmPassword" type="password" autocomplete="new-password">

        <fieldset class="radio-set">
            <legend>I am a</legend>
            <label class="check-label">
                <input type="radio" name="role" value="teacher" <%= !studentRole ? "checked" : "" %>
                       onchange="toggleRoleFields(this)"> Teacher
            </label>
            <label class="check-label">
                <input type="radio" name="role" value="student" <%= studentRole ? "checked" : "" %>
                       onchange="toggleRoleFields(this)"> Student
            </label>
        </fieldset>

        <button class="btn btn-primary" type="submit">Register</button>
        <div class="link-row">
            <a href="<%= request.getContextPath() %>/index.jsp">Already have an account? Login</a>
        </div>
    </form>
    <script src="<%= request.getContextPath() %>/js/app.js"></script>
</body>
</html>
