#!/bin/bash

# Run the commands before executing script to give script permissions & run file
# sudo chmod +x Start_App_Linux_And_Mac_Bash_Script.sh && ./Start_App_Linux_And_Mac_Bash_Script.sh 

##############################################################
## Setting Temp Variables For App to Run
##############################################################

clear
printf "\n\n\n"

echo "==============================="
echo "Starting My GYM App..."
echo "==============================="

printf "\n"

if [ ! -f .env ]; then
  echo "❌ .env file not found. Please run the prerequisites script first."
  exit 1
fi

printf "\n\n"

# Run your JAR (edit filename if needed)
java -jar Gym_App.jar

printf "\n\n"

# Pause at end (press Enter to continue)
read -p "Press Enter to continue..."
