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

REM Try to compile and run
echo Compiling Java files...
if not exist "target\classes" mkdir target\classes

REM Compile all Java files
dir /s /b src\*.java > sources.txt
javac -d target\classes -cp "target\classes" @sources.txt
del sources.txt

if errorlevel 1 (
    echo Compilation failed
    pause
    exit /b 1
)

echo Running application...
cd target\classes
java -cp . com.geekonsite.CallManagementApplication

pause
