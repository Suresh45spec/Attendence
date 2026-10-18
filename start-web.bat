@echo off
setlocal EnableExtensions
cd /d "%~dp0"

if not defined JAVA_HOME (
    for /f "delims=" %%i in ('where java 2^>nul') do (
        pushd "%%~dpi.."
        set "JAVA_HOME=%%CD"
        popd
        goto :java_home_done
    )
)
:java_home_done
echo JAVA_HOME=%JAVA_HOME%

set "TOMCAT_DIR=%~dp0tools\apache-tomcat"
set "TOMCAT_ZIP=%~dp0tools\apache-tomcat-9.0.108.zip"
set "TOMCAT_URL=https://archive.apache.org/dist/tomcat/tomcat-9/v9.0.108/bin/apache-tomcat-9.0.108.zip"

if not exist "%~dp0tools" mkdir "%~dp0tools"

if not exist "%TOMCAT_DIR%\bin\catalina.bat" (
    echo Downloading Apache Tomcat 9.0.108...
    powershell -NoProfile -Command "Invoke-WebRequest -Uri '%TOMCAT_URL%' -OutFile '%TOMCAT_ZIP%'"
    if errorlevel 1 (
        echo Could not download Tomcat. Install Apache Tomcat 9 yourself and set CATALINA_HOME.
        exit /b 1
    )
    echo Unpacking Tomcat...
    powershell -NoProfile -Command "Expand-Archive -Path '%TOMCAT_ZIP%' -DestinationPath '%~dp0tools' -Force"
    if exist "%~dp0tools\apache-tomcat-9.0.108" (
        move "%~dp0tools\apache-tomcat-9.0.108" "%TOMCAT_DIR%" >nul
    )
)

set "CATALINA_HOME=%TOMCAT_DIR%"
call "%~dp0compile-web.bat" silent
if errorlevel 1 (
    echo Compile failed. Tomcat was not started.
    pause
    exit /b 1
)

if exist "%CATALINA_HOME%\webapps\attendance" rmdir /s /q "%CATALINA_HOME%\webapps\attendance"
mkdir "%CATALINA_HOME%\webapps\attendance"
xcopy /e /i /y "webapp\*" "%CATALINA_HOME%\webapps\attendance\" >nul

echo.
echo Starting Tomcat. Open Chrome at:
echo   http://localhost:8080/attendance/
echo.
echo Stop the server with stop-web.bat  (or close this window after Ctrl+C).
echo.
call "%CATALINA_HOME%\bin\catalina.bat" run
endlocal
