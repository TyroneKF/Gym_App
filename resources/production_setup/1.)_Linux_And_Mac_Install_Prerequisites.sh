#!/bin/bash

# #############################################################
# Installing Prerequisites: OpenJDK 17 and MySQL Server
# #############################################################

set -e  # Exit immediately if a command fails

# Secure MySQL root password (set yours here)
MYSQL_ROOT_PASSWORD="password"

echo "Updating package list..."
sudo apt update

echo "Installing OpenJDK 17 (Java)..."
sudo add-apt-repository -y ppa:openjdk-r/ppa
sudo apt update
sudo apt install -y openjdk-17-jdk

echo "Verifying Java installation..."
java -version

echo "Installing MySQL server..."
sudo apt install -y mysql-server

echo "Starting and enabling MySQL service..."
sudo systemctl start mysql
sudo systemctl enable mysql

echo "Verifying MySQL installation..."
mysql --version

echo "Configuring MySQL root user..."

MYSQL_USER="root"
MYSQL_PASS="password"

# SQL commands to run
SQL="
ALTER USER 'root'@'localhost' IDENTIFIED WITH mysql_native_password BY '$MYSQL_PASS';
GRANT ALL PRIVILEGES ON *.* TO 'root'@'localhost' WITH GRANT OPTION;
FLUSH PRIVILEGES;
"
# Run the command using mysql client
mysql -u "$MYSQL_USER" -p"$MYSQL_PASS" -e "$SQL"

echo "Installation and configuration completed successfully!"
