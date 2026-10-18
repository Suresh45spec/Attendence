package attendance.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import attendance.dao.AttendanceDAO;
import attendance.dao.StudentDAO;
import attendance.model.AttendanceRecord;
import attendance.model.Student;
import attendance.util.AttendanceCalculator;
import attendance.util.Subjects;

/**
 * Daily / weekly / monthly / subject-wise / student-wise reports
 * with CSV export (PDF via the browser print dialog).
 */
public class ReportServlet extends HttpServlet {

    private final AttendanceDAO attendanceDAO = new AttendanceDAO();
    private final StudentDAO studentDAO = new StudentDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String type = value(request.getParameter("type"));
        if (type.isEmpty()) {
            type = "daily";
        }
        try {
            LocalDate today = LocalDate.now();
            String date = value(request.getParameter("date"));
            String from = value(request.getParameter("from"));
            String to = value(request.getParameter("to"));
            String subject = value(request.getParameter("subject"));
            String roll = value(request.getParameter("roll"));

            switch (type) {
                case "weekly":
                    if (from.isEmpty() || to.isEmpty()) {
                        LocalDate weekStart = today.with(DayOfWeek.MONDAY);
                        from = weekStart.toString();
                        to = weekStart.plusDays(6).toString();
                    }
                    break;
                case "monthly":
                    if (from.isEmpty()) {
                        from = today.withDayOfMonth(1).toString();
                    }
                    if (to.isEmpty()) {
                        to = today.withDayOfMonth(today.lengthOfMonth()).toString();
                    }
                    break;
                case "subject":
                    if (from.isEmpty() || to.isEmpty()) {
                        from = today.withDayOfMonth(1).toString();
                        to = today.withDayOfMonth(today.lengthOfMonth()).toString();
                    }
                    if (subject.isEmpty() || !Subjects.isValid(subject)) {
                        subject = Subjects.ALL.get(0);
                    }
                    break;
                default:
                    if (date.isEmpty()) {
                        date = today.toString();
                    }
                    break;
            }

            List<String[]> rows;
            String[] headers;
            String summary;

            if ("daily".equals(type)) {
                List<AttendanceRecord> records = attendanceDAO.findAll("", date, date);
                headers = new String[] { "Date", "Roll No", "Name", "Subject", "Status" };
                rows = new ArrayList<String[]>();
                for (AttendanceRecord record : records) {
                    rows.add(new String[] {
                            record.getClassDate(),
                            record.getRollNo(),
                            record.getStudentName(),
                            record.getSubject(),
                            record.isPresent() ? "Present" : "Absent" });
                }
                summary = rows.size() + " attendance entries on " + date;
            } else if ("student".equals(type)) {
                if (roll.isEmpty()) {
                    request.setAttribute("error", "Enter a roll number to generate a student-wise report.");
                }
                Student student = studentDAO.findByRollNo(roll);
                if (student == null && !roll.isEmpty()) {
                    request.setAttribute("error", "No student found with roll number " + roll + ".");
                }
                headers = new String[] { "Subject", "Conducted", "Attended", "Missed", "Percentage", "Status" };
                rows = new ArrayList<String[]>();
                Map<String, int[]> subjectWise = roll.isEmpty() || student == null
                        ? new LinkedHashMap<String, int[]>()
                        : attendanceDAO.subjectWiseForRoll(roll);
                int conducted = 0;
                int attended = 0;
                for (Map.Entry<String, int[]> entry : subjectWise.entrySet()) {
                    int c = entry.getValue()[0];
                    int a = entry.getValue()[1];
                    conducted += c;
                    attended += a;
                    double pct = AttendanceCalculator.calculatePercentage(a, c);
                    rows.add(new String[] {
                            entry.getKey(), String.valueOf(c), String.valueOf(a),
                            String.valueOf(c - a),
                            AttendanceCalculator.formatPercentage(pct),
                            AttendanceCalculator.classifyStatus(pct) });
                }
                summary = student == null
                        ? "No data"
                        : student.getName() + " (" + roll + ") overall: " + attended + "/" + conducted
                                + " = " + AttendanceCalculator.formatPercentage(
                                        AttendanceCalculator.calculatePercentage(attended, conducted));
            } else {
                // weekly, monthly, subject-wise: per-student totals for the range
                String subjectFilter = "subject".equals(type) ? subject : "";
                List<AttendanceRecord> records = attendanceDAO.findAll(subjectFilter, from, to);
                Map<String, int[]> totals = new LinkedHashMap<String, int[]>();
                Map<String, String> names = new LinkedHashMap<String, String>();
                for (AttendanceRecord record : records) {
                    int[] pair = totals.get(record.getRollNo());
                    if (pair == null) {
                        pair = new int[2];
                        totals.put(record.getRollNo(), pair);
                    }
                    pair[0]++;
                    if (record.isPresent()) {
                        pair[1]++;
                    }
                    names.put(record.getRollNo(), record.getStudentName());
                }
                headers = new String[] { "Roll No", "Name", "Conducted", "Attended", "Missed", "Percentage", "Status" };
                rows = new ArrayList<String[]>();
                for (Map.Entry<String, int[]> entry : totals.entrySet()) {
                    int c = entry.getValue()[0];
                    int a = entry.getValue()[1];
                    double pct = AttendanceCalculator.calculatePercentage(a, c);
                    rows.add(new String[] {
                            entry.getKey(),
                            names.get(entry.getKey()),
                            String.valueOf(c), String.valueOf(a), String.valueOf(c - a),
                            AttendanceCalculator.formatPercentage(pct),
                            AttendanceCalculator.classifyStatus(pct) });
                }
                summary = rows.size() + " students, " + from + " to " + to
                        + ("subject".equals(type) ? " (" + subject + ")" : "");
            }

            if ("csv".equals(value(request.getParameter("export")))) {
                writeCsv(response, headers, rows, "attendance-" + type + ".csv");
                return;
            }

            request.setAttribute("type", type);
            request.setAttribute("date", date);
            request.setAttribute("from", from);
            request.setAttribute("to", to);
            request.setAttribute("subject", subject);
            request.setAttribute("roll", roll);
            request.setAttribute("subjects", Subjects.ALL);
            request.setAttribute("headers", headers);
            request.setAttribute("rows", rows);
            request.setAttribute("summary", summary);
            request.setAttribute("activePage", "reports");
            request.setAttribute("pageTitle", "Reports");
            request.getRequestDispatcher("/WEB-INF/views/reports.jsp").forward(request, response);
        } catch (SQLException e) {
            request.setAttribute("errorMessage", "Could not generate the report from MySQL.");
            request.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(request, response);
        }
    }

    private void writeCsv(HttpServletResponse response, String[] headers, List<String[]> rows, String fileName)
            throws IOException {
        response.setContentType("text/csv; charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
        PrintWriter out = response.getWriter();
        out.println(csvLine(headers));
        for (String[] row : rows) {
            out.println(csvLine(row));
        }
    }

    private String csvLine(String[] cells) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < cells.length; i++) {
            if (i > 0) {
                sb.append(',');
            }
            String cell = cells[i] == null ? "" : cells[i];
            if (cell.contains(",") || cell.contains("\"") || cell.contains("\n")) {
                sb.append('"').append(cell.replace("\"", "\"\"")).append('"');
            } else {
                sb.append(cell);
            }
        }
        return sb.toString();
    }

    private String value(String text) {
        return text == null ? "" : text.trim();
    }
}
