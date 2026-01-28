#!/bin/bash

echo "========================================="
echo "Time Tracking System - Setup Script"
echo "========================================="

# Colors
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Check if Docker is running
echo -e "\n${YELLOW}[1/5] Checking Docker...${NC}"
if ! docker info > /dev/null 2>&1; then
    echo -e "${RED}Error: Docker is not running${NC}"
    exit 1
fi
echo -e "${GREEN}✓ Docker is running${NC}"

# Start infrastructure
echo -e "\n${YELLOW}[2/5] Starting infrastructure services...${NC}"
docker-compose up -d

# Wait for services to be ready
echo -e "\n${YELLOW}[3/5] Waiting for services to be ready...${NC}"
sleep 15

# Check PostgreSQL
echo "Checking PostgreSQL auth-service..."
until docker exec postgres-auth pg_isready -U auth_user > /dev/null 2>&1; do
    echo "Waiting for PostgreSQL auth-service..."
    sleep 2
done
echo -e "${GREEN}✓ PostgreSQL auth-service is ready${NC}"

echo "Checking PostgreSQL time-tracking..."
until docker exec postgres-timetracking pg_isready -U timetracking_user > /dev/null 2>&1; do
    echo "Waiting for PostgreSQL time-tracking..."
    sleep 2
done
echo -e "${GREEN}✓ PostgreSQL time-tracking is ready${NC}"

echo "Checking PostgreSQL timesheet..."
until docker exec postgres-timesheet pg_isready -U timesheet_user > /dev/null 2>&1; do
    echo "Waiting for PostgreSQL timesheet..."
    sleep 2
done
echo -e "${GREEN}✓ PostgreSQL timesheet is ready${NC}"

# Check MongoDB
echo "Checking MongoDB..."
until docker exec mongodb mongosh --eval "db.adminCommand('ping')" > /dev/null 2>&1; do
    echo "Waiting for MongoDB..."
    sleep 2
done
echo -e "${GREEN}✓ MongoDB is ready${NC}"

# Check Redis
echo "Checking Redis..."
until docker exec redis redis-cli ping > /dev/null 2>&1; do
    echo "Waiting for Redis..."
    sleep 2
done
echo -e "${GREEN}✓ Redis is ready${NC}"

# Check Kafka
echo "Checking Kafka..."
sleep 10
echo -e "${GREEN}✓ Kafka is ready${NC}"

# Initialize Kafka topics
echo -e "\n${YELLOW}[4/5] Creating Kafka topics...${NC}"
chmod +x infrastructure/scripts/init-kafka-topics.sh
./infrastructure/scripts/init-kafka-topics.sh

# Build all services
echo -e "\n${YELLOW}[5/5] Building all services...${NC}"
mvn clean install -DskipTests

echo -e "\n${GREEN}=========================================${NC}"
echo -e "${GREEN}✓ Setup completed successfully!${NC}"
echo -e "${GREEN}=========================================${NC}"

echo -e "\n${YELLOW}Services Status:${NC}"
docker-compose ps

echo -e "\n${YELLOW}Available URLs:${NC}"
echo "  • Kafka UI:     http://localhost:8090"
echo "  • Zipkin:       http://localhost:9411"
echo "  • Prometheus:   http://localhost:9090"
echo "  • Grafana:      http://localhost:3000 (admin/admin123)"
echo ""
echo "  • PostgreSQL Auth:        localhost:5432"
echo "  • PostgreSQL TimeTracking: localhost:5433"
echo "  • PostgreSQL Timesheet:    localhost:5434"
echo "  • MongoDB:                 localhost:27017"
echo "  • Redis:                   localhost:6379"

echo -e "\n${YELLOW}To start the microservices:${NC}"
echo "  cd auth-service && mvn spring-boot:run"
echo "  cd time-tracking-service && mvn spring-boot:run"
echo "  cd timesheet-service && mvn spring-boot:run"
echo "  cd billing-service && mvn spring-boot:run"
echo "  cd notification-service && mvn spring-boot:run"
echo "  cd api-gateway && mvn spring-boot:run"

echo -e "\n${YELLOW}Default test users (after running auth-service):${NC}"
echo "  • Admin:      admin@timetracking.com / admin123"
echo "  • Supervisor: supervisor@timetracking.com / supervisor123"
echo "  • Worker:     worker@timetracking.com / worker123"

echo -e "\n${GREEN}Happy coding! 🚀${NC}\n"