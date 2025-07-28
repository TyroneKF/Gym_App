#!/bin/bash

# Run the commands before executing script to give script permissions & run file
# sudo chmod +x Start_App_Linux_And_Mac_Bash_Script.sh && ./Start_App_Linux_And_Mac_Bash_Script.sh 

##############################################################
## Setting Temp Variables For App to Run
##############################################################

clear
echo "==============================="
echo "Starting My GYM App..."
echo "==============================="

# Set environment variables (EDIT THESE)
export DB_HOST=localhost
export DB_PORT=3306
export DB_NAME=gymapp00001
export DB_USER=root
export DB_PASS=password

# Optional: Show values (except password)
#echo "DB_HOST = $DB_HOST"
#echo "DB_PORT = $DB_PORT"
#echo "DB_NAME = $DB_NAME"
#echo "DB_USER = $DB_USER"

# Run your JAR (edit filename if needed)
java -jar Gym_App.jar

# Pause at end (press Enter to continue)
read -p "Press Enter to continue..."
