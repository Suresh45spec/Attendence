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
    int total = student != null ? student.getTotalClasses() : 0;
    int attended = student != null ? student.getClassesAttended() : 0;
    String status = total > 0 ? student.getStatus() : "No classes yet";
    String ctx = request.getContextPath();
%>
<%@ include file="layout-header.jsp" %>

<div class="stats">
    <div class="stat-card">
        Total Classes Conducted
        <strong><%= total %></strong>
    </div>
    <div class="stat-card">
        Classes Attended
        <strong><%= attended %></strong>
    </div>
    <div class="stat-card">
        Classes Missed
        <strong><%= total - attended %></strong>
    </div>
    <div class="stat-card">
        Attendance Percentage
        <strong><%= attendance.util.AttendanceCalculator.formatPercentage(student != null ? student.getPercentage() : 0) %></strong>
    </div>
    <div class="stat-card">
        Status
        <strong><%= HtmlEscape.e(status) %></strong>
    </div>
</div>

<% if (student != null && student.getClassesNeededFor75() > 0) { %>
    <div class="alert alert-warn">
        Attend your next <strong><%= student.getClassesNeededFor75() %></strong> consecutive classes
        to reach 75% attendance.
    </div>
<% } %>

<div class="card">
    <h2>Subject-wise attendance</h2>
    <% if (subjectWise.isEmpty()) { %>
        <p class="hint">No attendance has been recorded for you yet.</p>
    <% } else { %>
        <div class="table-wrap">
        <table>
            <thead>
                <tr>
                    <th>Subject</th>
                    <th>Conducted</th>
                    <th>Attended</th>
                    <th>Missed</th>
                    <th>Percentage</th>
                </tr>
            </thead>
            <tbody>
                <% for (Map.Entry<String, int[]> entry : subjectWise.entrySet()) {
                       int c = entry.getValue()[0];
                       int a = entry.getValue()[1];
                %>
                <tr>
                    <td><%= HtmlEscape.e(entry.getKey()) %></td>
                    <td><%= c %></td>
                    <td><%= a %></td>
                    <td><%= c - a %></td>
                    <td><%= String.format("%.2f%%", c > 0 ? (a * 100.0) / c : 0.0) %></td>
                </tr>
                <% } %>
            </tbody>
        </table>
        </div>
    <% } %>
    <p class="hint"><a href="<%= ctx %>/history">View full attendance history</a></p>
</div>

<%@ include file="layout-footer.jsp" %>
