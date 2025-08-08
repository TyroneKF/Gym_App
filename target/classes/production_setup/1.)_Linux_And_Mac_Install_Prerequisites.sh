#!/bin/bash

set -e  # Exit on any error
clear

printf "\n\n=================================================="
printf "\nInstalling OpenJDK 17 "
printf "\n=================================================="

# -------- Step 1: System Updates & Java --------
printf "\n\n[1/6] Updating packages...\n\n"

sudo apt update

printf "\n\n[2/6] Installing OpenJDK 17... \n\n"

sudo apt install -y openjdk-17-jdk
echo
java -version



printf "\n\n=================================================="
printf "\nInstalling MySQL"
printf "\n=================================================="

# -------- Step 2: Install MySQL Server --------

printf "\n\n[3/6] Installing MySQL Server...\n\n"

# sudo apt purge -y mysql-server
# sudo apt autoremove --purge -y
# sudo rm -rf /etc/mysql /var/lib/mysql
# sudo apt install --reinstall -y mysql-server

sudo apt install -y mysql-server


printf "\n\n[4/6] Enabling MySQL service...\n\n"

sudo systemctl enable mysql
sudo systemctl start mysql
mysql --version


printf "\n\n=================================================="
printf "\nDatabase Setup"
printf "\n=================================================="

printf "\n\n[5/6] Database Setup\n\n"

# Prompt for user for DB info to avoid internal conflicts on their mysql system
read -p "Enter the a DB name (MYSQL): " APP_DB
read -p "Enter the username (MYSQL): " APP_USER
read -s -p "Enter a password (MYSQL): " APP_PASS # Password Hidden


# Confirm inputs
printf "\n\n\nCreating database '${APP_DB}' and user '${APP_USER}'..."
printf "\nYou might be prompted to enter your password in the next steps (Mysql Setup)!!"

# -------- Step 4: Create DB, User, and Grant Privileges --------
sudo mysql <<EOF
CREATE DATABASE IF NOT EXISTS \`${APP_DB}\`;
CREATE USER IF NOT EXISTS '${APP_USER}'@'localhost' IDENTIFIED BY '${APP_PASS}';
GRANT ALL PRIVILEGES ON \`${APP_DB}\`.* TO '${APP_USER}'@'localhost';
FLUSH PRIVILEGES;
EOF

printf "\n\nDatabase and user created successfully."


# -------- Step 5: Generate .env File --------
printf "\n\n[6/6] Creating .env file for your application... "

cat <<ENV > .env
# DO NOT COMMIT THIS FILE
DB_NAME=${APP_DB}
DB_USER=${APP_USER}
DB_PASS=${APP_PASS}
DB_HOST=localhost
DB_PORT=3306
ENV

chmod 600 .env  # Secure file

printf "\n\n.env file created with DB credentials (only readable by you)."
printf "\n✅ Installation and configuration complete."
printf "\n🔒 Remember: Do not commit the .env file to source control.\n\n"

# Optionally: Add .env to .gitignore if project is initialized
if [ -f ".gitignore" ]; then
  if ! grep -qxF ".env" .gitignore; then
    echo ".env" >> .gitignore
    echo ".env added to .gitignore."
  fi
fi
