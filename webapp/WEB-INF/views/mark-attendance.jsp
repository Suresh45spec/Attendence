<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="attendance.model.Student" %>
<%@ page import="attendance.model.AttendanceRecord" %>
<%@ page import="attendance.util.HtmlEscape" %>
<%
    @SuppressWarnings("unchecked")
    List<Student> roster = (List<Student>) request.getAttribute("roster");
    @SuppressWarnings("unchecked")
    List<String> divisions = (List<String>) request.getAttribute("divisions");
    @SuppressWarnings("unchecked")
    List<String> subjects = (List<String>) request.getAttribute("subjects");
    if (roster == null) {
        roster = java.util.Collections.emptyList();
    }
    if (divisions == null) {
        divisions = java.util.Collections.emptyList();
    }
    String selectedDate = (String) request.getAttribute("selectedDate");
    String selectedSubject = (String) request.getAttribute("selectedSubject");
    String selectedDivision = (String) request.getAttribute("selectedDivision");
    String error = (String) request.getAttribute("error");
    String savedCount = (String) request.getAttribute("savedCount");
    String ctx = request.getContextPath();
%>
<%@ include file="layout-header.jsp" %>

<% if (savedCount != null && !savedCount.isEmpty()) { %>
    <div class="alert alert-ok">
        Attendance saved! <%= HtmlEscape.e(savedCount) %> record(s) written
        (duplicates were skipped, corrections applied).
    </div>
<% } %>
<% if (error != null) { %>
    <div class="alert alert-error"><%= HtmlEscape.e(error) %></div>
<% } %>

<div class="card">
    <h2>Select class session</h2>
    <form class="toolbar" method="get" action="<%= ctx %>/attendance">
        <div class="inline-field">
            <label for="date">Date</label>
            <input id="date" name="date" type="date" value="<%= HtmlEscape.e(selectedDate) %>">
        </div>
        <div class="inline-field">
            <label for="subject">Subject</label>
            <select id="subject" name="subject">
                <% for (String subject : subjects) { %>
                    <option value="<%= HtmlEscape.e(subject) %>"
                        <%= subject.equals(selectedSubject) ? "selected" : "" %>><%= HtmlEscape.e(subject) %></option>
                <% } %>
            </select>
        </div>
        <div class="inline-field">
            <label for="division">Class / Division</label>
            <select id="division" name="division">
                <% for (String div : divisions) { %>
                    <option value="<%= HtmlEscape.e(div) %>"
                        <%= div.equals(selectedDivision) ? "selected" : "" %>>Division <%= HtmlEscape.e(div) %></option>
                <% } %>
            </select>
        </div>
        <button class="btn btn-secondary" type="submit" style="align-self:flex-end;">Load Students</button>
    </form>
</div>

<div class="card">
    <h2>Mark attendance &mdash; <%= HtmlEscape.e(selectedSubject) %></h2>
    <p class="hint">
        Date: <strong><%= HtmlEscape.e(selectedDate) %></strong> &middot;
        Division: <strong><%= HtmlEscape.e(selectedDivision) %></strong> &middot;
        Saving the same session again corrects earlier entries (no duplicates are created).
    </p>
    <% if (roster.isEmpty()) { %>
        <div class="alert alert-warn">No students found in this division. Add students first.</div>
    <% } else { %>
    <form method="post" action="<%= ctx %>/attendance" onsubmit="return validateAttendanceForm();">
        <input type="hidden" name="date" value="<%= HtmlEscape.e(selectedDate) %>">
        <input type="hidden" name="subject" value="<%= HtmlEscape.e(selectedSubject) %>">
        <input type="hidden" name="division" value="<%= HtmlEscape.e(selectedDivision) %>">
        <div id="jsError" class="alert alert-error hidden"></div>
        <div class="table-wrap">
        <table>
            <thead>
                <tr>
                    <th>Roll No</th>
                    <th>Name</th>
                    <th>Already marked</th>
                    <th>Mark as</th>
                </tr>
            </thead>
            <tbody>
                <% for (Student student : roster) {
                       String existing = (String) request.getAttribute("status_" + student.getRollNo());
                       String current = existing == null ? "" : existing;
                %>
                <tr>
                    <td><%= HtmlEscape.e(student.getRollNo()) %></td>
                    <td><%= HtmlEscape.e(student.getName()) %></td>
                    <td>
                        <% if (AttendanceRecord.PRESENT.equals(current)) { %>
                            <span class="badge badge-eligible">Present</span>
                        <% } else if (AttendanceRecord.ABSENT.equals(current)) { %>
                            <span class="badge badge-critical">Absent</span>
                        <% } else { %>
                            <span class="badge badge-defaulter">Not marked</span>
                        <% } %>
                    </td>
                    <td class="radio-cell">
                        <label class="check-label">
                            <input type="radio" name="st_<%= HtmlEscape.e(student.getRollNo()) %>"
                                   value="PRESENT" <%= AttendanceRecord.PRESENT.equals(current) ? "checked" : "" %>> Present
                        </label>
                        <label class="check-label">
                            <input type="radio" name="st_<%= HtmlEscape.e(student.getRollNo()) %>"
                                   value="ABSENT" <%= AttendanceRecord.ABSENT.equals(current) ? "checked" : "" %>> Absent
                        </label>
                    </td>
                </tr>
                <% } %>
            </tbody>
        </table>
        </div>
        <button class="btn btn-primary" type="submit" style="width:auto;">Save Attendance</button>
        <a class="btn btn-ghost" href="<%= ctx %>/dashboard">Cancel</a>
    </form>
    <% } %>
</div>

<%@ include file="layout-footer.jsp" %>
