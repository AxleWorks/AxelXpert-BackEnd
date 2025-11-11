# AxleXpert Application Startup Script# AxleXpert Application Startup Script

# This script loads environment variables from .env file and starts the application# This script loads environment variables from .env file and starts the application



Write-Host "Loading environment variables from .env file..." -ForegroundColor CyanWrite-Host "Loading environment variables from .env file..." -ForegroundColor Cyan



# Check if .env file exists# Check if .env file exists

if (-Not (Test-Path ".env")) {if (-Not (Test-Path ".env")) {

    Write-Host "ERROR: .env file not found!" -ForegroundColor Red    Write-Host "ERROR: .env file not found!" -ForegroundColor Red

    Write-Host "Please create a .env file based on .env.example" -ForegroundColor Yellow    Write-Host "Please create a .env file based on .env.example" -ForegroundColor Yellow

    exit 1    exit 1

}}



# Load environment variables from .env file# Load environment variables from .env file

Get-Content .env | ForEach-Object {Get-Content .env | ForEach-Object {

    if ($_ -match '^\s*([^#][^=]*?)\s*=\s*(.*?)\s*$') {    if ($_ -match '^\s*([^#][^=]*?)\s*=\s*(.*?)\s*$') {

        $name = $matches[1]        $name = $matches[1]

        $value = $matches[2]        $value = $matches[2]

        Set-Item -Path "env:$name" -Value $value        Set-Item -Path "env:$name" -Value $value

    }    }

}}



Write-Host "Environment variables loaded successfully!" -ForegroundColor GreenWrite-Host "Environment variables loaded successfully!" -ForegroundColor Green

Write-Host "Starting AxleXpert application..." -ForegroundColor YellowWrite-Host "Starting AxleXpert application..." -ForegroundColor Yellow

Write-Host ""Write-Host ""



# Run the application# Run the application

java -jar target\AxleXpert-0.0.1-SNAPSHOT.jarjava -jar target\AxleXpert-0.0.1-SNAPSHOT.jar

