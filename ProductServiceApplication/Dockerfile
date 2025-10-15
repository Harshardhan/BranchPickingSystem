# Build Stage - uses Maven + JDK
FROM maven:3.9.6-eclipse-temurin-17-alpine AS build

WORKDIR /app
COPY . .

RUN mvn clean install -DskipTests

# Run Stage - uses lightweight JRE only
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app
RUN apk add --no-cache curl
RUN apk add --no-cache redis

RUN mkdir -p logs

COPY --from=build /app/ProductServiceApplication/target/ProductServiceApplication-0.0.1-SNAPSHOT.jar ProductServiceApplication.jar

EXPOSE 8082
ENV SPRING_PROFILES_ACTIVE=dev
ENTRYPOINT ["java", "-jar", "ProductServiceApplication.jar"]