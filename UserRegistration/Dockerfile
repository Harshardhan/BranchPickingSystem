FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

RUN apk add --no-cache curl bash
RUN mkdir -p logs

ENV JAVA_OPTS="-Xms256m -Xmx512m"
ENV SPRING_PROFILES_ACTIVE=dev

# Copy the pre-built JAR from GitHub Actions workspace
COPY target/UserRegistration-0.0.1-SNAPSHOT.jar UserRegistration.jar

EXPOSE 8083

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Dspring.profiles.active=$SPRING_PROFILES_ACTIVE -jar UserRegistration.jar"]
