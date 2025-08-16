# Requires admin privileges
# Run in PowerShell as Administrator

# ####################################################################################
# Install Chocolatey
# ####################################################################################

$ErrorActionPreference = "Stop"

Write-Host "Installing Chocolatey (if not already installed)..."
if (-not (Get-Command choco -ErrorAction SilentlyContinue)) {
    Set-ExecutionPolicy Bypass -Scope Process -Force
    [System.Net.ServicePointManager]::SecurityProtocol = [System.Net.ServicePointManager]::SecurityProtocol -bor 3072
    Invoke-Expression ((New-Object System.Net.WebClient).DownloadString('https://chocolatey.org/install.ps1'))
}

# ####################################################################################
# JAVA
# ####################################################################################

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

# ####################################################################################
# MySQL Installation & Database Setup Script
# ####################################################################################

# Variables For Script
$freshInstall = $false
$DB_PASS = $null
$PASSWORD = $null
$plainPassword = $null
$rootPassword = $null

# ####################################################################################
# Check if MySQL is installed; if not, install it via Chocolatey
# ####################################################################################
Write-Host "Checking if MySQL is installed."

if (-not (Get-Command mysql.exe -ErrorAction SilentlyContinue))
{
    Write-Host "Installing MySQL Server and Workbench..."
    choco install mysql -y
    choco install mysql.workbench -y

    # Reload Environment Variables (may require a new shell session if refreshenv not available)
    $env:Path = [System.Environment]::GetEnvironmentVariable("Path","Machine") + ";" +
            [System.Environment]::GetEnvironmentVariable("Path","User")
    try { refreshenv } catch { Write-Host "refreshenv not found. You may need to restart PowerShell." }

    # Checking if MySQL service exists
    Write-Host "Locating MySQL service..."
    $mysqlService = Get-Service | Where-Object { $_.DisplayName -like "*MySQL*" } | Select-Object -First 1

    if ($null -eq $mysqlService)
    {
        Write-Error "MySQL service not found. Check if MySQL installed correctly."
        exit 1
    }

    $freshInstall = $true
}
else
{
    Write-Host "MySQL is already installed."
}

# ####################################################################################
# Start MySQL Service if not already running
# ####################################################################################
$mysqlService = Get-Service | Where-Object { $_.Name -like "*mysql*" } | Select-Object -First 1
if ($null -eq $mysqlService)
{
    Write-Error "MySQL service not found. Cannot start MySQL."
    exit 1
}

if ($mysqlService.Status -ne "Running")
{
    Start-Service -Name $mysqlService.Name
    Write-Host "MySQL service '$($mysqlService.Name)' started."
}
else
{
    Write-Host "MySQL service '$($mysqlService.Name)' is already running."
}

# ####################################################################################
# Gather User Input for Database, User, and Password
# ####################################################################################
$DB_NAME = Read-Host "Please enter a Database Name"
$DB_USER = Read-Host "Please enter a Database Username"

# Prompt for password (hidden)
$PASSWORD = Read-Host "Enter MySQL root password" -AsSecureString

# Convert SecureString to plain text (needed for MySQL command)
$DB_PASS = [Runtime.InteropServices.Marshal]::PtrToStringAuto(
        [Runtime.InteropServices.Marshal]::SecureStringToBSTR($PASSWORD)
)

# ####################################################################################
# Create MySQL Statements
# ####################################################################################
$ExecutedStatement = $false

$mysqlCmd = @"
CREATE DATABASE IF NOT EXISTS $DB_NAME;
CREATE USER IF NOT EXISTS '$DB_USER'@'localhost' IDENTIFIED BY '$DB_PASS';
GRANT ALL PRIVILEGES ON $DB_NAME.* TO '$DB_USER'@'localhost';
FLUSH PRIVILEGES;
"@

# ####################################################################################
# Attempt execution without root password first
# ####################################################################################
try
{
    & mysql -u root -e $mysqlCmd

    if ($LASTEXITCODE -ne 0) { throw "MySQL command failed with exit code $LASTEXITCODE" }

    $ExecutedStatement = $true
    Write-Host "MYSQL: Successfully implemented DB Structure without password."
}
catch
{
    Write-Host "Caught an error: $($_.Exception.Message)"
    Write-Host "MYSQL: Failed executing statements without root password."
}

# ####################################################################################
# If failed, execute with root password
# ####################################################################################
if (-not $ExecutedStatement)
{
    Write-Host "Executing MySQL statements with root password..."
    Write-Host "If you haven't set a root password yet, one will be created now."

    $rootPassword = Read-Host "Enter MySQL root password" -AsSecureString
    $plainPassword = [Runtime.InteropServices.Marshal]::PtrToStringAuto(
            [Runtime.InteropServices.Marshal]::SecureStringToBSTR($rootPassword)
    )

    # If this was a fresh install and statements failed without root password, set root password
    if ($freshInstall)
    {
        Write-Host "Setting Root Password..."
        & mysql -u root -e "ALTER USER 'root'@'localhost' IDENTIFIED BY '$plainPassword'; FLUSH PRIVILEGES;"
        if ($LASTEXITCODE -ne 0)
        {
            Write-Host "MYSQL: Updating root password failed!"
            exit 1
        }
        Write-Host "MYSQL: Successfully updated root password."
    }

    # Execute statements using root password
    & mysql -u root -p$plainPassword -e $mysqlCmd
    if ($LASTEXITCODE -ne 0)
    {
        Write-Host "MYSQL: Failed executing database implementation!"
        exit 1
    }

    Write-Host "MYSQL: Successfully implemented DB Structure with password."
}

# ####################################################################################
# Create ENV File in current directory
# ####################################################################################
$envFilePath = ".\.env"

@"
# DO NOT COMMIT THIS FILE
DB_NAME=$DB_NAME
DB_USER=$DB_USER
DB_PASS=$DB_PASS
DB_HOST=localhost
DB_PORT=3306
"@ | Out-File -FilePath $envFilePath -Encoding UTF8

Write-Host "ENV: Successfully created in current directory!"

# ####################################################################################
# Reset Password Variables (Security)
# ####################################################################################
$DB_PASS = $null
$PASSWORD.Dispose()
$PASSWORD = $null
$plainPassword = $null
$rootPassword.Dispose()
$rootPassword = $null

Write-Host "Password variables cleared from memory."

# ####################################################################################
# Final Instructions
# ####################################################################################
Write-Host "Setup complete. You can now run your application or execute your BAT file."
