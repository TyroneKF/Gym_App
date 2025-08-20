@echo off
echo ==============================
echo Starting My GYM App...
echo ==============================

:: Check if .env file exists
if not exist ".env" (
    echo ❌ .env file not found. Please run the prerequisites script first.
    exit /b 1
)

echo ✅ .env file found, continuing...

:: Run JAR
java -jar Gym_App.jar

pause


