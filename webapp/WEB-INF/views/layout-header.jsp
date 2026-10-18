<%@ page import="attendance.model.Teacher" %>
<%@ page import="attendance.model.Student" %>
<%@ page import="attendance.util.HtmlEscape" %>
<%
    Teacher layTeacher = (Teacher) session.getAttribute("teacher");
    Student layStudent = (Student) session.getAttribute("student");
    boolean layIsTeacher = layTeacher != null;
    String activePage = (String) request.getAttribute("activePage");
    if (activePage == null) {
        activePage = "";
    }
    String layCtx = request.getContextPath();
    String displayName = layIsTeacher ? layTeacher.getFullName() : (layStudent != null ? layStudent.getName() : "");
    String roleLabel = layIsTeacher ? "Teacher" : "Student";
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><%= request.getAttribute("pageTitle") != null ? request.getAttribute("pageTitle") : "Attendance" %></title>
    <link rel="stylesheet" href="<%= layCtx %>/css/style.css">
</head>
<body>
<div class="app">
    <aside class="sidebar">
        <h2>Attendance</h2>
        <% if (layIsTeacher) { %>
            <a class="<%= "dashboard".equals(activePage) ? "active" : "" %>" href="<%= layCtx %>/dashboard">Dashboard</a>
            <a class="<%= "add".equals(activePage) ? "active" : "" %>" href="<%= layCtx %>/students?action=new">Add Student</a>
            <a class="<%= "attendance".equals(activePage) ? "active" : "" %>" href="<%= layCtx %>/attendance">Mark Attendance</a>
            <a class="<%= "students".equals(activePage) ? "active" : "" %>" href="<%= layCtx %>/students">View Students</a>
            <a class="<%= "defaulters".equals(activePage) ? "active" : "" %>" href="<%= layCtx %>/defaulters">Defaulter List</a>
            <a class="<%= "reports".equals(activePage) ? "active" : "" %>" href="<%= layCtx %>/reports">Reports</a>
            <a class="<%= "profile".equals(activePage) ? "active" : "" %>" href="<%= layCtx %>/profile">Profile</a>
        <% } else { %>
            <a class="<%= "dashboard".equals(activePage) ? "active" : "" %>" href="<%= layCtx %>/dashboard">Dashboard</a>
            <a class="<%= "my-attendance".equals(activePage) ? "active" : "" %>" href="<%= layCtx %>/my-attendance">My Attendance</a>
            <a class="<%= "history".equals(activePage) ? "active" : "" %>" href="<%= layCtx %>/history">Attendance History</a>
            <a class="<%= "profile".equals(activePage) ? "active" : "" %>" href="<%= layCtx %>/profile">My Profile</a>
        <% } %>
        <a class="logout" href="<%= layCtx %>/logout" onclick="confirmLogout(event)">Logout</a>
        <p class="hint">Signed in as <strong><%= HtmlEscape.e(roleLabel) %></strong><br>
            <strong><%= HtmlEscape.e(displayName) %></strong>
            <% if (!layIsTeacher && layStudent != null) { %><br>Roll: <strong><%= HtmlEscape.e(layStudent.getRollNo()) %></strong><% } %>
        </p>
    </aside>
    <main class="content">
        <div class="topbar">
            <button class="menu-toggle" type="button" onclick="toggleSidebar()">Menu</button>
            <h1><%= request.getAttribute("pageTitle") != null ? request.getAttribute("pageTitle") : "Attendance" %></h1>
            <span class="muted">Threshold: 75% eligible</span>
        </div>
