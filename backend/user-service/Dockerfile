# Use a lightweight OpenJDK image
FROM eclipse-temurin:17-jdk-alpine

# Copy the fat jar to the container
COPY target/*.jar app.jar

# Expose the port your service runs on
EXPOSE 8081

# Run the jar file
ENTRYPOINT ["java", "-jar", "/app.jar"]
