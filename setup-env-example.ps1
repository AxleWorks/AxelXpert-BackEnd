# AxleXpert Backend - Environment Variables Setup Example
# Copy this file to setup-env.ps1 and fill in your actual values
# Then run: . .\setup-env.ps1 (note the leading dot and space)

Write-Host "Setting up environment variables for AxleXpert Backend..." -ForegroundColor Cyan

# Database Password (for MySQL connection)
$env:DB_PASSWORD = "your-database-password-here"

# JWT Secret Key (used for signing tokens - keep this secure!)
# Should be at least 256 bits (32 characters) long
$env:JWT_SECRET_KEY = "your-very-long-secret-key-at-least-32-chars"

# Gemini API Key (for AI chatbot features)
$env:GEMINI_API_KEY = "your-gemini-api-key-here"

# Optional: Gemini Project ID (if using Vertex AI)
# $env:GEMINI_PROJECT_ID = "your-gcp-project-id"

# Optional: Gemini Location
# $env:GEMINI_LOCATION = "us-central1"

# Mail Password (for sending emails via SMTP)
$env:MAIL_PASSWORD = "your-gmail-app-password-here"

Write-Host "Environment variables set successfully!" -ForegroundColor Green
Write-Host ""
Write-Host "You can now run the application with: .\run-app.ps1" -ForegroundColor Yellow
