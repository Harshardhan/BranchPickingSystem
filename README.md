
# BranchPcikcingSystem
🏪 Branch Picking System – Microservices Deployment with Docker Compose
This project contains a complete microservices-based architecture for an Online Grocery Order Management System (BPS - Branch Picking System) built with Spring Boot, Docker, and various modern tools for monitoring, messaging, and logging.

📦 Features
Service Discovery with Eureka

Centralized Configuration via Spring Cloud Config

API Gateway for routing

Microservices:

Order Service

Product Service

Picking Service

Consolidation Service

Notification Service

Databases: MySQL (separate DBs per service)

Messaging: RabbitMQ and Apache Kafka

Monitoring: Prometheus and Grafana

Logging: ELK Stack (Elasticsearch, Logstash, Kibana)

🛠 Prerequisites
Docker

Docker Compose

Ports 3306, 3307, 8080-8085, 8888, 8761, 9200, 5601, 9090, 3000, 9092, 15672 must be free

🚀 How to Run
Clone the repository

bash
Copy
Edit
git clone https://github.com/your-username/branch-picking-system.git
cd branch-picking-system
Make sure .m2 directory is accessible

If you're on Windows:

yaml
Copy
Edit
${USERPROFILE}/.m2:/root/.m2
If you're on Linux/macOS, replace ${USERPROFILE} with $HOME or use a .env file:

bash
Copy
Edit
echo "USERPROFILE=$HOME" > .env
Build and Start All Services

bash
Copy
Edit
docker-compose up --build
Access the services

Service	URL
Eureka Discovery	http://localhost:8761
Config Server	http://localhost:8888
API Gateway	http://localhost:8080
Order Service	http://localhost:8081
Product Service	http://localhost:8082
Picking Service	http://localhost:8083
Consolidation Service	http://localhost:8084
Notification Service	http://localhost:8085
Kibana (Logs)	http://localhost:5601
Prometheus (Metrics)	http://localhost:9090
Grafana (Dashboards)	http://localhost:3000
RabbitMQ Management UI	http://localhost:15672
Kafka (internal)	kafka:9092

📁 Project Structure
Copy
Edit
.
├── docker-compose.yml
├── EurekaServerApp/
├── ConfigServerApp/
├── API-GateWay/
├── OrderServiceApplication/
├── ProductServiceApplication/
├── PickingService/
├── ConsolidationService/
├── NotificationService/
├── logstash.conf
├── prometheus.yml
📝 Notes
Volumes are used for MySQL persistence and Maven dependency caching

Logstash reads logs from ./logs/api-gateway

Profiles: All apps use docker profile when running inside containers

Kafka and RabbitMQ are both supported for notifications

🛑 To Stop and Remove All Containers
bash
Copy
Edit
docker-compose down -v
This also deletes volumes (MySQL data).

✅ Troubleshooting
Use docker-compose logs -f <service-name> to debug

Ensure services like config-server and eureka-server start before others

If using Windows, Docker Desktop must be running

📚 Future Enhancements
Add JWT-based security to API Gateway

Auto-scale services using Kubernetes

Store logs in Elasticsearch from all microservices
