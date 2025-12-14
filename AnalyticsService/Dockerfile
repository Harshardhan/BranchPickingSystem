# ---------- BUILD STAGE ----------
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

COPY . .
RUN mvn clean install -DskipTests

# ---------- RUNTIME STAGE ----------
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

RUN apk add --no-cache curl bash
RUN mkdir -p logs

COPY --from=build /app/AnalyticsService/target/AnalyticsService-0.0.1-SNAPSHOT.jar AnalyticsService.jar

EXPOSE 8087
ENTRYPOINT ["java","-jar","AnalyticsService.jar"]
