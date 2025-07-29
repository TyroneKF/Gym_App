@echo off
echo ==============================
echo Starting My GYM App...
echo ==============================

:: Set environment variables (EDIT THESE)
set DB_HOST=localhost
set DB_PORT=3306
set DB_NAME=gymapp00001
set DB_USER=root
set DB_PASS=password

:: Optional: Show values (except password)
echo DB_HOST = %DB_HOST%
echo DB_PORT = %DB_PORT%
echo DB_NAME = %DB_NAME%
echo DB_USER = %DB_USER%

:: Run your JAR (edit filename if needed)
java -jar Gym_App.jar

pause


