# AxleXpert Backend - Quick Start Guide

## ✅ Fixed Issues

All package naming issues have been resolved:
- Fixed package declarations in `Security` folder to match folder name (was `security`, now `Security`)
- Removed unused imports
- All compilation errors resolved

## 🚀 Running the Application

### Prerequisites
- JDK 21 installed
- Maven (included via wrapper)
- Access to MySQL database (configured in `application.properties`)

### Quick Run (3 steps)

1. **Set up environment variables** (one time only)
   ```powershell
   # Copy the example file
   Copy-Item setup-env-example.ps1 setup-env.ps1
   
   # Edit setup-env.ps1 and fill in your actual values
   notepad setup-env.ps1
   
   # Load the environment variables
   . .\setup-env.ps1
   ```

2. **Run the application**
   ```powershell
   .\run-app.ps1
   ```

3. **Access the application**
   - API will be available at: http://localhost:8080
   - Test endpoint: http://localhost:8080/api/auth/login

### Environment Variables Required

| Variable | Required | Description |
|----------|----------|-------------|
| `DB_PASSWORD` | **Yes** | MySQL database password |
| `JWT_SECRET_KEY` | **Yes** | Secret key for JWT token signing (min 32 chars) |
| `GEMINI_API_KEY` | No | Google Gemini API key for chatbot features |
| `MAIL_PASSWORD` | No | Gmail app password for email features |

### Alternative: Manual Run

```powershell
# Set environment variables manually
$env:DB_PASSWORD = "your-password"
$env:JWT_SECRET_KEY = "your-jwt-secret-at-least-32-characters-long"
$env:GEMINI_API_KEY = "your-gemini-key"
$env:MAIL_PASSWORD = "your-mail-password"

# Run with Maven wrapper
.\mvnw.cmd spring-boot:run
```

### Build Only (Skip Tests)

```powershell
.\mvnw.cmd -DskipTests clean package
```

### Run Tests

To fix test failures, you need to add test database configuration:

1. Create `src/test/resources/application.properties`:
   ```properties
   spring.datasource.url=jdbc:h2:mem:testdb
   spring.datasource.username=sa
   spring.datasource.password=
   spring.jpa.hibernate.ddl-auto=create-drop
   spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect
   ```

2. Run tests:
   ```powershell
   .\mvnw.cmd test
   ```

## 📁 Key Files

- **Main Application**: `src/main/java/com/login/AxleXpert/AxleXpertApplication.java`
- **Configuration**: `src/main/resources/application.properties`
- **Security**: `src/main/java/com/login/AxleXpert/Security/`
  - `SecurityConfig.java` - Security configuration
  - `JwtUtil.java` - JWT token utilities
  - `JwtFilter.java` - JWT authentication filter

## 🔧 Troubleshooting

### "Unable to determine Dialect without JDBC metadata"
- **Cause**: Database connection not configured or environment variables missing
- **Fix**: Set `DB_PASSWORD` environment variable with your MySQL password

### Compilation Errors
- **Cause**: Package naming mismatch (already fixed)
- **Status**: ✅ All resolved

### Mail/Email Features Not Working
- **Cause**: `MAIL_PASSWORD` not set or Gmail security settings
- **Fix**: 
  1. Set `MAIL_PASSWORD` environment variable
  2. Use Gmail App Password (not regular password)
  3. Enable "Less secure app access" if needed

### Chatbot Not Working
- **Cause**: `GEMINI_API_KEY` not set
- **Fix**: Get API key from Google AI Studio and set environment variable

## 📝 Notes

- The application connects to a cloud MySQL database (Aiven)
- CORS is configured for ports 3000 and 5173 (frontend)
- JWT authentication is required for most endpoints except `/api/auth/**`
- Profile images are stored via Cloudinary URLs
