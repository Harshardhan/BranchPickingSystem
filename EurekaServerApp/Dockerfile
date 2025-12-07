# Build Stage
FROM maven:3.9.6-eclipse-temurin-17-alpine AS build

WORKDIR /app
COPY . .

RUN mvn clean install -DskipTests

# Run Stage (very lightweight)
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Create logs folder
RUN mkdir -p logs

# Set Java memory limits to reduce RAM usage
ENV JAVA_OPTS="-Xms256m -Xmx512m"

COPY --from=build /app/EurekaServerApp/target/EurekaServerApp-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8761

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
