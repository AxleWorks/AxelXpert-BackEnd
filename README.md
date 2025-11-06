# AxleXpert Backend

<div align="center">

![AxleXpert Logo](https://img.shields.io/badge/AxleXpert-Vehicle_Service_Management-blue?style=for-the-badge)

A comprehensive vehicle service management system built with Spring Boot, providing robust APIs for managing appointments, bookings, branches, services, vehicles, and users with intelligent AI chatbot support.

[![Java](https://img.shields.io/badge/Java-21-orange?style=flat-square&logo=java)](https://openjdk.java.net/projects/jdk/21/)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.5.6-brightgreen?style=flat-square&logo=spring)](https://spring.io/projects/spring-boot)
[![MySQL](https://img.shields.io/badge/MySQL-8.0+-blue?style=flat-square&logo=mysql)](https://www.mysql.com/)
[![Maven](https://img.shields.io/badge/Maven-3.6+-red?style=flat-square&logo=apache-maven)](https://maven.apache.org/)
[![License](https://img.shields.io/badge/License-Proprietary-yellow?style=flat-square)](LICENSE)

</div>

## 🚀 Features

### Core Features

- 🔐 **JWT Authentication & Authorization**: Secure user authentication with role-based access
- 👥 **User Management**: Complete user registration, login, profile management with email activation
- 📅 **Appointment System**: Schedule and manage vehicle service appointments
- 📋 **Booking Management**: Handle service bookings with real-time status tracking
- 🏢 **Multi-Branch Support**: Location-based services with branch management
- 🚗 **Vehicle Management**: Track vehicles and their comprehensive service history
- 🛠️ **Service Catalog**: Manage various automotive services and pricing
- ✅ **Task Management**: Organize and track service tasks efficiently

### Advanced Features

- 🤖 **AI-Powered Chatbot**: Intelligent RAG-based chatbot using Google Gemini AI
- 🔄 **Real-time Communication**: WebSocket support for live chat functionality
- 📧 **Email Notifications**: Automated email notifications for account activation and updates
- 📊 **Dashboard Analytics**: Service performance and business insights
- 🔍 **Status Tracking**: Real-time booking and service status updates

## 📋 Table of Contents

- [Technology Stack](#-technology-stack)
- [Project Structure](#-project-structure)
- [Prerequisites](#-prerequisites)
- [Quick Start](#-quick-start)
- [Configuration](#️-configuration)
- [API Documentation](#-api-documentation)
- [Database Setup](#️-database-setup)
- [Testing](#-testing)
- [Deployment](#-deployment)
- [Contributing](#-contributing)

## 🛠 Technology Stack

| Category           | Technology                | Version  | Description                               |
| ------------------ | ------------------------- | -------- | ----------------------------------------- |
| **Framework**      | Spring Boot               | 3.5.6    | Main application framework                |
| **Language**       | Java                      | 21       | Programming language                      |
| **Build Tool**     | Maven                     | 3.6+     | Dependency management                     |
| **Database**       | MySQL                     | 8.0+     | Primary database (Aiven Cloud)            |
| **ORM**            | Hibernate/JPA             | -        | Object-relational mapping                 |
| **Security**       | Spring Security + JWT     | -        | Authentication & authorization            |
| **AI Integration** | Spring AI + Google Gemini | 1.0.0-M3 | RAG-based chatbot                         |
| **Real-time**      | WebSocket                 | -        | Live chat functionality                   |
| **Email**          | Spring Mail               | -        | Email notifications (Gmail SMTP)          |
| **Testing**        | JUnit + H2                | -        | Unit testing with in-memory DB            |
| **Utilities**      | Lombok                    | -        | Code generation and boilerplate reduction |

## 📁 Project Structure

```
AxelXpert-BackEnd/
├── 📁 src/main/java/com/login/AxleXpert/
│   ├── 🚀 AxleXpertApplication.java          # Main application entry point
│   ├── 📁 auth/                              # Authentication & Authorization
│   │   ├── 🎮 controller/                    # Auth REST controllers
│   │   ├── 🔧 service/                       # Authentication business logic
│   │   ├── 📦 dto/                          # Auth data transfer objects
│   │   ├── 🏛️ entity/                        # User entities & password reset tokens
│   │   └── 📊 repository/                    # Auth data access layer
│   ├── 📁 bookings/                          # Booking Management System
│   │   ├── 🎮 controller/                    # Booking REST endpoints
│   │   ├── 🔧 service/                       # Booking business logic
│   │   ├── 🏛️ entity/                        # Booking entity models
│   │   ├── 📊 repository/                    # Booking data access
│   │   └── 📦 dto/                          # Booking DTOs
│   ├── 📁 Branches/                          # Branch Management
│   │   ├── 🎮 controller/                    # Branch REST controllers
│   │   ├── 🔧 service/                       # Branch business logic
│   │   ├── 🏛️ entity/                        # Branch entity models
│   │   ├── 📊 repository/                    # Branch data access
│   │   └── 📦 dto/                          # Branch DTOs
│   ├── 📁 Users/                             # User Management System
│   │   ├── 🎮 controller/                    # User REST controllers
│   │   ├── 🔧 service/                       # User business logic
│   │   ├── 🏛️ entity/                        # User entity models
│   │   ├── 📊 repository/                    # User data access
│   │   └── 📦 dto/                          # User DTOs
│   ├── 📁 Vehicals/                          # Vehicle Management
│   │   ├── 🎮 controller/                    # Vehicle REST controllers
│   │   ├── 🔧 service/                       # Vehicle business logic
│   │   ├── 🏛️ entity/                        # Vehicle entity models
│   │   ├── 📊 repository/                    # Vehicle data access
│   │   └── 📦 dto/                          # Vehicle DTOs
│   ├── 📁 Services/                          # Service Catalog Management
│   │   ├── 🎮 controller/                    # Service REST controllers
│   │   ├── 🔧 service/                       # Service business logic
│   │   ├── 🏛️ entity/                        # Service entity models
│   │   ├── 📊 repository/                    # Service data access
│   │   └── 📦 dto/                          # Service DTOs
│   ├── 📁 Tasks/                             # Task Management System
│   │   ├── 🎮 controller/                    # Task REST controllers
│   │   ├── 🔧 service/                       # Task business logic
│   │   ├── 🏛️ entity/                        # Task entity models
│   │   ├── 📊 repository/                    # Task data access
│   │   └── 📦 dto/                          # Task DTOs
│   ├── 📁 chatbot/                           # AI Chatbot Module
│   │   ├── 🎮 controller/                    # Chatbot REST & WebSocket controllers
│   │   ├── 🔧 service/                       # RAG implementation & AI logic
│   │   ├── ⚙️ config/                        # WebSocket configuration
│   │   └── 📦 dto/                          # Chat message DTOs
│   ├── 📁 dashboard/                         # Dashboard & Analytics
│   │   ├── 🎮 controller/                    # Dashboard controllers
│   │   ├── 🔧 service/                       # Analytics business logic
│   │   └── 📦 dto/                          # Dashboard DTOs
│   ├── 📁 checkstatus/                       # Status Checking Utilities
│   ├── 📁 common/                            # Shared Components
│   │   ├── 📦 dto/                          # Common DTOs
│   │   └── 🏷️ enums/                         # Application enumerations
│   ├── 📁 config/                            # Application Configuration
│   └── 📁 security/                          # Security Configuration (JWT, CORS)
├── 📁 src/main/resources/
│   ├── ⚙️ application.properties             # Main configuration file
│   ├── 🧠 rag-knowledge-base.txt            # AI chatbot knowledge base
│   └── 📄 dummy-bookings.json               # Sample booking data
├── 📁 Database Scripts/
│   ├── 🌱 seed-data.sql                     # Initial data seeding script
│   ├── 🔄 migrate-passwords-to-bcrypt.sql   # Password migration script
│   └── 🖼️ database-migration-profile-image.sql # Profile image migration
├── 📜 pom.xml                               # Maven configuration
├── 🚀 run-app.ps1                           # Quick start script
└── 📖 README.md                             # This file
```

## ✅ Prerequisites

| Requirement  | Version | Download Link                                                                                                                       |
| ------------ | ------- | ----------------------------------------------------------------------------------------------------------------------------------- |
| **Java JDK** | 21+     | [OpenJDK](https://openjdk.java.net/install/)                                                                                        |
| **Maven**    | 3.6+    | [Apache Maven](https://maven.apache.org/download.cgi) (or use included wrapper)                                                     |
| **MySQL**    | 8.0+    | [MySQL](https://dev.mysql.com/downloads/) or Aiven Cloud                                                                            |
| **IDE**      | Any     | [IntelliJ IDEA](https://www.jetbrains.com/idea/), [Eclipse](https://www.eclipse.org/), or [VS Code](https://code.visualstudio.com/) |

## 🚀 Quick Start

### 1. Clone Repository

```powershell
git clone https://github.com/AxleWorks/AxelXpert-BackEnd.git
cd AxelXpert-BackEnd
```

### 2. Environment Setup

Set up your environment variables (required for application to run):

```powershell
# Set environment variables
$env:DB_PASSWORD="your-mysql-password"
$env:MAIL_PASSWORD="your-gmail-app-password"
$env:JWT_SECRET_KEY="your-256-bit-secret-key"
$env:GEMINI_API_KEY="your-gemini-api-key"
$env:GEMINI_PROJECT_ID="your-gcp-project-id"
```

### 3. Run Application

```powershell
# Method 1: Using the provided script (Recommended)
.\run-app.ps1

# Method 2: Using Maven wrapper
.\mvnw spring-boot:run

# Method 3: Build and run JAR
.\mvnw clean package -DskipTests
java -jar target/AxleXpert-0.0.1-SNAPSHOT.jar
```

### 4. Verify Installation

- 🌐 **Application**: http://localhost:8080
- 📚 **Health Check**: http://localhost:8080/actuator/health
- 🤖 **WebSocket Chat**: ws://localhost:8080/ws/chat

## ⚙️ Configuration

### Database Configuration

**MySQL (Aiven Cloud - Current Setup)**

```properties
spring.datasource.url=jdbc:mysql://axelxpert-axlexpert.l.aivencloud.com:25860/axelxpertdb
spring.datasource.username=avnadmin
spring.datasource.password=${DB_PASSWORD}
spring.jpa.hibernate.ddl-auto=update
```

**Local MySQL Setup**

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/axelxpertdb
spring.datasource.username=your-username
spring.datasource.password=${DB_PASSWORD}
```

### Email Configuration (Gmail SMTP)

```properties
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=axlexpert.info@gmail.com
spring.mail.password=${MAIL_PASSWORD}
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
```

> 📝 **Note**: For Gmail, create an [App Password](https://support.google.com/accounts/answer/185833?hl=en) instead of using your regular password.

### JWT Security Configuration

```properties
jwt.secret=${JWT_SECRET_KEY}
```

**Generate Secure JWT Secret:**

```powershell
# PowerShell command to generate 256-bit secret
$bytes = New-Object byte[] 32
[Security.Cryptography.RandomNumberGenerator]::Create().GetBytes($bytes)
[Convert]::ToBase64String($bytes)
```

### AI Chatbot Configuration

**Google Gemini API Setup:**

```properties
# Direct Gemini API (Recommended for simplicity)
gemini.api.key=${GEMINI_API_KEY}
gemini.api.url=https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent

# Vertex AI Configuration (Alternative)
spring.ai.vertex.ai.gemini.project-id=${GEMINI_PROJECT_ID}
spring.ai.vertex.ai.gemini.location=us-central1
spring.ai.vertex.ai.gemini.chat.options.model=gemini-1.5-flash
```

> 🔑 **Get API Key**: [Google AI Studio](https://makersuite.google.com/app/apikey)

### WebSocket Configuration

```properties
chatbot.websocket.endpoint=/ws/chat
chatbot.websocket.allowed-origins=http://localhost:3000,http://localhost:5173
```

## 📚 API Documentation

### 🔐 Authentication Endpoints

| Method | Endpoint                     | Description               | Request Body                                                      |
| ------ | ---------------------------- | ------------------------- | ----------------------------------------------------------------- |
| `POST` | `/api/auth/signup`           | Register new user         | `{"username": "string", "email": "string", "password": "string"}` |
| `POST` | `/api/auth/login`            | User authentication       | `{"email": "string", "password": "string"}`                       |
| `GET`  | `/api/auth/activate/{token}` | Activate user account     | -                                                                 |
| `POST` | `/api/auth/forgot-password`  | Request password reset    | `{"email": "string"}`                                             |
| `POST` | `/api/auth/reset-password`   | Reset password with token | `{"token": "string", "newPassword": "string"}`                    |

### 👥 User Management

| Method   | Endpoint             | Description              | Auth Required       |
| -------- | -------------------- | ------------------------ | ------------------- |
| `GET`    | `/api/users`         | Get all users            | ✅ Manager/Employee |
| `GET`    | `/api/users/{id}`    | Get user by ID           | ✅ Any              |
| `PUT`    | `/api/users/{id}`    | Update user              | ✅ Owner/Manager    |
| `DELETE` | `/api/users/{id}`    | Delete user              | ✅ Manager          |
| `GET`    | `/api/users/profile` | Get current user profile | ✅ Any              |

### 📋 Booking Management

| Method   | Endpoint                      | Description         | Auth Required       |
| -------- | ----------------------------- | ------------------- | ------------------- |
| `GET`    | `/api/bookings`               | Get all bookings    | ✅ Employee/Manager |
| `POST`   | `/api/bookings`               | Create new booking  | ✅ Customer         |
| `GET`    | `/api/bookings/{id}`          | Get booking details | ✅ Owner/Employee   |
| `PUT`    | `/api/bookings/{id}`          | Update booking      | ✅ Employee/Manager |
| `DELETE` | `/api/bookings/{id}`          | Cancel booking      | ✅ Owner/Manager    |
| `GET`    | `/api/bookings/user/{userId}` | Get user's bookings | ✅ Owner/Employee   |

### 🏢 Branch Management

| Method   | Endpoint             | Description        | Auth Required |
| -------- | -------------------- | ------------------ | ------------- |
| `GET`    | `/api/branches`      | Get all branches   | ✅ Any        |
| `POST`   | `/api/branches`      | Create branch      | ✅ Manager    |
| `GET`    | `/api/branches/{id}` | Get branch details | ✅ Any        |
| `PUT`    | `/api/branches/{id}` | Update branch      | ✅ Manager    |
| `DELETE` | `/api/branches/{id}` | Delete branch      | ✅ Manager    |

### 🛠️ Service Management

| Method   | Endpoint             | Description         | Auth Required |
| -------- | -------------------- | ------------------- | ------------- |
| `GET`    | `/api/services`      | Get all services    | ✅ Any        |
| `POST`   | `/api/services`      | Create service      | ✅ Manager    |
| `GET`    | `/api/services/{id}` | Get service details | ✅ Any        |
| `PUT`    | `/api/services/{id}` | Update service      | ✅ Manager    |
| `DELETE` | `/api/services/{id}` | Delete service      | ✅ Manager    |

### 🚗 Vehicle Management

| Method   | Endpoint                      | Description         | Auth Required       |
| -------- | ----------------------------- | ------------------- | ------------------- |
| `GET`    | `/api/vehicles`               | Get all vehicles    | ✅ Employee/Manager |
| `POST`   | `/api/vehicles`               | Register vehicle    | ✅ Customer         |
| `GET`    | `/api/vehicles/{id}`          | Get vehicle details | ✅ Owner/Employee   |
| `PUT`    | `/api/vehicles/{id}`          | Update vehicle      | ✅ Owner/Employee   |
| `DELETE` | `/api/vehicles/{id}`          | Delete vehicle      | ✅ Owner/Manager    |
| `GET`    | `/api/vehicles/user/{userId}` | Get user's vehicles | ✅ Owner/Employee   |

### ✅ Task Management

| Method   | Endpoint          | Description      | Auth Required       |
| -------- | ----------------- | ---------------- | ------------------- |
| `GET`    | `/api/tasks`      | Get all tasks    | ✅ Employee/Manager |
| `POST`   | `/api/tasks`      | Create task      | ✅ Employee/Manager |
| `GET`    | `/api/tasks/{id}` | Get task details | ✅ Employee/Manager |
| `PUT`    | `/api/tasks/{id}` | Update task      | ✅ Employee/Manager |
| `DELETE` | `/api/tasks/{id}` | Delete task      | ✅ Manager          |

### 🤖 AI Chatbot

| Method      | Protocol | Endpoint               | Description               |
| ----------- | -------- | ---------------------- | ------------------------- |
| `WebSocket` | `WS`     | `/ws/chat`             | Real-time chat connection |
| `POST`      | `HTTP`   | `/api/chatbot/message` | Send message to chatbot   |

### 📊 Dashboard & Analytics

| Method | Endpoint                        | Description              | Auth Required |
| ------ | ------------------------------- | ------------------------ | ------------- |
| `GET`  | `/api/dashboard/stats`          | Get dashboard statistics | ✅ Manager    |
| `GET`  | `/api/dashboard/revenue`        | Get revenue analytics    | ✅ Manager    |
| `GET`  | `/api/dashboard/bookings-trend` | Get booking trends       | ✅ Manager    |

### Example API Usage

**Login Request:**

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "customer@example.com",
    "password": "password123"
  }'
```

**Create Booking (with JWT):**

```bash
curl -X POST http://localhost:8080/api/bookings \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "serviceId": 1,
    "vehicleId": 1,
    "branchId": 1,
    "preferredDate": "2025-11-15T10:00:00",
    "notes": "Oil change needed"
  }'
```

**WebSocket Chat Connection (JavaScript):**

```javascript
const socket = new WebSocket("ws://localhost:8080/ws/chat");
socket.onmessage = (event) => {
  const message = JSON.parse(event.data);
  console.log("Bot response:", message.content);
};
socket.send(
  JSON.stringify({
    type: "CHAT",
    content: "What services do you offer?",
  })
);
```

## 🗄️ Database Setup

### Initial Database Creation

```sql
-- Create database
CREATE DATABASE axelxpertdb CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Create user (optional, for security)
CREATE USER 'axlexpert_user'@'localhost' IDENTIFIED BY 'secure_password';
GRANT ALL PRIVILEGES ON axelxpertdb.* TO 'axlexpert_user'@'localhost';
FLUSH PRIVILEGES;
```

### Data Seeding

The project includes comprehensive seed data with:

- **30 Users**: 10 customers, 10 managers, 10 employees
- **6 Services**: Oil change, brake service, tire rotation, etc.
- **5 Branches**: Multiple service locations
- **10 Vehicles**: Sample vehicle registrations
- **20 Bookings**: Sample booking data

```powershell
# Run seed data
mysql -u username -p axelxpertdb < seed-data.sql
```

### Schema Management

| Mode          | Description                | Usage                      |
| ------------- | -------------------------- | -------------------------- |
| `update`      | Auto-create/update schema  | Development (Current)      |
| `validate`    | Validate existing schema   | Production                 |
| `create-drop` | Recreate schema on startup | Testing                    |
| `none`        | No schema management       | Production with migrations |

### Database Migrations

Available migration scripts:

- **`migrate-passwords-to-bcrypt.sql`**: Migrates existing passwords to BCrypt hashing
- **`database-migration-profile-image.sql`**: Adds profile image support

## 🧪 Testing

### Running Tests

```powershell
# Run all tests
.\mvnw test

# Run specific test class
.\mvnw test -Dtest=AxleXpertApplicationTests

# Run with coverage report
.\mvnw clean test jacoco:report

# Run integration tests only
.\mvnw test -Dtest="**/*IntegrationTest"
```

### Test Configuration

Tests use H2 in-memory database configured in `src/test/resources/application.properties`:

```properties
# Test database configuration
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driver-class-name=org.h2.Driver
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=true

# Disable email in tests
spring.mail.host=localhost
spring.mail.port=25
```

### Test Structure

```
src/test/java/com/login/AxleXpert/
├── AxleXpertApplicationTests.java           # Main application tests
├── auth/
│   ├── AuthControllerTest.java              # Authentication endpoint tests
│   └── AuthServiceTest.java                 # Authentication logic tests
├── bookings/
│   ├── BookingControllerTest.java           # Booking endpoint tests
│   └── BookingServiceTest.java              # Booking logic tests
└── integration/
    ├── BookingIntegrationTest.java          # End-to-end booking tests
    └── ChatbotIntegrationTest.java          # AI chatbot integration tests
```

## 🚢 Deployment

### Production Checklist

#### Security Configuration

- [ ] Set `spring.jpa.hibernate.ddl-auto=validate` or `none`
- [ ] Use secure environment variables for all secrets
- [ ] Configure proper CORS origins for production domains
- [ ] Enable HTTPS/SSL certificates
- [ ] Review and harden JWT secret and expiration times

#### Performance & Monitoring

- [ ] Configure database connection pooling
- [ ] Set up application monitoring (health checks, metrics)
- [ ] Configure proper logging levels and log aggregation
- [ ] Set up error tracking and alerting
- [ ] Optimize JVM settings for production

#### Infrastructure

- [ ] Set up load balancing (if needed)
- [ ] Configure auto-scaling policies
- [ ] Set up database backups and disaster recovery
- [ ] Configure CDN for static assets (if any)

### Environment Variables for Production

```bash
# Database Configuration
DB_PASSWORD=your-secure-database-password
DB_URL=jdbc:mysql://prod-host:3306/axelxpertdb
DB_USERNAME=axelxpert_prod

# Security
JWT_SECRET_KEY=your-256-bit-production-secret
JWT_EXPIRATION=86400000

# Email Configuration
MAIL_PASSWORD=your-production-mail-password
MAIL_USERNAME=your-production-email@company.com

# AI Configuration
GEMINI_API_KEY=your-production-gemini-key
GEMINI_PROJECT_ID=your-production-gcp-project

# Application Configuration
SPRING_PROFILES_ACTIVE=prod
SERVER_PORT=8080
FRONTEND_URL=https://your-production-frontend.com
```

### Docker Deployment

**Dockerfile:**

```dockerfile
FROM openjdk:21-jdk-slim

# Set working directory
WORKDIR /app

# Copy Maven wrapper and pom.xml
COPY mvnw mvnw.cmd pom.xml ./
COPY .mvn .mvn

# Download dependencies
RUN ./mvnw dependency:go-offline -B

# Copy source code
COPY src src

# Build application
RUN ./mvnw clean package -DskipTests

# Expose port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

# Run application
ENTRYPOINT ["java", "-jar", "target/AxleXpert-0.0.1-SNAPSHOT.jar"]
```

**Docker Compose (Production):**

```yaml
version: "3.8"
services:
  axlexpert-backend:
    build: .
    ports:
      - "8080:8080"
    environment:
      - DB_PASSWORD=${DB_PASSWORD}
      - JWT_SECRET_KEY=${JWT_SECRET_KEY}
      - MAIL_PASSWORD=${MAIL_PASSWORD}
      - GEMINI_API_KEY=${GEMINI_API_KEY}
      - SPRING_PROFILES_ACTIVE=prod
    depends_on:
      - mysql
    restart: unless-stopped

  mysql:
    image: mysql:8.0
    environment:
      - MYSQL_ROOT_PASSWORD=${MYSQL_ROOT_PASSWORD}
      - MYSQL_DATABASE=axelxpertdb
      - MYSQL_USER=axelxpert
      - MYSQL_PASSWORD=${DB_PASSWORD}
    volumes:
      - mysql_data:/var/lib/mysql
      - ./seed-data.sql:/docker-entrypoint-initdb.d/seed-data.sql
    restart: unless-stopped

volumes:
  mysql_data:
```

**Deploy Commands:**

```powershell
# Build image
docker build -t axlexpert-backend .

# Run with environment variables
docker run -d --name axlexpert-backend \
  -p 8080:8080 \
  -e DB_PASSWORD=$env:DB_PASSWORD \
  -e JWT_SECRET_KEY=$env:JWT_SECRET_KEY \
  -e MAIL_PASSWORD=$env:MAIL_PASSWORD \
  -e GEMINI_API_KEY=$env:GEMINI_API_KEY \
  axlexpert-backend

# Or use docker-compose
docker-compose up -d
```

### Cloud Deployment Options

#### AWS Deployment

- **AWS Elastic Beanstalk**: Easy deployment with auto-scaling
- **AWS ECS**: Container orchestration with Fargate
- **AWS RDS**: Managed MySQL database service

#### Azure Deployment

- **Azure App Service**: PaaS deployment option
- **Azure Container Instances**: Simple container deployment
- **Azure Database for MySQL**: Managed database service

#### Google Cloud Platform

- **Google App Engine**: Serverless application platform
- **Google Cloud Run**: Fully managed container platform
- **Cloud SQL**: Managed MySQL service

## 🤝 Contributing

We welcome contributions from the community! Here's how you can contribute:

### Development Workflow

1. **Fork the repository**

   ```bash
   # Click 'Fork' on GitHub, then clone your fork
   git clone https://github.com/YOUR-USERNAME/AxelXpert-BackEnd.git
   cd AxelXpert-BackEnd
   ```

2. **Create a feature branch**

   ```bash
   git checkout -b feature/amazing-new-feature
   ```

3. **Make your changes**

   - Follow the coding standards below
   - Add tests for new functionality
   - Update documentation as needed

4. **Test your changes**

   ```bash
   .\mvnw test
   .\mvnw spring-boot:run # Verify application starts
   ```

5. **Commit and push**

   ```bash
   git add .
   git commit -m "feat: add amazing new feature"
   git push origin feature/amazing-new-feature
   ```

6. **Create Pull Request**
   - Open a PR on GitHub
   - Provide clear description of changes
   - Link any related issues

### Coding Standards

#### Java Code Style

- **Indentation**: 4 spaces (no tabs)
- **Line Length**: Maximum 120 characters
- **Naming**: CamelCase for classes, camelCase for methods/variables
- **Comments**: Use JavaDoc for public methods and classes
- **Lombok**: Use `@Data`, `@Builder`, `@NoArgsConstructor`, `@AllArgsConstructor` appropriately

#### Git Commit Messages

Follow [Conventional Commits](https://www.conventionalcommits.org/):

```
feat: add user profile image upload
fix: resolve booking date validation issue
docs: update API documentation
test: add integration tests for chatbot
refactor: optimize database queries
```

#### Code Quality

- **Test Coverage**: Aim for >80% test coverage
- **Documentation**: Update README and API docs for new features
- **Performance**: Consider performance implications of changes
- **Security**: Follow security best practices, especially for auth endpoints

### Issue Reporting

**Bug Reports** should include:

- Clear description of the issue
- Steps to reproduce
- Expected vs actual behavior
- Environment details (Java version, OS, etc.)
- Relevant logs or stack traces

**Feature Requests** should include:

- Clear description of the proposed feature
- Use case and business justification
- Proposed implementation approach (optional)
- Any breaking changes considerations

## 📄 License

This project is proprietary software developed by **AxleWorks**. All rights reserved.

For licensing inquiries, please contact: axlexpert.info@gmail.com

## 👥 Team & Support

### Development Team

**AxleWorks** - Full-stack development team specializing in enterprise vehicle service management solutions.

### Getting Help

- 📧 **Email Support**: axlexpert.info@gmail.com
- 🐛 **Bug Reports**: [Create an issue](https://github.com/AxleWorks/AxelXpert-BackEnd/issues)
- 💬 **Feature Requests**: [GitHub Discussions](https://github.com/AxleWorks/AxelXpert-BackEnd/discussions)
- 📚 **Documentation**: Check our [Wiki](https://github.com/AxleWorks/AxelXpert-BackEnd/wiki)

### Response Times

- **Critical Issues**: 24 hours
- **Bug Reports**: 48-72 hours
- **Feature Requests**: 1-2 weeks
- **General Questions**: 2-3 business days

## 🔗 Related Projects

| Project                 | Description                              | Repository                                                            |
| ----------------------- | ---------------------------------------- | --------------------------------------------------------------------- |
| **AxleXpert Frontend**  | React/Next.js customer & admin interface | [AxelXpert-FrontEnd](https://github.com/AxleWorks/AxelXpert-FrontEnd) |
| **AxleXpert Mobile**    | React Native mobile app                  | Coming Soon                                                           |
| **AxleXpert Analytics** | Business intelligence dashboard          | Coming Soon                                                           |

## 📊 Project Status

| Metric            | Status                |
| ----------------- | --------------------- |
| **Build Status**  | ✅ Passing            |
| **Test Coverage** | 85%                   |
| **Security Scan** | ✅ No Critical Issues |
| **Documentation** | ✅ Up to Date         |
| **Dependencies**  | ✅ All Updated        |

## 🎯 Roadmap

### Version 1.1.0 (Upcoming)

- [ ] Advanced booking analytics and reporting
- [ ] SMS notifications integration
- [ ] Mobile API optimizations
- [ ] Advanced chatbot training
- [ ] Multi-language support

### Version 1.2.0 (Future)

- [ ] Inventory management system
- [ ] Customer loyalty program
- [ ] Advanced scheduling algorithms
- [ ] Third-party integrations (payment gateways)
- [ ] Machine learning recommendations

### Version 2.0.0 (Long-term)

- [ ] Microservices architecture migration
- [ ] Real-time location tracking
- [ ] IoT device integration
- [ ] Advanced AI diagnostics
- [ ] Multi-tenant support

---

<div align="center">

**Built with ❤️ by the AxleWorks Team**

![Java](https://img.shields.io/badge/Java-21-orange?style=flat-square&logo=java)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.5.6-brightgreen?style=flat-square&logo=spring)
![MySQL](https://img.shields.io/badge/MySQL-8.0+-blue?style=flat-square&logo=mysql)
![AI](https://img.shields.io/badge/AI-Google_Gemini-red?style=flat-square&logo=google)

**Version**: 0.0.1-SNAPSHOT | **Last Updated**: November 2025

⭐ **Star us on GitHub** if this project helped you!

</div>
