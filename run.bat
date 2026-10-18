@echo off
cd /d "%~dp0"
if not exist bin\attendance\Main.class (
    echo Application is not compiled yet. Running compile.bat first...
    call compile.bat silent
)
echo Starting Defaulter List Generator...
java -cp bin attendance.Main
if errorlevel 1 (
    echo.
    echo Could not start the application.
    pause
)
