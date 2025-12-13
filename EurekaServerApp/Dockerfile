# =========================
# Stage 1: Build
# =========================
FROM maven:3.9.6-eclipse-temurin-17-alpine AS build

# Set working directory inside container
WORKDIR /build

# Copy the entire project (all modules) into the container
COPY . .

# Build only the EurekaServerApp module and all its dependencies
RUN mvn -pl EurekaServerApp -am clean package -DskipTests

# =========================
# Stage 2: Runtime
# =========================
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Install any required runtime tools (optional)
RUN apk add --no-cache bash curl

# Copy the built jar from the build stage
COPY --from=build /build/EurekaServerApp/target/*.jar app.jar

# Create a logs folder
RUN mkdir -p logs

# Expose the port your EurekaServerApp runs on (adjust if needed)
EXPOSE 8761

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
