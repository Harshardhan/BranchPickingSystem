# ---------- BUILD STAGE ----------
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

COPY pom.xml .
COPY src ./src
RUN mvn clean install -DskipTests

# ---------- RUNTIME STAGE ----------
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

RUN apk add --no-cache curl bash
RUN mkdir -p logs

COPY --from=build /app/target/*jar app.jar

EXPOSE 8085
ENTRYPOINT ["java","-jar","app.jar"]
