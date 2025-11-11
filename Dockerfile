# Multi-stage build for AxleXpert Spring Boot Application

# Stage 1: Build the application
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# Copy pom.xml and download dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code and build
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Create the runtime image
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Copy the built JAR from the build stage
COPY --from=build /app/target/AxleXpert-*.jar app.jar

# Expose the application port
EXPOSE 8080

# Set environment variables (these will be overridden by .env or docker-compose)
ENV JWT_SECRET_KEY=""
ENV DB_PASSWORD=""
ENV MAIL_PASSWORD=""
ENV GEMINI_API_KEY=""

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
