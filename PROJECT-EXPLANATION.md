# 📚 PROJECT EXPLANATION — In Simple Language

**Project Name:** Defaulter List Generator – Student Attendance Management System
**Type:** Java Mini Project (Web Application)

---

## 1. What is this project? (30-second answer)

> "This is a **Student Attendance Management System**. Teachers log in and mark which students
> attended each class. The system **automatically calculates attendance percentages**, finds
> students whose attendance is **below 75% (defaulters)** and **below 65% (critical)**, and
> generates the **defaulter list** and reports. Students get their own login where they can see
> their own attendance and how many more classes they must attend to reach 75%.
> All data is stored safely in a **MySQL database**."

---

## 2. Why do we need this project?

In most colleges, attendance is noted on paper. Counting percentages by hand is:

- ❌ Slow and boring
- ❌ Full of calculation mistakes
- ❌ Hard to find who the defaulters are
- ❌ Hard to give each student their own report

This project fixes all of that with **one click**.

### Main Objectives
1. Store student records permanently in a database
2. Mark daily attendance (Present / Absent)
3. Calculate attendance percentage automatically
4. Find defaulters (below 75%) and critical students (below 65%)
5. Tell each student: *"attend X more classes to reach 75%"*
6. Generate reports and export them as CSV / PDF

---

## 3. Which technologies did we use, and why?

| Technology | What it does (simple words) |
|---|---|
| **Java** | The main programming language of the project |
| **JSP & Servlets** | Create the web pages and handle login, marking, reports etc. |
| **JDBC** | Connects Java to MySQL database |
| **MySQL** | Stores all data permanently (students, teachers, attendance) |
| **HTML, CSS, JavaScript** | The look of the pages, buttons, forms, and small validations |
| **Apache Tomcat 9** | The free server that runs our Java web application |
| **SHA-256 hashing** | Converts passwords into unreadable text so nobody can steal them |

> We did **not** use Spring Boot, Node.js or PHP — this is a pure Java (JSP/Servlet) project,
> which is what colleges expect in a Java mini project.

---

## 4. Who can use the system? (Two roles)

### 👨‍🏫 Teacher
- Adds students to the system
- Marks attendance for a class, subject and date
- Sees all students, searches and sorts them
- Sees the **Defaulter List** (everyone below 75%)
- Generates daily / weekly / monthly / subject-wise / student-wise reports
- Can update their own profile and password

### 🧑‍🎓 Student
- Logs in with **roll number and password**
- Sees **only their own** attendance (nobody else's)
- Sees: total classes, attended, missed, percentage, status
- Sees subject-wise attendance and full history
- Sees: *"attend the next X consecutive classes to reach 75%"*

---

## 5. How do the pages work? (Walkthrough)

### Login & Account pages
1. **Teacher Login** (`index.jsp`) → username + password. Has *Remember Me* and *Forgot Password*.
2. **Student Login** → roll number + password.
3. **Register** — ONE common page for both roles. Choose Teacher or Student with a radio button.
   It checks:
   - All fields are filled
   - Email looks like `name@example.com`
   - Both passwords match
   - Username / roll number / email are not already used by someone else
4. **Forgot Password** → prove your identity with username/roll + email, then set a new password.

### Teacher pages (after login)
5. **Dashboard** — 4 big cards:
   - Total Students
   - Present Today
   - Students below 75%
   - Critical students below 65% (red warning banner lists them)
6. **Add Student** — enter roll number, name, division, email. Same roll number cannot be added twice.
7. **Mark Attendance** — choose **Date + Subject + Division**, then the student list opens.
   Tick Present or Absent for each student and press Save.
   - Saving the same class again = **correction** (updates the old entry, never creates a double entry)
8. **View Students** — table of every student with percentage and status. Has search box,
   status filter (Eligible / Defaulter / Critical) and sorting.
9. **Defaulter List** — everyone below 75%, with a "Classes to 75%" column.
   Can be **filtered, sorted, exported as CSV, or printed (Save as PDF)**.
10. **Reports** — pick Daily, Weekly, Monthly, Subject-wise or Student-wise.
    Export as CSV or Print to PDF.
11. **Profile** — change your name, email and password.

### Student pages (after login)
12. **Dashboard** — 5 cards: Total Classes, Attended, Missed, Percentage, Status.
    Plus a subject-wise table and a message about how many classes are needed to reach 75%.
13. **My Attendance** — full summary + subject-wise percentages.
14. **Attendance History** — every class that was marked, with filters by subject and date range.
15. **My Profile** — update details, change password.

---

## 6. How is the data stored? (MySQL tables — simply explained)

1. **teachers** — one row per teacher: username, password (hashed), full name, email
2. **students** — one row per student: roll number, name, division, email, password,
   total classes, classes attended
3. **attendance_records** — one row for every marked class:
   *which student, which subject, which date, Present or Absent*
   - A **UNIQUE key** on (roll number + subject + date) makes it **impossible** to
     accidentally mark the same student twice for the same class. 👈 (Great viva point!)

---

## 7. The two formulas (explain them like this)

### Attendance percentage
```
percentage = (classes attended × 100) ÷ total classes
```
Example: attended 28 out of 40 → (28 × 100) / 40 = **70%** → Defaulter

### Status rules
| Percentage | Status |
|---|---|
| 75% or more | ✅ **Eligible** (Safe) |
| 65% to 74.99% | ⚠️ **Defaulter** |
| Below 65% | 🔴 **Critical** |

### How many more classes to reach 75%?
We need: `(attended + x) ÷ (total + x) ≥ 0.75`
Solving gives the easy formula:
```
x = (3 × total) − (4 × attended)
```
Example: attended 20 of 40 → x = (3×40) − (4×20) = 120 − 80 = **40 more classes**
If the student is already ≥ 75%, x = 0.

---

## 8. What security features are used? (Impress in viva)

1. **Password hashing (SHA-256 with random salt)** — passwords are never stored as plain text.
   Even if someone reads the database, they only see `sha256$....$....`
2. **Role-based access control** — a filter checks every page:
   - Students cannot open teacher pages (mark attendance, reports, defaulters)
   - Teachers cannot open student-only pages
   - Not logged in? → bounced back to the login page
3. **Students see only their own data** — the roll number comes from the session, not from the URL,
   so nobody can view another student's attendance
4. **Sessions** — after login, Tomcat keeps a session; Logout destroys it
5. **SQL injection protection** — all database queries use `PreparedStatement` (no string merging)
6. **HTML escaping** — user input is escaped so no one can inject scripts
7. **Remember Me** — stores only your *username* in a cookie (never the password)

---

## 9. OOP concepts used (they always ask this!)

| Concept | Where we used it |
|---|---|
| **Encapsulation** | `Student` and `Teacher` classes: private fields + getters/setters |
| **Separation of concerns** | Model / DAO / Servlet / JSP — each has one job |
| **Inheritance** | All servlets extend `HttpServlet` |
| **Polymorphism** | `doGet()` / `doPost()` overridden in every servlet |
| **ArrayList** | Storing students, records, defaulters in lists |
| **Exception handling** | try/catch around every database operation with friendly error pages |
| **Utility classes** | `AttendanceCalculator`, `PasswordHasher`, `InputValidator` — static methods, private constructor |

### Project structure (layers)
```
Model     → Student, Teacher, AttendanceRecord     (the data)
DAO       → StudentDAO, TeacherDAO, AttendanceDAO  (talks to MySQL)
Servlet   → Login, Dashboard, MarkAttendance...    (decides what happens)
JSP/HTML  → views folder                            (what the user sees)
Filter    → AuthFilter                              (the security guard)
```

---

## 10. How to run the project (steps)

1. Start MySQL service (`MySQL80`)
2. Run `sql\schema.sql` once to create the database
3. Put your MySQL password in `webapp\WEB-INF\db.properties`
4. Double-click **`start-web.bat`**
5. Open Chrome → **http://localhost:8080/attendance/**

### Demo logins
| Role | Username / Roll | Password |
|---|---|---|
| Teacher | `teacher` | `teacher123` |
| Student | `101` | `student123` |

---

## 11. Bonus: Viva Q&A (most asked questions)

**Q: What is the difference between Defaulter and Critical?**
A: Defaulter is 65% to below 75%. Critical is below 65% and needs urgent action.

**Q: How do you prevent marking the same student twice for one class?**
A: A UNIQUE database key on (roll, subject, date). If the same entry comes again, it is
either skipped (duplicate) or updates the old one (correction).

**Q: Where are passwords stored?**
A: As salted SHA-256 hashes in the database, never in plain text.

**Q: How does a student see only their own attendance?**
A: The logged-in roll number is stored in the HTTP session. The queries always use that
session value, so changing the URL cannot show someone else's data.

**Q: Which design pattern do Servlets follow?**
A: Model-View-Controller (MVC) — Model = Student/DAO, View = JSP, Controller = Servlets.

**Q: What if two teachers mark attendance at the same time?**
A: The marking runs inside a database transaction — either all rows save or none,
and the UNIQUE key still prevents duplicates.

**Q: Can reports be taken out of the system?**
A: Yes — CSV download for Excel, and Print → Save as PDF in the browser.

---

*Team: Sankirtana Arkala (02), Sarvesh Chaudhari (08), Shruti Devapache (13)*
