#!/bin/bash

# Run the commands before executing script to give script permissions & run file
# sudo chmod +x Start_App_Linux_And_Mac_Bash_Script.sh && ./Start_App_Linux_And_Mac_Bash_Script.sh 

##############################################################
## Setting Temp Variables For App to Run
##############################################################

clear

printf "\n\n\n########################################################################################################################"
printf "\nStarting My GYM App..."
printf "\n########################################################################################################################\n\n"

if [ ! -f .env ]; then
  printf "❌ .env file not found. Please run the prerequisites script first.\n\n"
  exit 1
fi

# Run your JAR (edit filename if needed)
java -jar Gym_App.jar

# Pause at end (press Enter to continue)
printf "\n\n\n############################################################################################################################"
printf "\nProgram Terminated..."
printf "\n############################################################################################################################\n\n"

read -p "Press Enter to continue..."
