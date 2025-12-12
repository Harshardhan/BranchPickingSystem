# Build Stage
FROM maven:3.9.6-eclipse-temurin-17-alpine AS build

WORKDIR /app

COPY . .

RUN mvn clean install -DskipTests

# Run Stage
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

RUN apk add --no-cache curl bash

RUN mkdir -p logs

ENV JAVA_OPTS="-Xms256m -Xmx512m"
ENV SPRING_PROFILES_ACTIVE=dev

# Correct path for GitHub Actions
COPY --from=build /app/target/NotificationService-0.0.1-SNAPSHOT.jar NotificationService.jar

EXPOSE 8085

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Dspring.profiles.active=$SPRING_PROFILES_ACTIVE -jar NotificationService.jar"]
