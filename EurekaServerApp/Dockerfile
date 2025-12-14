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

COPY --from=build /app/EurekaServerApp/target/EurekaServerApp-0.0.1-SNAPSHOT.jar EurekaServerApp.jar

EXPOSE 8761
ENTRYPOINT ["java","-jar","EurekaServerApp.jar"]
