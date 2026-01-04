# Stage 1: Build the Application
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
# Build the jar file, skipping tests to save time
RUN mvn clean package -DskipTests

# Stage 2: Run the Application
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
# Copy the jar from Stage 1
COPY --from=build /app/target/*.jar app.jar
# Expose the port
EXPOSE 8080
# Run the app
ENTRYPOINT ["java", "-jar", "app.jar"]