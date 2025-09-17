# Build Stage - uses Maven + JDK
FROM maven:3.9.6-eclipse-temurin-17-alpine AS build

WORKDIR /app
COPY . .

RUN mvn clean install -DskipTests

# Run Stage - uses lightweight JRE only
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app
RUN apk add --no-cache curl
RUN mkdir -p logs

COPY --from=build /app/API-GateWay/target/API-GateWay-0.0.1-SNAPSHOT.jar API-GateWay.jar

EXPOSE 8761
ENV SPRING_PROFILES_ACTIVE=dev
ENTRYPOINT ["java", "-jar", "API-GateWay.jar"]
