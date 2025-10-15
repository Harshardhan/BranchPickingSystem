# First stage: Build the application
FROM maven:3.9.6-eclipse-temurin-17 as builder

WORKDIR /app
COPY . .
RUN mvnw clean install-DskipTests

# Second stage: Run the application
FROM openjdk:17-jdk-slim

WORKDIR /app
COPY --from=builder /app/target/BranchPickingSystem-0.0.1-SNAPSHOT.jar BranchPickingSystem.jar


ENTRYPOINT ["java", "-jar", "BranchPickingSystem.jar"]