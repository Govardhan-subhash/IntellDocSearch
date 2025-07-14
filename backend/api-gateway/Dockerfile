# Use a lightweight OpenJDK image
FROM eclipse-temurin:17-jdk-alpine

# Copy the fat jar to the container
COPY target/*.jar app.jar

# Expose API Gateway port
EXPOSE 8086

# Run the jar file
ENTRYPOINT ["java", "-jar", "/app.jar"]
