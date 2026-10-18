@echo off
setlocal
cd /d "%~dp0"
if exist "%~dp0tools\apache-tomcat\bin\catalina.bat" (
    set "CATALINA_HOME=%~dp0tools\apache-tomcat"
    call "%~dp0tools\apache-tomcat\bin\catalina.bat" stop
) else if defined CATALINA_HOME (
    call "%CATALINA_HOME%\bin\catalina.bat" stop
) else (
    echo Tomcat was not found.
)
endlocal
