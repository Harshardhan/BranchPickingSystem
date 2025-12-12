# Build Stage

FROM maven:3.9.6-eclipse-temurin-17-alpine AS build

WORKDIR /app
COPY . .
RUN mvn clean install -DskipTests

# Run Stage (optimized + tools)

FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Install essential debugging tools

RUN apk add --no-cache curl bash

# Create logs folder

RUN mkdir -p logs

# Set memory limits for JVM

ENV JAVA_OPTS="-Xms256m -Xmx512m"

# Allow dynamic Spring profile

ENV SPRING_PROFILES_ACTIVE=dev

# Copy built JAR from build stage

COPY --from=build /app/ProductServiceApplication/target/ProductServiceApplication-0.0.1-SNAPSHOT.jar ProductServiceApplication.jar

# Expose application port

EXPOSE 8082

# Entry point with JVM memory limits and dynamic profile

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Dspring.profiles.active=$SPRING_PROFILES_ACTIVE -jar ProductServiceApplication.jar"]
