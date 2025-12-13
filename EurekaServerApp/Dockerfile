# Build Stage
FROM maven:3.9.6-eclipse-temurin-17-alpine AS build

WORKDIR /build

# Copy parent pom first (cache optimization)
COPY pom.xml .

# Copy modules
COPY EurekaServerApp EurekaServerApp

# Build only Eureka module but resolve parent
RUN mvn -pl EurekaServerApp -am clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app
RUN apk add --no-cache curl bash
RUN mkdir -p logs

ENV JAVA_OPTS="-Xms256m -Xmx512m"
ENV SPRING_PROFILES_ACTIVE=dev

COPY --from=build /build/EurekaServerApp/target/*jar EurekaServerApp.jar

EXPOSE 8761
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar EurekaServerApp.jar"]
