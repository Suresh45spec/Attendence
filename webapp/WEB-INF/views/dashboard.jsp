<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="attendance.model.Student" %>
<%@ page import="attendance.util.AttendanceCalculator" %>
<%@ page import="attendance.util.HtmlEscape" %>
<%
    @SuppressWarnings("unchecked")
    List<Student> criticalStudents = (List<Student>) request.getAttribute("criticalStudents");
    @SuppressWarnings("unchecked")
    List<Student> recentStudents = (List<Student>) request.getAttribute("recentStudents");
    if (criticalStudents == null) {
        criticalStudents = java.util.Collections.emptyList();
    }
    if (recentStudents == null) {
        recentStudents = java.util.Collections.emptyList();
    }
    String ctx = request.getContextPath();
%>
<%@ include file="layout-header.jsp" %>

<div class="stats">
    <div class="stat-card">
        Total Students
        <strong><%= request.getAttribute("totalStudents") %></strong>
    </div>
    <div class="stat-card">
        Present Today
        <strong><%= request.getAttribute("presentToday") %></strong>
    </div>
    <div class="stat-card">
        Attendance Below 75%
        <strong><%= request.getAttribute("below75Count") %></strong>
    </div>
    <div class="stat-card critical">
        Critical Below 65%
        <strong><%= request.getAttribute("criticalCount") %></strong>
    </div>
</div>

<div class="quick-actions">
    <a class="btn btn-secondary" href="<%= ctx %>/attendance">Mark Today's Attendance</a>
    <a class="btn btn-secondary" href="<%= ctx %>/defaulters">View Defaulter List</a>
    <a class="btn btn-secondary" href="<%= ctx %>/reports">Generate Reports</a>
</div>

<% if (!criticalStudents.isEmpty()) { %>
<div class="critical-banner">
    <h3>Critical attendance alert</h3>
    <p>These students are below 65% and need immediate follow-up.</p>
    <ul>
        <% for (Student student : criticalStudents) { %>
            <li>
                <strong><%= HtmlEscape.e(student.getRollNo()) %></strong>
                <%= HtmlEscape.e(student.getName()) %>
                — <%= AttendanceCalculator.formatPercentage(student.getPercentage()) %>
                (needs <%= student.getClassesNeededFor75() %> consecutive classes to reach 75%)
            </li>
        <% } %>
    </ul>
</div>
<% } else { %>
<div class="alert alert-ok">No students are currently in the critical range.</div>
<% } %>

<div class="card">
    <h2>All students</h2>
    <p class="hint">Percentage and status are calculated automatically from total classes and classes attended.</p>
    <%@ include file="student-table.jspf" %>
</div>

<%@ include file="layout-footer.jsp" %>
