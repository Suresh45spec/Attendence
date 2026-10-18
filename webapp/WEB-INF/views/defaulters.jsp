<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="attendance.model.Student" %>
<%@ page import="attendance.util.AttendanceCalculator" %>
<%@ page import="attendance.util.HtmlEscape" %>
<%
    String statusFilter = (String) request.getAttribute("statusFilter");
    String sortKey = (String) request.getAttribute("sortKey");
    String sortDir = (String) request.getAttribute("sortDir");
    if (statusFilter == null) statusFilter = "All";
    if (sortKey == null) sortKey = "percent";
    if (sortDir == null) sortDir = "asc";
    String ctx = request.getContextPath();
%>
<%@ include file="layout-header.jsp" %>

<div class="critical-banner">
    <h3>Critical students (&lt; 65%)</h3>
    <p>Rows highlighted in red are critical. Other students on this list are defaulters (65% to below 75%).
       The last column shows consecutive classes needed to reach 75%.</p>
</div>

<div class="card">
    <h2>Defaulter list (below 75%)</h2>

    <form class="toolbar" method="get" action="<%= ctx %>/defaulters">
        <div class="inline-field">
            <label for="status">Show</label>
            <select id="status" name="status">
                <option value="All" <%= "All".equals(statusFilter) ? "selected" : "" %>>All defaulters</option>
                <option value="Defaulter" <%= "Defaulter".equals(statusFilter) ? "selected" : "" %>>Defaulter only (65–74.99%)</option>
                <option value="Critical" <%= "Critical".equals(statusFilter) ? "selected" : "" %>>Critical only (&lt; 65%)</option>
            </select>
        </div>
        <div class="inline-field">
            <label for="sort">Sort by</label>
            <select id="sort" name="sort">
                <option value="percent" <%= "percent".equals(sortKey) ? "selected" : "" %>>Attendance %</option>
                <option value="name" <%= "name".equals(sortKey) ? "selected" : "" %>>Name</option>
                <option value="roll" <%= "roll".equals(sortKey) ? "selected" : "" %>>Roll number</option>
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
        <a class="btn btn-ghost" href="<%= ctx %>/defaulters?status=<%= HtmlEscape.e(statusFilter) %>&sort=<%= HtmlEscape.e(sortKey) %>&dir=<%= HtmlEscape.e(sortDir) %>&export=csv" style="align-self:flex-end;">Export CSV</a>
        <button class="btn btn-ghost" type="button" onclick="window.print()" style="align-self:flex-end;">Print / Save as PDF</button>
    </form>

    <%@ include file="student-table.jspf" %>
</div>

<%@ include file="layout-footer.jsp" %>
