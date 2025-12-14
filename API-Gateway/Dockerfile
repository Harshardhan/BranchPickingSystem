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

COPY --from=build /app/API-Gateway/target/API-Gateway-0.0.1-SNAPSHOT.jar API-Gateway.jar

EXPOSE 80
ENTRYPOINT ["java","-jar","API-Gateway.jar"]
