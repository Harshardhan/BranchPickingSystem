# Build Stage - uses Maven + JDK
FROM maven:3.9.6-eclipse-temurin-17-alpine AS build

WORKDIR /app
COPY . .

# 🔽 Pre-fetch all dependencies (go offline) before build
RUN mvn dependency:go-offline -B

# 🔽 Then build the application
RUN mvn clean install -DskipTests

# Run Stage - uses lightweight JRE only
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

RUN apk add --no-cache curl
RUN mkdir -p logs

# Copy the JAR from the build stage (ensure correct path)
COPY --from=build /app/EurekaServerApp/target/EurekaServerApp-0.0.1-SNAPSHOT.jar EurekaServerApp.jar

EXPOSE 8761

ENV SPRING_PROFILES_ACTIVE=dev

ENTRYPOINT ["java", "-jar", "EurekaServerApp.jar"]