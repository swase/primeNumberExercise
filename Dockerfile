# ---- Stage 1: Build the application ----
FROM eclipse-temurin:17-jdk-alpine AS build

# Set work directory
WORKDIR /app

# Copy Maven wrapper & project files
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Download dependencies (cached layer)
RUN ./mvnw dependency:go-offline

# Copy the rest of the project
COPY src src

# Build the Spring Boot fat jar (skip tests for faster build)
RUN ./mvnw clean package -DskipTests

# ---- Stage 2: Run the application ----
#FROM eclipse-temurin:17-jdk-alpine
FROM eclipse-temurin:17-jre-alpine

# Set work directory
WORKDIR /app

# Copy jar from build stage
COPY --from=build /app/target/*.jar app.jar

# Expose the port your app runs on (change if different)
EXPOSE 8081

# Run the jar
ENTRYPOINT ["java","-jar","app.jar"]
