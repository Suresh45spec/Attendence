<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isErrorPage="true" %>
<%@ page import="attendance.util.HtmlEscape" %>
<%
    String message = (String) request.getAttribute("errorMessage");
    if (message == null && exception != null) {
        message = exception.getMessage();
    }
    if (message == null) {
        message = "Something went wrong. Please try again.";
    }
    request.setAttribute("pageTitle", "Error");
    request.setAttribute("activePage", "");
%>
<%@ include file="layout-header.jsp" %>
<div class="card">
    <h2>Unable to complete that request</h2>
    <div class="alert alert-error"><%= HtmlEscape.e(message) %></div>
    <p class="hint">Typical causes: MySQL is not running, the database was not created, or WEB-INF/db.properties has the wrong password.</p>
    <a class="btn btn-secondary" href="<%= request.getContextPath() %>/dashboard">Back to dashboard</a>
</div>
<%@ include file="layout-footer.jsp" %>
