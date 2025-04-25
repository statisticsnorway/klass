# Use OpenJDK 17 as the base image
FROM eclipse-temurin:17-jre

# Set the working directory inside the container
WORKDIR /app

# Copy the WAR file from the host to the container
COPY ./target/*.jar app.jar

# Expose the port the app will run on (default Spring Boot port is 8080)
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
