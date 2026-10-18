<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="attendance.model.AttendanceRecord" %>
<%@ page import="attendance.util.HtmlEscape" %>
<%
    @SuppressWarnings("unchecked")
    List<AttendanceRecord> records = (List<AttendanceRecord>) request.getAttribute("records");
    if (records == null) {
        records = java.util.Collections.emptyList();
    }
    @SuppressWarnings("unchecked")
    List<String> subjects = (List<String>) request.getAttribute("subjects");
    String filterSubject = (String) request.getAttribute("filterSubject");
    String filterFrom = (String) request.getAttribute("filterFrom");
    String filterTo = (String) request.getAttribute("filterTo");
    if (filterSubject == null) filterSubject = "";
    if (filterFrom == null) filterFrom = "";
    if (filterTo == null) filterTo = "";
    String ctx = request.getContextPath();
%>
<%@ include file="layout-header.jsp" %>

<div class="card">
    <h2>Filter history</h2>
    <form class="toolbar" method="get" action="<%= ctx %>/history">
        <div class="inline-field">
            <label for="subject">Subject</label>
            <select id="subject" name="subject">
                <option value="">All subjects</option>
                <% if (subjects != null) { for (String s : subjects) { %>
                    <option value="<%= HtmlEscape.e(s) %>" <%= s.equals(filterSubject) ? "selected" : "" %>><%= HtmlEscape.e(s) %></option>
                <% } } %>
            </select>
        </div>
        <div class="inline-field">
            <label for="from">From</label>
            <input id="from" name="from" type="date" value="<%= HtmlEscape.e(filterFrom) %>">
        </div>
        <div class="inline-field">
            <label for="to">To</label>
            <input id="to" name="to" type="date" value="<%= HtmlEscape.e(filterTo) %>">
        </div>
        <button class="btn btn-secondary" type="submit" style="align-self:flex-end;">Apply</button>
        <a class="btn btn-ghost" href="<%= ctx %>/history" style="align-self:flex-end;">Clear</a>
    </form>
</div>

<div class="card">
    <h2>Attendance history</h2>
    <p class="hint"><%= records.size() %> record(s) found. Export via Reports if you need a copy.</p>
    <div class="table-wrap">
    <table>
        <thead>
            <tr>
                <th>Date</th>
                <th>Subject</th>
                <th>Status</th>
            </tr>
        </thead>
        <tbody>
            <% if (records.isEmpty()) { %>
                <tr><td colspan="3">No attendance records match this filter.</td></tr>
            <% } else {
                   for (AttendanceRecord record : records) { %>
                <tr>
                    <td><%= HtmlEscape.e(record.getClassDate()) %></td>
                    <td><%= HtmlEscape.e(record.getSubject()) %></td>
                    <td>
                        <% if (record.isPresent()) { %>
                            <span class="badge badge-eligible">Present</span>
                        <% } else { %>
                            <span class="badge badge-critical">Absent</span>
                        <% } %>
                    </td>
                </tr>
            <% } } %>
        </tbody>
    </table>
    </div>
</div>

<%@ include file="layout-footer.jsp" %>
