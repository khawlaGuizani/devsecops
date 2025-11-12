# Multi-stage build for Spring Boot application

# Stage 1: Build stage
FROM maven:3.8.4-openjdk-8-slim AS build

# Set working directory
WORKDIR /app

# Copy pom.xml and download dependencies (cached layer)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code
COPY src ./src

# Build the application
RUN mvn clean package -DskipTests

# Stage 2: Runtime stage
FROM openjdk:8-jre-slim

# Set working directory
WORKDIR /app

# Create directory for logs
RUN mkdir -p /app/logs

# Copy the JAR file from build stage
COPY --from=build /app/target/timesheet-devops-1.0.jar app.jar

# Expose the application port
EXPOSE 8082

# Set JVM options for better container performance
ENV JAVA_OPTS="-Xmx512m -Xms256m"

# Run the application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]

