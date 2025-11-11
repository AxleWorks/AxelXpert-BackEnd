# AxleXpert Backend Run Script
# This script runs the Spring Boot application

Write-Host "=== AxleXpert Backend Startup ===" -ForegroundColor Cyan
Write-Host ""

Write-Host "Starting application..." -ForegroundColor Green
Write-Host "Press Ctrl+C to stop the server" -ForegroundColor Gray
Write-Host ""

# Run the Spring Boot application
.\mvnw.cmd spring-boot:run
