<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="attendance.model.Student" %>
<%@ page import="attendance.util.AttendanceCalculator" %>
<%@ page import="attendance.util.HtmlEscape" %>
<%
    String query = (String) request.getAttribute("query");
    if (query == null) {
        query = "";
    }
    String success = (String) request.getAttribute("success");
    String error = (String) request.getAttribute("error");
    String statusFilter = (String) request.getAttribute("statusFilter");
    String sortKey = (String) request.getAttribute("sortKey");
    String sortDir = (String) request.getAttribute("sortDir");
    if (statusFilter == null) statusFilter = "All";
    if (sortKey == null) sortKey = "roll";
    if (sortDir == null) sortDir = "asc";
    String ctx = request.getContextPath();
%>
<%@ include file="layout-header.jsp" %>

<div class="card">
    <h2>View and search students</h2>
    <% if (success != null) { %>
        <div class="alert alert-ok"><%= HtmlEscape.e(success) %></div>
    <% } %>
    <% if (error != null) { %>
        <div class="alert alert-error"><%= HtmlEscape.e(error) %></div>
    <% } %>
    <form class="toolbar" method="get" action="<%= ctx %>/students">
        <input id="liveSearch" type="text" name="q" value="<%= HtmlEscape.e(query) %>"
               placeholder="Search by roll number or name" oninput="filterTable()">
        <button class="btn btn-secondary" type="submit">Search</button>

        <div class="inline-field">
            <label for="status">Status filter</label>
            <select id="status" name="status">
                <option value="All" <%= "All".equals(statusFilter) ? "selected" : "" %>>All</option>
                <option value="Eligible" <%= "Eligible".equals(statusFilter) ? "selected" : "" %>>Eligible (&ge; 75%)</option>
                <option value="Defaulter" <%= "Defaulter".equals(statusFilter) ? "selected" : "" %>>Defaulter (65–74.99%)</option>
                <option value="Critical" <%= "Critical".equals(statusFilter) ? "selected" : "" %>>Critical (&lt; 65%)</option>
            </select>
        </div>
        <div class="inline-field">
            <label for="sort">Sort by</label>
            <select id="sort" name="sort">
                <option value="roll" <%= "roll".equals(sortKey) ? "selected" : "" %>>Roll number</option>
                <option value="name" <%= "name".equals(sortKey) ? "selected" : "" %>>Name</option>
                <option value="percent" <%= "percent".equals(sortKey) ? "selected" : "" %>>Attendance %</option>
                <option value="total" <%= "total".equals(sortKey) ? "selected" : "" %>>Total classes</option>
                <option value="attended" <%= "attended".equals(sortKey) ? "selected" : "" %>>Attended</option>
            </select>
        </div>
        <div class="inline-field">
            <label for="dir">Direction</label>
            <select id="dir" name="dir">
                <option value="asc" <%= "asc".equals(sortDir) ? "selected" : "" %>>Ascending</option>
                <option value="desc" <%= "desc".equals(sortDir) ? "selected" : "" %>>Descending</option>
            </select>
        </div>
        <button class="btn btn-secondary" type="submit" style="align-self:flex-end;">Apply</button>
        <a class="btn btn-ghost" href="<%= ctx %>/students" style="align-self:flex-end;">Clear</a>
        <a class="btn btn-primary" style="width:auto;margin-top:0;align-self:flex-end;" href="<%= ctx %>/students?action=new">Add student</a>
    </form>
    <p class="hint">Server search and filters use MySQL. Typing also filters the table live on this page.</p>
    <%@ include file="student-table.jspf" %>
</div>

<%@ include file="layout-footer.jsp" %>
