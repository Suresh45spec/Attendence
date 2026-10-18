# 🖥️ SETUP GUIDE — How to Run This Project on Another Computer (VS Code)

Follow these steps **in order**. Total time: about 15–20 minutes.
Write down your MySQL password when you set it — you'll need it in Step 3.

---

## STEP 0 — Copy the project

Copy the whole `Attendence` folder (pen drive, Google Drive, GitHub, whatever) to the new computer.

✅ Good news: **Tomcat and the MySQL driver are already inside the folder**
(in the `tools` and `webapp\WEB-INF\lib` folders), so you don't need to download them again.

---

## STEP 1 — Install the 3 required programs

| # | Program | Download | Check it worked |
|---|---------|----------|-----------------|
| 1 | **JDK 17 or newer** (11 also works) | https://adoptium.net | Open terminal → type `java -version` |
| 2 | **MySQL Community Server 8.0** | https://dev.mysql.com/downloads/mysql/ | During install it asks you to set a **root password** — remember it! |
| 3 | **VS Code** | https://code.visualstudio.com | Open it once |

> ⚠️ Do **NOT** install Tomcat 10 or 11. This project needs **Tomcat 9** —
> but the folder already contains Tomcat 9, so just skip installing any Tomcat.

Also install these VS Code extensions (optional but helpful):
- **Extension Pack for Java** (Microsoft)
- **Community Server Connectors** (optional, for managing Tomcat inside VS Code)

---

## STEP 2 — Open the project in VS Code

1. Open **VS Code**
2. **File → Open Folder…** → choose the `Attendence` folder
3. Open the integrated terminal: **Terminal → New Terminal** (or `Ctrl + `` `)
4. In the terminal check Java works:
   ```bat
   javac -version
   java -version
   ```

You should see the folder structure on the left:
```
Attendence/
  src/           ← desktop Swing app
  web-src/       ← Java backend of the web app (servlets, DAOs)
  webapp/        ← web pages (JSP, CSS, JS) + WEB-INF
  sql/schema.sql ← database script
  tools/         ← Tomcat 9 (already included!)
  start-web.bat  ← starts everything
  stop-web.bat   ← stops the server
```

---

## STEP 3 — Set up MySQL (do this only once)

### 3a. Start the MySQL service
Press `Win + R` → type `services.msc` → find **MySQL80** → make sure it says **Running**
(right-click → Start if it's stopped).

### 3b. Tell the project your MySQL password

Open the file `webapp\WEB-INF\db.properties` in VS Code and change the password line:

```properties
jdbc.url=jdbc:mysql://localhost:3306/attendance_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
jdbc.user=root
jdbc.password=YOUR_OWN_MYSQL_PASSWORD      ← change this line only
```

### 3c. Create the database (run the schema)
In the VS Code terminal (or Command Prompt), run:

```bat
"C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe" -u root -p < sql\schema.sql
```

- Enter your MySQL root password when asked
- No error message = success ✅
- If your MySQL is installed in a different folder, search for `mysql.exe` on your PC and use that full path

> What did this do? It created the database `attendance_db` with 3 tables
> (`teachers`, `students`, `attendance_records`) and loaded the demo accounts.

---

## STEP 4 — Start the web application

### Easy way (recommended)
Double-click **`start-web.bat`** in the project folder.

Or in the VS Code terminal:
```bat
start-web.bat
```

What happens:
1. It compiles all Java files (`compile-web.bat` runs automatically)
2. It copies the app into Tomcat
3. It starts Tomcat — **keep this terminal/window open**, the server must keep running

### The manual way (if you want to understand each step)
```bat
compile-web.bat          :: 1. compile Java files
start-web.bat            :: 2. deploy + start Tomcat
```

---

## STEP 5 — Open in the browser

Go to: **http://localhost:8080/attendance/**

You should see the **Teacher Login** page.

### Demo logins
| Role | Username / Roll | Password |
|------|-----------------|----------|
| Teacher | `teacher` | `teacher123` |
| Student | `101` | `student123` |

Try it in this order for a good demo:
1. Log in as **teacher** → look at the dashboard cards
2. Click **Mark Attendance** → pick date/subject/division → mark a few P/A → Save
3. Open **Defaulter List** → show the "Classes to 75%" column and CSV export
4. Click **Logout** → log in as **student 101** → show the student dashboard

---

## STEP 6 — Stop the server when finished

- Click **`stop-web.bat`**, or
- Go to the Tomcat window and press `Ctrl + C`, or
- Close the window Tomcat is running in

To start again later: just run `start-web.bat` again.

---

## 🧩 How to run it INSIDE VS Code (optional, no .bat files)

1. `Ctrl + `` ` to open the terminal
2. Compile:
   ```bat
   compile-web.bat
   ```
3. Set Tomcat home (only needed if the bat files can't find it):
   ```bat
   set CATALINA_HOME=%CD%\tools\apache-tomcat
   ```
4. Start Tomcat:
   ```bat
   %CATALINA_HOME%\bin\catalina.bat run
   ```
5. Stop with `Ctrl + C` in the same terminal.

Tip: install the **Extension Pack for Java**, then you can click "Run" on Java files
and get autocomplete + error underlines for all the servlets in `web-src`.

---

## 🐛 Troubleshooting (most common problems)

| Problem | Solution |
|---|---|
| `localhost:8080` page never loads | Is the `start-web.bat` window still open? Any error in it? |
| 404 error | The address must be exactly `http://localhost:8080/attendance/` (with `/attendance/`) |
| "Could not connect to MySQL" | Is service **MySQL80** running? Is the password in `db.properties` correct? Did you run `schema.sql`? |
| `Access denied for user 'root'` | Wrong password in `webapp\WEB-INF\db.properties` |
| `port 8080 is already in use` | Another program (or an old Tomcat) is using port 8080 → close it, or edit `tools\apache-tomcat\conf\server.xml` and change `8080` to `8081` |
| `jakarta.servlet` error | You started a Tomcat 10/11 by mistake. Use the Tomcat 9 inside `tools\apache-tomcat` |
| Compile errors about `servlet-api` | Run `start-web.bat` once so Tomcat exists, then `compile-web.bat` |
| Blank page / old version showing | Hard refresh with `Ctrl + F5`, and remember to re-run `start-web.bat` after editing files so changes are copied into Tomcat |
| Changed a `.java` file but nothing changed | Re-run `compile-web.bat`, then restart Tomcat |
| Changed a `.jsp`/`.css` file | Just refresh the browser (JSPs recompile automatically) |
| `mysql.exe` not found | Search your PC for `mysql.exe`, copy its full path and use it in the schema command |
| MySQL service won't start | Install VC++ Redistributable, or restart your computer; check `services.msc` |

---

## 📁 What runs where (one-line map)

```
Browser  ──HTTP──►  Tomcat (port 8080, in tools\apache-tomcat)
                       ├── JSP pages      (webapp\*.jsp)      → what you see
                       ├── Servlets       (web-src\...\servlet) → decides what to do
                       ├── DAO + JDBC     (web-src\...\dao)     → talks to MySQL
                       └── MySQL (port 3306, service MySQL80)  → stores everything
```

---

## ✅ Quick checklist before your demo

- [ ] MySQL80 service is **Running**
- [ ] `db.properties` has the correct password
- [ ] `start-web.bat` window is open (server running)
- [ ] `http://localhost:8080/attendance/` opens the Teacher Login page
- [ ] Demo logins work: `teacher/teacher123` and `101/student123`
- [ ] Keep `PROJECT-EXPLANATION.md` open for viva questions

**All checked = you're ready to present! 🎉**
