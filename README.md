# Defaulter List Generator – Student Attendance Management System

College mini project. Two versions live in this folder:

- **Desktop (original):** Core Java + Swing, text-file storage. Use `compile.bat` then `run.bat`.
- **Web (full-featured):** HTML / CSS / JavaScript + JSP / Servlets + JDBC + MySQL on Apache Tomcat 9. See **[WEB-SETUP.md](WEB-SETUP.md)** for software, schema, and `http://localhost:8080/attendance/`.

## How to run the web app on Windows

1. Start the MySQL service (`MySQL80`).
2. Run the schema once: `mysql -u root -p < sql\schema.sql`
3. Put your MySQL password in `webapp\WEB-INF\db.properties`.
4. Run `start-web.bat` and open **http://localhost:8080/attendance/**.

## How to run the desktop app

```bat
compile.bat
run.bat
```

## Demo login

| Role | Username / Roll | Password |
|------|-----------------|----------|
| Teacher | `teacher` | `teacher123` |
| Student | `101` | `student123` |

You can also create a new account from **Register** (one common page for both roles).

## What is excluded from git (.gitignore)

- `webapp/WEB-INF/db.properties` — contains your MySQL root password; a `db.properties.example` placeholder is committed instead
- `tools/` (downloaded Tomcat), `bin/` + `*.class` (compiled output), `lib/` (extracted connector source), and `data/*.txt` (user records)

Anyone cloning it just needs to copy `db.properties.example` → `db.properties`, set their password, run the schema, and run `start-web.bat`.

## Project structure

```
Attendence/
  compile.bat
  run.bat
  src/attendance/
    Main.java
    model/          Student, User
    util/           AttendanceCalculator, UITheme
    service/        FileStorage, AuthService, AttendanceService
    gui/            Welcome, Login, Register, Dashboard
    gui/panels/     Dashboard pages
  data/             Created automatically (students.txt, users.txt)
```

## What the program does

- Teacher and Student login, registration, remember me, basic forgot password
- Add student: roll number, name, division, total classes, classes attended
- Auto percentage and status:
  - **Eligible** >= 75%
  - **Defaulter** 65% to 74.99%
  - **Critical** < 65%
- Defaulter list (everyone below 75%)
- Critical alert on the dashboard
- Consecutive classes needed to reach 75%
- Search by roll number, update attendance, mark today’s attendance
- View all students in a table
- Save / load records with file handling

## Formula (viva)

Percentage:

`(classesAttended * 100) / totalClasses`

Classes needed to reach 75%:

`(attended + x) / (total + x) >= 0.75`  
which becomes `x = 3 * total - 4 * attended`  
If already >= 75%, `x = 0`.

## OOP used

- **Encapsulation**: private fields + getters/setters in `Student` and `User`
- **Separate classes**: model, service, GUI
- **ArrayList**: in-memory list of students and users
- **Exception handling**: invalid numbers, missing files, save errors
- **File handling**: `BufferedReader` / `BufferedWriter` in `FileStorage`

Team: Sankirtana Arkala (02), Sarvesh Chaudhari (08), Shruti Devapache (13)
