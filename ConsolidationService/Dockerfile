FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Optional tools
RUN apk add --no-cache curl bash

# Logs directory
RUN mkdir -p logs

# JVM settings
ENV JAVA_OPTS="-Xms256m -Xmx512m"
ENV SPRING_PROFILES_ACTIVE=dev

# Copy the pre-built JAR from GitHub Actions
COPY target/*.jar ConsolidationService.jar

EXPOSE 8084

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Dspring.profiles.active=$SPRING_PROFILES_ACTIVE -jar ConsolidationService.jar"]
