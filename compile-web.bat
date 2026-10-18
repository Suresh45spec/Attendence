@echo off
setlocal EnableExtensions
cd /d "%~dp0"

if not defined CATALINA_HOME (
    if exist "%~dp0tools\apache-tomcat\lib\servlet-api.jar" (
        set "CATALINA_HOME=%~dp0tools\apache-tomcat"
    )
)

if not exist "%CATALINA_HOME%\lib\servlet-api.jar" (
    echo.
    echo servlet-api.jar was not found.
    echo Install Apache Tomcat 9, then either:
    echo   set CATALINA_HOME=C:\path\to\apache-tomcat-9
    echo or run start-web.bat once so Tomcat is unpacked into tools\apache-tomcat
    echo.
    exit /b 1
)

if not exist "webapp\WEB-INF\classes" mkdir "webapp\WEB-INF\classes"
if not exist "webapp\WEB-INF\lib" mkdir "webapp\WEB-INF\lib"

if not exist "webapp\WEB-INF\lib\mysql-connector-j-8.4.0.jar" (
    echo Downloading MySQL JDBC driver...
    powershell -NoProfile -Command "Invoke-WebRequest -Uri 'https://repo1.maven.org/maven2/com/mysql/mysql-connector-j/8.4.0/mysql-connector-j-8.4.0.jar' -OutFile 'webapp\WEB-INF\lib\mysql-connector-j-8.4.0.jar'"
    if errorlevel 1 (
        echo Could not download the MySQL JDBC driver. Download mysql-connector-j-8.4.0.jar into webapp\WEB-INF\lib
        exit /b 1
    )
)

echo Compiling web application...
dir /s /b web-src\*.java > "%TEMP%\attendance-web-sources.txt"
echo src\attendance\model\Student.java>> "%TEMP%\attendance-web-sources.txt"
echo src\attendance\util\AttendanceCalculator.java>> "%TEMP%\attendance-web-sources.txt"

javac -encoding UTF-8 -cp "%CATALINA_HOME%\lib\servlet-api.jar" -d "webapp\WEB-INF\classes" @"%TEMP%\attendance-web-sources.txt"
if errorlevel 1 (
    echo.
    echo WEB COMPILE FAILED.
    exit /b 1
)

echo Web compile successful. Classes are in webapp\WEB-INF\classes
if not "%1"=="silent" pause
endlocal
