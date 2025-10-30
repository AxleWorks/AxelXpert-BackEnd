# AxleXpert Backend Run Script
# This script sets required environment variables and runs the Spring Boot application

Write-Host "=== AxleXpert Backend Startup ===" -ForegroundColor Cyan
Write-Host ""

# Load .env file if exists
if (Test-Path .env) {
    Write-Host "Loading environment variables from .env file..." -ForegroundColor Green
    Get-Content .env | ForEach-Object {
        if ($_ -match '^\s*([^#][^=]+)=(.*)$') {
            $name = $matches[1].Trim()
            $value = $matches[2].Trim()
            [Environment]::SetEnvironmentVariable($name, $value, "Process")
        }
    }
    Write-Host ""
} else {
    Write-Host "WARNING: .env file not found" -ForegroundColor Yellow
    Write-Host ""
}

# Check if required environment variables are set
$missingVars = @()

if (-not $env:DB_PASSWORD) {
    Write-Host "WARNING: DB_PASSWORD not set" -ForegroundColor Yellow
    $missingVars += "DB_PASSWORD"
}

if (-not $env:JWT_SECRET_KEY) {
    Write-Host "WARNING: JWT_SECRET_KEY not set - using default (INSECURE for production!)" -ForegroundColor Yellow
    $env:JWT_SECRET_KEY = "your-very-long-secret-key-that-should-be-at-least-256-bits-long-for-security"
}

if (-not $env:GEMINI_API_KEY) {
    Write-Host "WARNING: GEMINI_API_KEY not set - chatbot features will not work" -ForegroundColor Yellow
}

if (-not $env:MAIL_PASSWORD) {
    Write-Host "WARNING: MAIL_PASSWORD not set - email features will not work" -ForegroundColor Yellow
}

Write-Host ""
Write-Host "Starting application..." -ForegroundColor Green
Write-Host "Press Ctrl+C to stop the server" -ForegroundColor Gray
Write-Host ""

# Run the Spring Boot application
.\mvnw.cmd spring-boot:run
