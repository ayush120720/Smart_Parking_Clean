# ---- Build Stage ----
FROM eclipse-temurin:17-jdk-jammy AS build
WORKDIR /app

# Copy Maven wrapper and project files
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
RUN ./mvnw dependency:go-offline

# Copy source code
COPY src src

# Build the application
RUN ./mvnw -B package -DskipTests

# ---- Run Stage ----
FROM eclipse-temurin:17-jdk-jammy
WORKDIR /app

# Copy the jar from the build stage
COPY --from=build /app/target/*.jar app.jar

# Expose port (Render injects $PORT)
EXPOSE 8080

CMD ["java", "-jar", "app.jar"]
