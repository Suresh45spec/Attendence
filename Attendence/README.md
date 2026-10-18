# Defaulter List Generator – Student Attendance Management System

College mini project in **Core Java + Swing**. Data is stored in text files (no database).

## How to run on Windows

1. Install JDK and make sure `javac` and `java` work in Command Prompt.
2. Double-click `compile.bat`, then `run.bat`.
3. Or from this folder:

```bat
compile.bat
run.bat
```

Manual commands:

```bat
javac -encoding UTF-8 -d bin -sourcepath src src\attendance\Main.java
java -cp bin attendance.Main
```

## Demo login

| Role | Username / Roll | Password |
|------|-----------------|----------|
| Teacher | `teacher` | `teacher123` |
| Student | `101` | `student123` |

You can also create a new account from **Register**.

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
