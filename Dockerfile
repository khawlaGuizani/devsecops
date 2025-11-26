# Multi-stage build for Spring Boot application

# =========================
# Stage 1: Build stage
# =========================
# Use a more recent JDK + Maven image
FROM maven:3.9-eclipse-temurin-17 AS build

# Set working directory
WORKDIR /app

# Copy pom.xml (dependency layer)
COPY pom.xml .

# (Optional) If you really want offline deps, uncomment this.
# It was the step causing trouble with your previous image.
# RUN mvn dependency:go-offline -B

# Copy source code
COPY src ./src

# Build the application
RUN mvn -B -DskipTests clean package

# =========================
# Stage 2: Runtime stage
# =========================
# Use matching runtime JRE version
FROM eclipse-temurin:17-jre-jammy

# Set working directory
WORKDIR /app

# Create directory for logs
RUN mkdir -p /app/logs

# Copy the JAR file from build stage (use wildcard to be resilient to version changes)
COPY --from=build /app/target/*.jar app.jar

# Expose the application port (matches server.port in application.properties)
EXPOSE 8082

# Metadata
LABEL maintainer="khawlaGuizani <devnull@example.com>"

# Set JVM options for better container performance
ENV JAVA_OPTS="-Xmx512m -Xms256m"

# Simple healthcheck (tries actuator health, falls back to root)
HEALTHCHECK --interval=30s --timeout=5s --start-period=10s --retries=3 \
	CMD wget -qO- --tries=1 --timeout=2 http://localhost:8082/actuator/health || wget -qO- --tries=1 --timeout=2 http://localhost:8082/ || exit 1

# Run the application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
