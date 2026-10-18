# Web app setup – Student Attendance Management System

The original **Java Swing desktop app is still in this folder**. The web version is added beside it. It uses **HTML / CSS / JavaScript**, **JSP + Servlets**, **JDBC**, **MySQL**, and **Apache Tomcat 9**.

## Software to install

| Software | Why | This PC |
|----------|-----|---------|
| **JDK 8 or newer** (JDK 11 / 17 / 21 are fine) | Compile Java and run Tomcat | Already installed (OpenJDK 21) |
| **MySQL Server** | Store teachers and students | Already installed (MySQL 8 / Server 26.7, service `MySQL267`) |
| **Apache Tomcat 9** | Runs JSP and Servlets (`javax.servlet`) | `start-web.bat` can download Tomcat 9.0.108 into `tools\apache-tomcat` |
| **Google Chrome** | Use the app at `http://localhost:8080/attendance/` | Install if needed |
| **MySQL Workbench** (optional) | Run `sql/schema.sql` with a GUI | Optional |

Do **not** use Node.js, PHP, or Spring Boot for this project.

Tomcat **10+** uses `jakarta.servlet` and will **not** run this code. Use **Tomcat 9**.

## Project structure

```
Attendence/                          (your existing folder – nothing deleted)
  src/                               original Swing app (kept)
  data/                              original text-file storage (kept)
  compile.bat / run.bat              original desktop launchers (kept)

  sql/schema.sql                     MySQL database script
  web-src/attendance/                Java backend for the web app
    model/Teacher.java
    dao/                             JDBC (PreparedStatement)
    servlet/                         Login, Logout, Dashboard, Students, Defaulters
    filter/AuthFilter.java           session login check
    listener/AppContextListener.java loads db.properties
    util/InputValidator.java
  src/attendance/model/Student.java  reused by the web app
  src/attendance/util/AttendanceCalculator.java   reused percentage / status / classes-to-75%

  webapp/                            exploded WAR (HTML, CSS, JS, JSP)
    index.jsp                        teacher login
    css/style.css
    js/app.js
    WEB-INF/web.xml
    WEB-INF/db.properties            JDBC URL, user, password
    WEB-INF/lib/                     mysql-connector-j-8.4.0.jar
    WEB-INF/views/                   JSP pages after login

  compile-web.bat                    compile servlets
  start-web.bat                      compile, deploy, start Tomcat
  stop-web.bat                       stop Tomcat
```

## Dependencies

- **servlet-api.jar** – provided by Tomcat (`CATALINA_HOME\lib`). Used only at compile time.
- **mysql-connector-j-8.4.0.jar** – JDBC driver in `webapp\WEB-INF\lib`. `compile-web.bat` downloads it if missing.

No Maven/Gradle is required.

## 1. Create the database

1. Start the MySQL service if it is stopped.
2. Set your MySQL root password in `webapp\WEB-INF\db.properties`:

```
jdbc.url=jdbc:mysql://localhost:3306/attendance_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
jdbc.user=root
jdbc.password=YOUR_MYSQL_PASSWORD
```

3. Run the schema (Command Prompt):

```bat
"C:\Program Files\MySQL\MySQL Server 26.7\bin\mysql.exe" -u root -p < sql\schema.sql
```

Or open `sql/schema.sql` in MySQL Workbench and execute it.

This creates `attendance_db` with tables `teachers`, `students` and `attendance_records`, a demo teacher, and five sample students (each student can also log in).

## 2. Start the web app

From this folder:

```bat
start-web.bat
```

The first run may download Tomcat and the JDBC driver.

## 3. Open in Chrome

http://localhost:8080/attendance/

Demo login:

- Username: `teacher`
- Password: `teacher123`

Logout is in the left menu.

## What each page does

**Login / account pages**

- **Teacher Login** (`index.jsp`) – username + password, Remember Me (cookie), Forgot Password, links to Student Login and Register.
- **Student Login** (`/student-login`) – roll number + password, Remember Me, Forgot Password.
- **Register** (`/register`) – one common page for both roles (Full Name, Email, Username/Roll, Password, Confirm, role radio buttons). Duplicate usernames, roll numbers and emails are rejected.
- **Forgot Password** (`/forgot-password`) – verify role + username/roll + email on file, then set a new password.
- Passwords are stored as **salted SHA-256 hashes** (`sha256$salt$hash`), never plain text.

**Teacher pages**

- **Dashboard** – cards: Total Students, Present Today, Attendance Below 75%, Critical Below 65%; quick actions; critical alert banner.
- **Add Student** – roll number, name, division, email, initial attendance counts. Duplicate roll numbers are rejected.
- **Update Student** – change name, division, email and attendance counts (roll number stays the same).
- **View Students** – search by roll/name, filter by status (Eligible / Defaulter / Critical), sort by any column.
- **Mark Attendance** (`/attendance`) – pick date, subject and division; mark each student Present/Absent; saving the same session again corrects earlier entries; duplicate entries are impossible (UNIQUE key in MySQL).
- **Defaulter List** – everyone below 75% with classes-to-75% column, filters, sorting, **CSV export** and Print/Save-as-PDF.
- **Reports** (`/reports`) – daily, weekly, monthly, subject-wise and student-wise reports with **CSV export** and Print/Save-as-PDF.
- **Profile** – view/update name and email, change password.

**Student pages**

- **Dashboard** – cards: Total Classes Conducted, Attended, Missed, Current %, Status (Safe / Defaulter / Critical) plus subject-wise breakdown and classes needed to reach 75%.
- **My Attendance** – full summary and subject-wise table.
- **Attendance History** (`/history`) – every marked class with subject and date filters.
- **My Profile** – view/update details, change password.

**Access control** – the `AuthFilter` blocks students from teacher pages (students, attendance, defaulters, reports) and teachers from student-only pages (my-attendance, history). Students only ever see their own records.

Status rules (same as the Swing app):

- **Eligible**: 75% or above  
- **Defaulter**: 65% up to below 75%  
- **Critical**: below 65%  

Classes needed for 75%: `x = 3 * total - 4 * attended` (0 if already eligible).

## Common problems

| Problem | What to check |
|---------|----------------|
| Login page never loads | Tomcat window still open? Port 8080 free? URL includes `/attendance/` |
| “Could not connect to MySQL” | Service `MySQL267` running? Password in `db.properties`? Schema executed? |
| Compile error about servlet-api | Run `start-web.bat` so Tomcat exists, or set `CATALINA_HOME` to Tomcat 9 |
| 404 on localhost:8080 | You must use `http://localhost:8080/attendance/` |
| Tomcat 10 error `jakarta.servlet` | Uninstall / ignore Tomcat 10; this project needs Tomcat 9 |

## Desktop app

The Swing program is unchanged:

```bat
compile.bat
run.bat
```
