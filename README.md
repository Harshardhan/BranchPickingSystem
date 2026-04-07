# 🚀 Branch Picking System – Microservices Architecture

## 📌 Overview

The **Branch Picking System** is a distributed microservices-based application built using **Spring Boot**, following modern cloud-native architecture principles.

This project demonstrates:

* Microservices architecture
* Service discovery using Eureka
* API Gateway routing
* Centralized configuration
* Docker & Docker Compose orchestration
* Kubernetes deployment support

---

## 🏗️ Architecture

```
Client → API Gateway → Microservices → Database
                      ↓
                 Eureka Server
                      ↓
               Config Server
```

---

## 🧩 Services Included

| Service Name              | Description                         |
| ------------------------- | ----------------------------------- |
| API-Gateway               | Entry point for all client requests |
| EurekaServerApp           | Service discovery                   |
| ConfigServerApp           | Centralized configuration           |
| ConfigRepo                | External configuration repository   |
| UserRegistration          | User management service             |
| OrderServiceApplication   | Order processing                    |
| ProductServiceApplication | Product management                  |
| PaymentApplication        | Payment handling                    |
| NotificationService       | Notification system                 |
| AnalyticsService          | Data analytics                      |
| ConsolidationService      | Data aggregation                    |
| Common-Library            | Shared utilities                    |

---

## ⚙️ Technologies Used

* Java 17
* Spring Boot
* Spring Cloud (Eureka, Config Server, Gateway)
* Maven
* Docker & Docker Compose
* Kubernetes (k8s folder)
* Prometheus (Monitoring)
* Logstash (Logging)

---

## 🐳 Running with Docker Compose (Development)

### 🔹 Prerequisites

* Docker installed
* Docker Compose installed

### 🔹 Run the application

```bash
docker-compose -f docker-compose.dev.yml up --build
```

### 🔹 AWS Deployment

```bash
docker-compose -f docker-compose.aws.yml up --build
```

---

## 🔗 Key Concepts

### ✅ Service Communication

Services communicate using **service names**, not localhost.

Example:

```
jdbc:mysql://mysql:3306/db
```

---

### ✅ Service Discovery

* Managed by **Eureka Server**
* Services register automatically

---

### ✅ API Gateway

* Routes all external requests
* Provides centralized entry point

---

### ✅ Centralized Configuration

* Managed using Config Server + ConfigRepo

---

## 📊 Monitoring & Logging

* **Prometheus** → Metrics collection
* **Logstash** → Log aggregation

---

## ☸️ Kubernetes Support

Kubernetes configuration files are available in:

```
/k8s
```

To deploy:

```bash
kubectl apply -f k8s/
```

---

## 📂 Project Structure

```
BranchPickingSystem/
│
├── API-Gateway/
├── EurekaServerApp/
├── ConfigServerApp/
├── ConfigRepo/
├── UserRegistration/
├── OrderServiceApplication/
├── ProductServiceApplication/
├── PaymentApplication/
├── NotificationService/
├── AnalyticsService/
├── ConsolidationService/
├── Common-Library/
│
├── docker-compose.dev.yml
├── docker-compose.aws.yml
├── k8s/
├── README.md
```

---

## 🧪 How to Test

1. Start all services using Docker Compose
2. Access API Gateway:

```
http://localhost:<gateway-port>
```

3. Call APIs via Gateway routes

---

## 🎯 Interview Highlights

* Implemented **microservices architecture using Spring Boot**
* Used **Eureka for service discovery**
* Integrated **API Gateway for routing**
* Managed configs using **Spring Cloud Config**
* Containerized services using **Docker**
* Orchestrated using **Docker Compose**
* Prepared for **Kubernetes deployment**

---

## 📌 Future Improvements

* Add distributed tracing (Zipkin)
* Implement circuit breaker (Resilience4j)
* Add CI/CD pipeline
* Improve security (OAuth2 / JWT)

---

## 👨‍💻 Author

Developed as part of backend microservices practice and system design preparation.

---
