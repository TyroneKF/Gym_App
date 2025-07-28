# Requires admin privileges
# Run in PowerShell as Administrator

$ErrorActionPreference = "Stop"

# Set MySQL root password
$MySQLRootPassword = "password"

Write-Host "Installing Chocolatey (if not already installed)..."
if (-not (Get-Command choco -ErrorAction SilentlyContinue)) {
    Set-ExecutionPolicy Bypass -Scope Process -Force
    [System.Net.ServicePointManager]::SecurityProtocol = [System.Net.ServicePointManager]::SecurityProtocol -bor 3072
    Invoke-Expression ((New-Object System.Net.WebClient).DownloadString('https://chocolatey.org/install.ps1'))
}

Write-Host "Installing OpenJDK 17..."
choco install openjdk17 -y

Write-Host "Verifying Java installation..."
java -version

Write-Host "Installing MySQL Server..."
choco install mysql -y

Write-Host "Locating MySQL service name..."
# Find the actual MySQL service name
$mysqlService = Get-Service | Where-Object { $_.DisplayName -like "*MySQL*" } | Select-Object -First 1

if ($null -eq $mysqlService) {
    Write-Error "MySQL service not found. Check if MySQL installed correctly."
    exit 1
}

Write-Host "Starting MySQL Service: $($mysqlService.Name)..."
Start-Service $mysqlService.Name

# Wait a few seconds for the service to be ready
Start-Sleep -Seconds 10

Write-Host "Configuring MySQL root user..."
$env:MYSQL_PWD = $MySQLRootPassword

$grantSql = @"
GRANT ALL PRIVILEGES ON *.* TO 'root'@'localhost' WITH GRANT OPTION;
FLUSH PRIVILEGES;
"@

Write-Host "Running SQL grant statement..."
mysql -u root -e "$grantSql"

Write-Host "`n✅ Installation and configuration completed successfully!"
