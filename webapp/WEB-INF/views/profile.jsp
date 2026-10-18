<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="attendance.model.Teacher" %>
<%@ page import="attendance.model.Student" %>
<%@ page import="attendance.util.HtmlEscape" %>
<%
    Teacher teacher = (Teacher) session.getAttribute("teacher");
    Student student = (Student) session.getAttribute("student");
    boolean isTeacher = teacher != null;
    String fullName = isTeacher ? teacher.getFullName() : (student != null ? student.getName() : "");
    String email = isTeacher ? teacher.getEmail() : (student != null ? student.getEmail() : "");
    String username = isTeacher ? teacher.getUsername() : (student != null ? student.getRollNo() : "");
    String division = student != null ? student.getDivision() : "";
    String error = (String) request.getAttribute("error");
    String success = (String) request.getAttribute("success");
    String ctx = request.getContextPath();
%>
<%@ include file="layout-header.jsp" %>

<% if (success != null) { %>
    <div class="alert alert-ok"><%= HtmlEscape.e(success) %></div>
<% } %>
<% if (error != null) { %>
    <div class="alert alert-error"><%= HtmlEscape.e(error) %></div>
<% } %>

<div class="card">
    <h2>Profile details</h2>
    <p class="hint">
        <%= isTeacher ? "Teacher" : "Student" %> account:
        <strong><%= HtmlEscape.e(username) %></strong>
        <%= isTeacher ? "" : "(Roll Number)" %>
    </p>
    <form method="post" action="<%= ctx %>/profile" onsubmit="return validateProfileForm();">
        <input type="hidden" name="action" value="update">
        <div id="jsError" class="alert alert-error hidden"></div>
        <div class="form-grid">
            <div>
                <label for="fullName">Full name</label>
                <input id="fullName" name="fullName" type="text" value="<%= HtmlEscape.e(fullName) %>">
            </div>
            <div>
                <label for="email">Email ID</label>
                <input id="email" name="email" type="email" value="<%= HtmlEscape.e(email) %>">
            </div>
            <% if (!isTeacher) { %>
            <div>
                <label for="division">Division</label>
                <input id="division" name="division" type="text" value="<%= HtmlEscape.e(division) %>" placeholder="A, B, C...">
            </div>
            <% } %>
        </div>
        <button class="btn btn-primary" type="submit" style="width:auto;">Update Profile</button>
    </form>
</div>

<div class="card">
    <h2>Change password</h2>
    <form method="post" action="<%= ctx %>/profile" onsubmit="return validatePasswordForm();">
        <input type="hidden" name="action" value="password">
        <div id="jsError" class="alert alert-error hidden"></div>
        <div class="form-grid">
            <div class="full">
                <label for="currentPassword">Current password</label>
                <input id="currentPassword" name="currentPassword" type="password" autocomplete="current-password">
            </div>
            <div>
                <label for="password">New password</label>
                <input id="password" name="password" type="password" autocomplete="new-password">
            </div>
            <div>
                <label for="confirmPassword">Confirm new password</label>
                <input id="confirmPassword" name="confirmPassword" type="password" autocomplete="new-password">
            </div>
        </div>
        <button class="btn btn-primary" type="submit" style="width:auto;">Change Password</button>
    </form>
</div>

<%@ include file="layout-footer.jsp" %>
