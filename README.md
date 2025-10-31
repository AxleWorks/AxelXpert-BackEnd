# AxleXpert Backend

A comprehensive vehicle service management system built with Spring Boot, providing robust APIs for managing appointments, bookings, branches, services, vehicles, and users with intelligent chatbot support.

## 🚀 Features

- **Authentication & Authorization**: JWT-based secure authentication system
- **User Management**: Complete user registration, login, and profile management
- **Appointment System**: Schedule and manage vehicle service appointments
- **Booking Management**: Handle service bookings with real-time status tracking
- **Branch Management**: Multi-branch support with location-based services
- **Vehicle Management**: Track vehicles and their service history
- **Service Catalog**: Manage various automotive services offered
- **Task Management**: Organize and track service tasks
- **AI Chatbot**: Intelligent RAG-based chatbot powered by Google Gemini
- **Real-time Communication**: WebSocket support for live chat
- **Email Notifications**: Automated email notifications for account activation and updates

## 📋 Table of Contents

- [Technology Stack](#technology-stack)
- [Project Structure](#project-structure)
- [Prerequisites](#prerequisites)
- [Installation](#installation)
- [Configuration](#configuration)
- [Running the Application](#running-the-application)
- [API Documentation](#api-documentation)
- [Database Setup](#database-setup)
- [Testing](#testing)
- [Deployment](#deployment)
- [Contributing](#contributing)

## 🛠 Technology Stack

- **Framework**: Spring Boot 3.5.6
- **Language**: Java 21
- **Build Tool**: Maven
- **Database**: MySQL (Aiven Cloud)
- **ORM**: Spring Data JPA / Hibernate
- **Security**: Spring Security + JWT
- **AI Integration**: Spring AI with Google Vertex AI Gemini
- **Real-time**: WebSocket (Spring WebSocket)
- **Email**: Spring Mail (Gmail SMTP)
- **Testing**: JUnit, Spring Boot Test, H2 (in-memory DB for tests)
- **Code Generation**: Lombok
- **API Client**: Spring WebFlux

## 📁 Project Structure

```
src/main/java/com/login/AxleXpert/
├── AxleXpertApplication.java          # Main application entry point
├── Appointments/                       # Appointment management module
├── auth/                              # Authentication & authorization
│   ├── controller/                    # Auth controllers (login, signup)
│   ├── service/                       # Auth business logic
│   └── dto/                          # Auth DTOs (LoginDTO, SignupDTO, etc.)
├── bookings/                          # Booking management
│   ├── controller/                    # Booking REST controllers
│   ├── service/                       # Booking business logic
│   ├── repository/                    # Booking data access
│   ├── entity/                        # Booking entity models
│   └── dto/                          # Booking data transfer objects
├── Branches/                          # Branch management
│   ├── controller/                    # Branch REST controllers
│   ├── service/                       # Branch business logic
│   ├── repository/                    # Branch data access
│   ├── entity/                        # Branch entity models
│   └── dto/                          # Branch DTOs
├── Services/                          # Service catalog management
│   ├── controller/                    # Service REST controllers
│   ├── service/                       # Service business logic
│   ├── repository/                    # Service data access
│   ├── entity/                        # Service entity models
│   └── dto/                          # Service DTOs
├── Users/                             # User management
│   ├── controller/                    # User REST controllers
│   ├── service/                       # User business logic
│   ├── repository/                    # User data access
│   ├── entity/                        # User entity models
│   └── dto/                          # User DTOs
├── Vehicals/                          # Vehicle management
│   ├── controller/                    # Vehicle REST controllers
│   ├── service/                       # Vehicle business logic
│   ├── repository/                    # Vehicle data access
│   ├── entity/                        # Vehicle entity models
│   └── dto/                          # Vehicle DTOs
├── Tasks/                             # Task management
│   ├── controller/                    # Task REST controllers
│   ├── service/                       # Task business logic
│   ├── repository/                    # Task data access
│   ├── entity/                        # Task entity models
│   └── dto/                          # Task DTOs
├── chatbot/                           # AI Chatbot module
│   ├── controller/                    # Chatbot controllers
│   ├── service/                       # RAG implementation & chatbot logic
│   ├── dto/                          # Chat message DTOs
│   └── config/                       # WebSocket configuration
├── checkstatus/                       # Status checking utilities
├── common/                            # Shared utilities
│   ├── dto/                          # Common DTOs
│   └── enums/                        # Common enumerations
├── config/                            # Application configurations
├── security/                          # Security configurations (JWT, CORS, etc.)
└── vehicles/                          # Additional vehicle-related features

src/main/resources/
├── application.properties             # Main configuration file
├── rag-knowledge-base.txt            # RAG chatbot knowledge base
└── dummy-bookings.json               # Sample booking data

Database Scripts:
├── seed-data.sql                     # Initial data seeding
├── migrate-passwords-to-bcrypt.sql   # Password migration script
└── database-migration-profile-image.sql # Profile image migration
```

## ✅ Prerequisites

- **Java**: JDK 21 or higher
- **Maven**: 3.6+ (or use included Maven wrapper)
- **MySQL**: 8.0+ (or Aiven Cloud MySQL)
- **IDE**: IntelliJ IDEA, Eclipse, or VS Code with Java extensions

## 📦 Installation

1. **Clone the repository**

   ```powershell
   git clone https://github.com/AxleWorks/AxelXpert-BackEnd.git
   cd AxelXpert-BackEnd
   ```

2. **Set up environment variables**

   Create a `.env` file or set environment variables:

   ```powershell
   $env:DB_PASSWORD="your-database-password"
   $env:MAIL_PASSWORD="your-gmail-app-password"
   $env:JWT_SECRET_KEY="your-secret-key-min-256-bits"
   $env:GEMINI_API_KEY="your-gemini-api-key"
   $env:GEMINI_PROJECT_ID="your-gcp-project-id"
   ```

   Or use the provided script:

   ```powershell
   # Copy the example file
   cp setup-env-example.ps1 setup-env.ps1
   # Edit setup-env.ps1 with your credentials
   # Run the script
   .\setup-env.ps1
   ```

3. **Install dependencies**
   ```powershell
   .\mvnw clean install
   ```

## ⚙️ Configuration

### Database Configuration

Update `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://your-host:port/database-name
spring.datasource.username=your-username
spring.datasource.password=${DB_PASSWORD}
spring.jpa.hibernate.ddl-auto=update
```

### Email Configuration

Configure SMTP settings for email notifications:

```properties
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=your-email@gmail.com
spring.mail.password=${MAIL_PASSWORD}
```

**Note**: For Gmail, create an [App Password](https://support.google.com/accounts/answer/185833).

### JWT Configuration

```properties
jwt.secret=${JWT_SECRET_KEY}
```

Generate a secure key (minimum 256 bits):

```powershell
# PowerShell
$bytes = New-Object byte[] 32
[Security.Cryptography.RandomNumberGenerator]::Create().GetBytes($bytes)
[Convert]::ToBase64String($bytes)
```

### Gemini AI Configuration

For the RAG chatbot feature:

```properties
gemini.api.key=${GEMINI_API_KEY}
spring.ai.vertex.ai.gemini.project-id=${GEMINI_PROJECT_ID}
```

Get your API key from [Google AI Studio](https://makersuite.google.com/app/apikey).

### WebSocket Configuration

```properties
chatbot.websocket.endpoint=/ws/chat
chatbot.websocket.allowed-origins=http://localhost:3000,http://localhost:5173
```

## 🏃 Running the Application

### Using Maven Wrapper (Recommended)

```powershell
# Clean and compile
.\mvnw clean compile

# Run the application
.\mvnw spring-boot:run

# Or use the provided script
.\run-app.ps1
```

### Using IDE

1. Open the project in your IDE
2. Run `AxleXpertApplication.java` as a Java Application
3. The application will start on `http://localhost:8080`

### Build JAR

```powershell
.\mvnw clean package -DskipTests
java -jar target/AxleXpert-0.0.1-SNAPSHOT.jar
```

## 📚 API Documentation

### Authentication Endpoints

```
POST   /api/auth/signup          # Register new user
POST   /api/auth/login           # User login
GET    /api/auth/activate/{code} # Activate account
```

### User Management

```
GET    /api/users                # Get all users
GET    /api/users/{id}           # Get user by ID
PUT    /api/users/{id}           # Update user
DELETE /api/users/{id}           # Delete user
```

### Bookings

```
GET    /api/bookings             # Get all bookings
POST   /api/bookings             # Create booking
GET    /api/bookings/{id}        # Get booking by ID
PUT    /api/bookings/{id}        # Update booking
DELETE /api/bookings/{id}        # Delete booking
```

### Branches

```
GET    /api/branches             # Get all branches
POST   /api/branches             # Create branch
GET    /api/branches/{id}        # Get branch by ID
PUT    /api/branches/{id}        # Update branch
DELETE /api/branches/{id}        # Delete branch
```

### Services

```
GET    /api/services             # Get all services
POST   /api/services             # Create service
GET    /api/services/{id}        # Get service by ID
PUT    /api/services/{id}        # Update service
DELETE /api/services/{id}        # Delete service
```

### Vehicles

```
GET    /api/vehicles             # Get all vehicles
POST   /api/vehicles             # Create vehicle
GET    /api/vehicles/{id}        # Get vehicle by ID
PUT    /api/vehicles/{id}        # Update vehicle
DELETE /api/vehicles/{id}        # Delete vehicle
```

### Chatbot

```
WebSocket: /ws/chat               # Real-time chat connection
POST   /api/chatbot/message      # Send message to chatbot
```

### Example Request

```bash
# Login
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@example.com",
    "password": "password123"
  }'

# Get bookings (with JWT token)
curl -X GET http://localhost:8080/api/bookings \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

## 🗄️ Database Setup

### Initial Setup

1. **Create database**

   ```sql
   CREATE DATABASE axelxpertdb;
   ```

2. **Run seed data** (optional)
   ```powershell
   # Connect to MySQL and run
   mysql -u username -p axelxpertdb < seed-data.sql
   ```

### Migrations

- **Password Migration to BCrypt**: `migrate-passwords-to-bcrypt.sql`
- **Profile Image Migration**: `database-migration-profile-image.sql`

### Schema Management

The application uses `spring.jpa.hibernate.ddl-auto=update` to automatically manage schema updates. For production, consider using migration tools like Flyway or Liquibase.

## 🧪 Testing

### Run Tests

```powershell
# Run all tests
.\mvnw test

# Run specific test class
.\mvnw test -Dtest=AxleXpertApplicationTests

# Run with coverage
.\mvnw clean test jacoco:report
```

### Test Configuration

Tests use H2 in-memory database configured in `src/test/resources/application.properties`.

## 🚢 Deployment

### Production Checklist

- [ ] Set `spring.jpa.hibernate.ddl-auto=validate` or `none`
- [ ] Use secure environment variables for all secrets
- [ ] Configure proper CORS origins
- [ ] Enable HTTPS/SSL
- [ ] Set up proper logging configuration
- [ ] Configure database connection pooling
- [ ] Set up monitoring and health checks
- [ ] Review and harden security configurations

### Environment Variables for Production

```properties
DB_PASSWORD=<secure-database-password>
MAIL_PASSWORD=<secure-mail-password>
JWT_SECRET_KEY=<secure-jwt-secret>
GEMINI_API_KEY=<gemini-api-key>
SPRING_PROFILES_ACTIVE=prod
```

### Docker Deployment (Optional)

```dockerfile
FROM openjdk:21-jdk-slim
WORKDIR /app
COPY target/AxleXpert-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

```powershell
# Build
docker build -t axlexpert-backend .

# Run
docker run -p 8080:8080 \
  -e DB_PASSWORD=xxx \
  -e JWT_SECRET_KEY=xxx \
  axlexpert-backend
```

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

### Code Style

- Follow standard Java conventions
- Use Lombok annotations to reduce boilerplate
- Write meaningful commit messages
- Add tests for new features
- Update documentation as needed

## 📄 License

This project is proprietary software developed by AxleWorks.

## 👥 Team

Developed and maintained by the AxleWorks team.

## 📞 Support

For issues and questions:

- Email: axlexpert.info@gmail.com
- Create an issue in the repository

## 🔗 Related Repositories

- [AxleXpert Frontend](https://github.com/AxleWorks/AxelXpert-FrontEnd) - React/Next.js frontend application

## 📝 Additional Documentation

- [Task Management API Documentation](TASK_MANAGEMENT_API.md)
- [Password Migration Guide](PASSWORD-MIGRATION-README.md)

---

**Version**: 0.0.1-SNAPSHOT  
**Last Updated**: October 2025  
**Built with**: ☕ Java 21 & 🍃 Spring Boot 3.5.6
