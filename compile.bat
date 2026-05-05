@echo off
echo ===== SRMS Compiler =====
echo.

set JAVAFX_PATH=lib\javafx-sdk-21.0.2\lib
set CLASSPATH=lib\mysql-connector-j-9.6.0.jar;lib\itextpdf-5.5.13.4.jar

if not exist out mkdir out

echo Compiling source files...
javac -d out --module-path %JAVAFX_PATH% --add-modules javafx.controls,javafx.graphics -cp "%CLASSPATH%" -sourcepath src src\com\srms\App.java src\com\srms\config\*.java src\com\srms\model\*.java src\com\srms\dao\*.java src\com\srms\service\*.java src\com\srms\controller\*.java src\com\srms\view\*.java src\com\srms\util\*.java

if %errorlevel% neq 0 (
    echo.
    echo COMPILATION FAILED!
    pause
    exit /b 1
)

echo.
echo Compilation successful!
echo.
pause
