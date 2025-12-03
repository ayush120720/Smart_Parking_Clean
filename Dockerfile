# Use official Java 17 JDK image
FROM eclipse-temurin:17-jdk-jammy

# Set work directory
WORKDIR /app

# Copy build jar from Maven target folder
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar

# Expose Render port
EXPOSE 8080

# Run the Spring Boot app
ENTRYPOINT ["java", "-jar", "app.jar"]
