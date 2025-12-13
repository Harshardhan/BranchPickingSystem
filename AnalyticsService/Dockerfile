FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

RUN apk add --no-cache curl bash
RUN mkdir -p logs

ENV JAVA_OPTS="-Xms256m -Xmx512m"
ENV SPRING_PROFILES_ACTIVE=dev

# Copy the pre-built JAR from GitHub Actions workspace
COPY target/AnalyticsService-0.0.1-SNAPSHOT.jar AnalyticsService.jar

EXPOSE 8087

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Dspring.profiles.active=$SPRING_PROFILES_ACTIVE -jar AnalyticsService.jar"]
