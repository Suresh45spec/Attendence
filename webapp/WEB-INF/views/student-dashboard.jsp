<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.Map" %>
<%@ page import="attendance.model.Student" %>
<%@ page import="attendance.util.HtmlEscape" %>
<%
    Student student = (Student) session.getAttribute("student");
    @SuppressWarnings("unchecked")
    Map<String, int[]> subjectWise = (Map<String, int[]>) request.getAttribute("subjectWise");
    if (subjectWise == null) {
        subjectWise = java.util.Collections.emptyMap();
    }
    String status = (String) request.getAttribute("attendanceStatus");
    String statusClass = "Critical".equals(status) ? "critical"
            : ("Defaulter".equals(status) ? "warn" : "ok");
    String ctx = request.getContextPath();
%>
<%@ include file="layout-header.jsp" %>

<div class="alert alert-ok">
    Welcome back, <strong><%= student != null ? HtmlEscape.e(student.getName()) : "" %></strong>
    (Roll No: <strong><%= student != null ? HtmlEscape.e(student.getRollNo()) : "" %></strong>)
</div>

<div class="stats">
    <div class="stat-card">
        Total Classes Conducted
        <strong><%= request.getAttribute("totalClasses") %></strong>
    </div>
    <div class="stat-card">
        Classes Attended
        <strong><%= request.getAttribute("classesAttended") %></strong>
    </div>
    <div class="stat-card">
        Classes Missed
        <strong><%= request.getAttribute("classesMissed") %></strong>
    </div>
    <div class="stat-card">
        Current Attendance
        <strong><%= request.getAttribute("percent") %></strong>
    </div>
    <div class="stat-card stat-card-<%= statusClass %>">
        Attendance Status
        <strong><%= HtmlEscape.e(status) %></strong>
    </div>
</div>

<% int needed = request.getAttribute("classesNeeded") != null
        ? (Integer) request.getAttribute("classesNeeded") : 0; %>
<% if (needed > 0) { %>
    <div class="alert alert-warn">
        Attend your next <strong><%= needed %></strong> consecutive classes to reach 75% attendance.
    </div>
<% } else if ("Eligible".equals(status)) { %>
    <div class="alert alert-ok">Your attendance is safe (75% or above). Keep it up!</div>
<% } %>

<div class="card">
    <h2>Subject-wise attendance</h2>
    <% if (subjectWise.isEmpty()) { %>
        <p class="hint">No attendance recorded yet. Your teacher has not marked any classes for you.</p>
    <% } else { %>
        <div class="table-wrap">
        <table>
            <thead>
                <tr>
                    <th>Subject</th>
                    <th>Classes Conducted</th>
                    <th>Attended</th>
                    <th>Missed</th>
                    <th>Percentage</th>
                </tr>
            </thead>
            <tbody>
                <% for (Map.Entry<String, int[]> entry : subjectWise.entrySet()) {
                       int conducted = entry.getValue()[0];
                       int attended = entry.getValue()[1];
                       double pct = conducted > 0 ? (attended * 100.0) / conducted : 0.0;
                %>
                <tr>
                    <td><%= HtmlEscape.e(entry.getKey()) %></td>
                    <td><%= conducted %></td>
                    <td><%= attended %></td>
                    <td><%= conducted - attended %></td>
                    <td><%= String.format("%.2f%%", pct) %></td>
                </tr>
                <% } %>
            </tbody>
        </table>
        </div>
    <% } %>
    <p class="hint">
        <a href="<%= ctx %>/my-attendance">View my full attendance</a> &middot;
        <a href="<%= ctx %>/history">Attendance history</a>
    </p>
</div>

<%@ include file="layout-footer.jsp" %>
