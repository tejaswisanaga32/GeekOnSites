@echo off
echo Starting GeekOnSites Backend...
echo.

REM Check if Java is installed
java -version
if errorlevel 1 (
    echo Java is not installed or not in PATH
    echo Please install Java JDK and add to PATH
    pause
    exit /b 1
)

REM Check if target directory exists
if not exist "target" (
    echo Creating target directory...
    mkdir target
)

REM Simple compilation and run
echo Compiling and running application...
cd src\main\java
javac -cp ..\..\..\target\lib\* -d ..\..\..\target\classes com\geekonsite\*.java
cd ..\..\..\

if exist "target\classes\com\geekonsite\CallManagementApplication.class" (
    echo Running application...
    java -cp target\classes com.geekonsite.CallManagementApplication
) else (
    echo Compilation failed. Please check the source code.
    echo Consider installing Maven for easier build process.
    pause
)
