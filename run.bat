@echo off
echo ===== Starting SRMS =====
echo.

set JAVAFX_PATH=lib\javafx-sdk-21.0.2\lib
set CLASSPATH=out;lib\mysql-connector-j-9.6.0.jar;lib\itextpdf-5.5.13.4.jar

REM Compile first if out directory doesn't exist
if not exist out\com\srms\App.class (
    echo Compiling...
    call compile.bat
)

echo Launching application...
java --module-path %JAVAFX_PATH% --add-modules javafx.controls,javafx.graphics -cp "%CLASSPATH%" com.srms.App

if %errorlevel% neq 0 (
    echo.
    echo Application exited with an error.
    pause
)
