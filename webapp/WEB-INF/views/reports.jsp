<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="attendance.util.HtmlEscape" %>
<%
    String type = (String) request.getAttribute("type");
    if (type == null) {
        type = "daily";
    }
    String date = (String) request.getAttribute("date");
    String from = (String) request.getAttribute("from");
    String to = (String) request.getAttribute("to");
    String subject = (String) request.getAttribute("subject");
    String roll = (String) request.getAttribute("roll");
    String error = (String) request.getAttribute("error");
    String summary = (String) request.getAttribute("summary");
    @SuppressWarnings("unchecked")
    List<String> subjects = (List<String>) request.getAttribute("subjects");
    @SuppressWarnings("unchecked")
    String[] headers = (String[]) request.getAttribute("headers");
    @SuppressWarnings("unchecked")
    List<String[]> rows = (List<String[]>) request.getAttribute("rows");
    if (rows == null) {
        rows = java.util.Collections.emptyList();
    }
    String ctx = request.getContextPath();
%>
<%@ include file="layout-header.jsp" %>

<div class="card">
    <h2>Generate report</h2>
    <% if (error != null) { %>
        <div class="alert alert-error"><%= HtmlEscape.e(error) %></div>
    <% } %>
    <form class="toolbar" method="get" action="<%= ctx %>/reports">
        <div class="inline-field">
            <label for="type">Report type</label>
            <select id="type" name="type" onchange="toggleReportFields()">
                <option value="daily" <%= "daily".equals(type) ? "selected" : "" %>>Daily</option>
                <option value="weekly" <%= "weekly".equals(type) ? "selected" : "" %>>Weekly</option>
                <option value="monthly" <%= "monthly".equals(type) ? "selected" : "" %>>Monthly</option>
                <option value="subject" <%= "subject".equals(type) ? "selected" : "" %>>Subject-wise</option>
                <option value="student" <%= "student".equals(type) ? "selected" : "" %>>Student-wise</option>
            </select>
        </div>
        <div class="inline-field report-daily">
            <label for="date">Date</label>
            <input id="date" name="date" type="date" value="<%= HtmlEscape.e(date != null ? date : "") %>">
        </div>
        <div class="inline-field report-range">
            <label for="from">From</label>
            <input id="from" name="from" type="date" value="<%= HtmlEscape.e(from != null ? from : "") %>">
        </div>
        <div class="inline-field report-range">
            <label for="to">To</label>
            <input id="to" name="to" type="date" value="<%= HtmlEscape.e(to != null ? to : "") %>">
        </div>
        <div class="inline-field report-subject">
            <label for="subject">Subject</label>
            <select id="subject" name="subject">
                <% if (subjects != null) { for (String s : subjects) { %>
                    <option value="<%= HtmlEscape.e(s) %>" <%= s.equals(subject) ? "selected" : "" %>><%= HtmlEscape.e(s) %></option>
                <% } } %>
            </select>
        </div>
        <div class="inline-field report-student">
            <label for="roll">Roll Number</label>
            <input id="roll" name="roll" type="text" value="<%= HtmlEscape.e(roll != null ? roll : "") %>" placeholder="e.g. 101">
        </div>
        <button class="btn btn-secondary" type="submit" style="align-self:flex-end;">Generate</button>
    </form>
</div>

<div class="card report-output">
    <h2>Report output</h2>
    <% if (summary != null) { %>
        <p class="hint"><%= HtmlEscape.e(summary) %></p>
    <% } %>
    <% if (headers != null) { %>
        <div class="export-row no-print">
            <a class="btn btn-ghost" href="<%= ctx %>/reports?type=<%= type %>&date=<%= HtmlEscape.e(date != null ? date : "") %>&from=<%= HtmlEscape.e(from != null ? from : "") %>&to=<%= HtmlEscape.e(to != null ? to : "") %>&subject=<%= java.net.URLEncoder.encode(subject != null ? subject : "", "UTF-8") %>&roll=<%= HtmlEscape.e(roll != null ? roll : "") %>&export=csv">Export CSV</a>
            <button class="btn btn-ghost" type="button" onclick="window.print()">Print / Save as PDF</button>
        </div>
        <div class="table-wrap">
        <table>
            <thead>
                <tr>
                    <% for (String h : headers) { %><th><%= HtmlEscape.e(h) %></th><% } %>
                </tr>
            </thead>
            <tbody>
                <% if (rows.isEmpty()) { %>
                    <tr><td colspan="<%= headers.length %>">No attendance data for this selection.</td></tr>
                <% } else {
                       for (String[] row : rows) { %>
                    <tr>
                        <% for (String cell : row) { %><td><%= HtmlEscape.e(cell) %></td><% } %>
                    </tr>
                <% } } %>
            </tbody>
        </table>
        </div>
    <% } else { %>
        <p class="hint">Choose a report type and press Generate.</p>
    <% } %>
</div>

<%@ include file="layout-footer.jsp" %>
