# Requires admin privileges
# Run in PowerShell as Administrator

# ###############################
# 
# ###############################

$ErrorActionPreference = "Stop"

Write-Host "Installing Chocolatey (if not already installed)..."
if (-not (Get-Command choco -ErrorAction SilentlyContinue)) {
    Set-ExecutionPolicy Bypass -Scope Process -Force
    [System.Net.ServicePointManager]::SecurityProtocol = [System.Net.ServicePointManager]::SecurityProtocol -bor 3072
    Invoke-Expression ((New-Object System.Net.WebClient).DownloadString('https://chocolatey.org/install.ps1'))
}

# ###############################
# JAVA
# ###############################

Write-Host "Installing OpenJDK 17..."
choco install openjdk17 -y

Write-Host "Detecting installed OpenJDK 17 path..."
$jdkPath = Get-ChildItem "C:\Program Files\Eclipse Adoptium" -Directory |
           Where-Object { $_.Name -like "jdk-17*" } |
           Select-Object -First 1 |
           ForEach-Object { "$($_.FullName)\bin" }

if (-not $jdkPath -or -not (Test-Path $jdkPath)) {
    Write-Error "OpenJDK 17 installation path not found."
    exit 1
}

Write-Host "Adding OpenJDK 17 to system PATH..."
[Environment]::SetEnvironmentVariable("Path", $env:Path + ";$jdkPath", [System.EnvironmentVariableTarget]::Machine)
$env:Path += ";$jdkPath"

Write-Host "Verifying Java installation..."
java -version

# ###############################
# MYSQL
# ###############################
Write-Host "Installing MySQL Server..."
choco install mysql -y
choco install mysql.workbench -y

# Set MySQL root password
$MySQLRootPassword = "password"

Write-Host "Locating MySQL service name..."
$mysqlService = Get-Service | Where-Object { $_.DisplayName -like "*MySQL*" } | Select-Object -First 1

if ($null -eq $mysqlService) {
    Write-Error "MySQL service not found. Check if MySQL installed correctly."
    exit 1
}

Write-Host "Starting MySQL Service: $($mysqlService.Name)..."
Start-Service $mysqlService.Name

Start-Sleep -Seconds 10

Write-Host "Setting MySQL root password using mysql_native_password..."
$alterSql = "ALTER USER 'root'@'localhost' IDENTIFIED WITH mysql_native_password BY '$MySQLRootPassword'; FLUSH PRIVILEGES;"

try {
    mysql -u root -e "$alterSql"
    Write-Host "Root password set successfully."
}
catch {
    Write-Warning "Failed to set root password — it may already be set."
}

$env:MYSQL_PWD = $MySQLRootPassword

$grantSql = @"
GRANT ALL PRIVILEGES ON *.* TO 'root'@'localhost' WITH GRANT OPTION;
FLUSH PRIVILEGES;
"@

Write-Host "Running SQL grant statement..."
mysql -u root -e "$grantSql"

Write-Host "`n✅ Installation and configuration completed successfully!"
