# Time Tracking System - Microservices Architecture
Sistema de control de tiempo para trabajadores remotos construido con arquitectura de microservicios.
## 🏗️ Arquitectura

API Gateway: Spring Cloud Gateway (Puerto 8080)
Auth Service: Autenticación y autorización (Puerto 8081)
Time Tracking Service: Registro de entradas/salidas (Puerto 8082)
Timesheet Service: Gestión de hojas de tiempo (Puerto 8083)
Billing Service: Generación de facturas (Puerto 8084)
Notification Service: Envío de notificaciones (Puerto 8085)

## 🛠️ Stack Tecnológico

Framework: Spring Boot 3.2.x
Java: 17+
Build Tool: Maven
Bases de datos:

PostgreSQL 15 (auth, time-tracking, timesheet)
MongoDB 7 (billing)
Redis 7 (cache)


Message Broker: Apache Kafka 3.5
API Gateway: Spring Cloud Gateway
Observability:

Distributed Tracing: Zipkin
Metrics: Prometheus + Grafana
Logging: SLF4J + Logback


Database Migration: Liquibase
Containerization: Docker & Docker Compose

## 🚀 Inicio Rápido
### Prerrequisitos

- Java 17+
- Maven 3.8+
- Docker & Docker Compose

## Levantar Infraestructura
```bash
# Levantar todos los servicios de infraestructura
docker-compose up -d

#Ver logs
docker-compose logs -f

# Detener servicios
docker-compose down
```

## Detener y eliminar volúmenes

```bash 
docker-compose down -v
```

## Crear Topics de Kafka 
``` bash
chmod +x infrastructure/scripts/init-kafka-topics.sh ./infrastructure/scripts/init-kafka-topics.sh
```

## Compilar Servicios 

```bash
#Compilar todos los servicios
mvn clean install
   
#Compilar un servicio específico
cd auth-service
mvn clean install
```

## Ejecutar Servicios

```bash
#Ejecutar cada servicio en terminales separadas
cd auth-service && mvn spring-boot:run
cd time-tracking-service && mvn spring-boot:run
cd timesheet-service && mvn spring-boot:run
cd billing-service && mvn spring-boot:run
cd notification-service && mvn spring-boot:run
cd api-gateway && mvn spring-boot:run
```

## 📊 URLs de Acceso

**API Gateway:** http://localhost:8080

**Zipkin:** http://localhost:9411

**Kafka UI:** http://localhost:8090

**Prometheus:** http://localhost:9090

**Grafana:** http://localhost:3000 (admin/admin123)

**Swagger UI:** http://localhost:8080/swagger-ui.html

## 🗄️ Bases de Datos
PostgreSQL

`` ServicioPuertoDatabaseUserPasswordauth-service5432auth_dbauth_userauth_passtime-tracking5433timetracking_dbtimetracking_usertimetracking_passtimesheet5434timesheet_dbtimesheet_usertimesheet_pass
``

MongoDB

Puerto: 27017
Database: billing_db
User: admin
Password: admin123

Redis

Puerto: 6379
Sin autenticación (solo desarrollo)

📡 Topics de Kafka

- time.entry.registered - Eventos de registro de tiempo
- timesheet.approved - Eventos de aprobación de timesheets
- timesheet.rejected - Eventos de rechazo de timesheets
- invoice.generated - Eventos de facturas generadas
- user.notification - Eventos de notificaciones

🧪 Testing bash# Ejecutar tests de un servicio
cd auth-service
mvn test

# Ejecutar tests de integración
mvn verify
📝 API Documentation
Cada microservicio expone su documentación OpenAPI en:

http://localhost:{puerto}/v3/api-docs
http://localhost:{puerto}/swagger-ui.html

🔐 Autenticación
El sistema usa JWT tokens. Para obtener un token:
bashPOST http://localhost:8080/api/v1/auth/login
{
  "email": "user@example.com",
  "password": "password123"
}
Incluir el token en requests subsecuentes:
Authorization: Bearer {token}
🏛️ Principios de Diseño

Domain Driven Design (DDD)
Arquitectura Hexagonal
SOLID Principles
Event-Driven Architecture
API-First Design
12-Factor App

📦 Estructura de Servicios
Cada microservicio sigue esta estructura:
```
service-name/
├── src/main/java/com/timetracking/{service}/
│   ├── config/          # Configuraciones
│   ├── controller/      # REST Controllers
│   ├── service/         # Lógica de negocio
│   ├── repository/      # Acceso a datos
│   ├── domain/          # Entidades y DTOs
│   ├── event/           # Eventos de Kafka
│   ├── exception/       # Manejo de excepciones
│   └── util/            # Utilidades
├── src/main/resources/
│   ├── application.yml
│   ├── application-dev.yml
│   ├── application-prod.yml
│   └── db/changelog/    # Liquibase migrations
└── src/test/
```

🔄 Flujo de Datos
Check-in/Check-out: Worker → API Gateway → Time Tracking Service → Kafka
Cálculo Timesheet: Scheduled Job → Timesheet Service → PostgreSQL/Redis
Aprobación: Supervisor → API Gateway → Timesheet Service → Kafka
Facturación: Kafka Consumer → Billing Service → MongoDB
Notificación: Kafka Consumer → Notification Service → Email/Push

🐛 Troubleshooting
Kafka no inicia
bashdocker-compose restart zookeeper kafka
Puerto ocupado
bash# Verificar puertos en uso
lsof -i :8080
Limpiar todo y empezar de nuevo
bashdocker-compose down -v
docker system prune -a
docker-compose up -d
📚 Recursos

Spring Boot Documentation
Apache Kafka Documentation
Liquibase Documentation

👥 Contribuir

Fork el proyecto
Crea una rama feature (git checkout -b feature/amazing-feature)
Commit cambios (git commit -m 'Add amazing feature')
Push a la rama (git push origin feature/amazing-feature)
Abre un Pull Request

📄 Licencia
Este proyecto es de código abierto para propósitos de portfolio.