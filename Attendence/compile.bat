@echo off
cd /d "%~dp0"
if not exist bin mkdir bin
if not exist data mkdir data
echo Compiling Java files...
javac -encoding UTF-8 -d bin -sourcepath src src\attendance\Main.java
if errorlevel 1 (
    echo.
    echo COMPILE FAILED. Make sure JDK is installed and javac is in PATH.
    pause
    exit /b 1
)
echo Compile successful. Class files are in the bin folder.
if not "%1"=="silent" pause
