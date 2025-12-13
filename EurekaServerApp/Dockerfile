# ---------- Build stage ----------
FROM maven:3.9.6-eclipse-temurin-17-alpine AS build

WORKDIR /build

# Copy ENTIRE repo (critical)
COPY . .

# Build only Eureka module, but all modules must exist
RUN mvn -pl EurekaServerApp -am clean package -DskipTests

# ---------- Runtime stage ----------
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

COPY --from=build /build/EurekaServerApp/target/*.jar EurekaServerApp.jar

EXPOSE 8761

ENTRYPOINT ["java","-jar","EurekaServerApp.jar"]
