<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="attendance.model.Student" %>
<%@ page import="attendance.util.HtmlEscape" %>
<%
    String mode = (String) request.getAttribute("mode");
    boolean edit = "edit".equals(mode);
    Student student = (Student) request.getAttribute("student");
    String error = (String) request.getAttribute("error");
    String roll = student != null ? student.getRollNo() : "";
    String name = student != null ? student.getName() : "";
    String division = student != null ? student.getDivision() : "";
    String email = student != null ? student.getEmail() : "";
    String total = student != null ? String.valueOf(student.getTotalClasses()) : "";
    String attended = student != null ? String.valueOf(student.getClassesAttended()) : "";
%>
<%@ include file="layout-header.jsp" %>

<div class="card">
    <h2><%= edit ? "Update student" : "Add student" %></h2>
    <% if (error != null) { %>
        <div class="alert alert-error"><%= HtmlEscape.e(error) %></div>
    <% } %>
    <div id="jsError" class="alert alert-error hidden"></div>
    <form method="post" action="<%= request.getContextPath() %>/students" onsubmit="return validateStudentForm();">
        <input type="hidden" name="action" value="<%= edit ? "update" : "add" %>">
        <div class="form-grid">
            <div>
                <label for="rollNo">Roll number</label>
                <input id="rollNo" name="rollNo" type="text" value="<%= HtmlEscape.e(roll) %>" <%= edit ? "readonly" : "" %>>
            </div>
            <div>
                <label for="name">Name</label>
                <input id="name" name="name" type="text" value="<%= HtmlEscape.e(name) %>">
            </div>
            <div>
                <label for="division">Division</label>
                <input id="division" name="division" type="text" value="<%= HtmlEscape.e(division) %>" placeholder="A, B, C...">
            </div>
            <div>
                <label for="email">Email (optional)</label>
                <input id="email" name="email" type="email" value="<%= HtmlEscape.e(email) %>">
            </div>
            <div>
                <label for="totalClasses">Total classes</label>
                <input id="totalClasses" name="totalClasses" type="number" min="0" step="1" value="<%= HtmlEscape.e(total) %>">
            </div>
            <div class="full">
                <label for="classesAttended">Classes attended</label>
                <input id="classesAttended" name="classesAttended" type="number" min="0" step="1" value="<%= HtmlEscape.e(attended) %>">
            </div>
        </div>
        <p class="hint">Attendance percentage and Eligible / Defaulter / Critical status are calculated automatically after save.</p>
        <button class="btn btn-primary" type="submit" style="width:auto;"><%= edit ? "Save changes" : "Add student" %></button>
        <a class="btn btn-ghost" href="<%= request.getContextPath() %>/students">Cancel</a>
    </form>
</div>

<%@ include file="layout-footer.jsp" %>
