@echo off
setlocal enabledelayedexpansion

echo =========================================
echo Time Tracking System - Setup Script
echo =========================================

REM Check if Docker is running
echo.
echo [1/5] Checking Docker...
docker info >nul 2>&1
if errorlevel 1 (
    echo [ERROR] Docker is not running
    echo Please start Docker Desktop and try again
    pause
    exit /b 1
)
echo [OK] Docker is running

REM Start infrastructure
echo.
echo [2/5] Starting infrastructure services...
docker-compose up -d
if errorlevel 1 (
    echo [ERROR] Failed to start infrastructure
    pause
    exit /b 1
)

REM Wait for services to be ready
echo.
echo [3/5] Waiting for services to be ready...
timeout /t 15 /nobreak >nul

REM Check PostgreSQL auth-service
echo Checking PostgreSQL auth-service...
:wait_postgres_auth
docker exec postgres-auth pg_isready -U auth_user >nul 2>&1
if errorlevel 1 (
    echo Waiting for PostgreSQL auth-service...
    timeout /t 2 /nobreak >nul
    goto wait_postgres_auth
)
echo [OK] PostgreSQL auth-service is ready

REM Check PostgreSQL time-tracking
echo Checking PostgreSQL time-tracking...
:wait_postgres_timetracking
docker exec postgres-timetracking pg_isready -U timetracking_user >nul 2>&1
if errorlevel 1 (
    echo Waiting for PostgreSQL time-tracking...
    timeout /t 2 /nobreak >nul
    goto wait_postgres_timetracking
)
echo [OK] PostgreSQL time-tracking is ready

REM Check PostgreSQL timesheet
echo Checking PostgreSQL timesheet...
:wait_postgres_timesheet
docker exec postgres-timesheet pg_isready -U timesheet_user >nul 2>&1
if errorlevel 1 (
    echo Waiting for PostgreSQL timesheet...
    timeout /t 2 /nobreak >nul
    goto wait_postgres_timesheet
)
echo [OK] PostgreSQL timesheet is ready

REM Check MongoDB
echo Checking MongoDB...
:wait_mongodb
docker exec mongodb mongosh --eval "db.adminCommand('ping')" --quiet >nul 2>&1
if errorlevel 1 (
    echo Waiting for MongoDB...
    timeout /t 2 /nobreak >nul
    goto wait_mongodb
)
echo [OK] MongoDB is ready

REM Check Redis
echo Checking Redis...
:wait_redis
docker exec redis redis-cli ping >nul 2>&1
if errorlevel 1 (
    echo Waiting for Redis...
    timeout /t 2 /nobreak >nul
    goto wait_redis
)
echo [OK] Redis is ready

REM Check Kafka
echo Checking Kafka...
timeout /t 10 /nobreak >nul
echo [OK] Kafka is ready

REM Initialize Kafka topics
echo.
echo [4/5] Creating Kafka topics...
call infrastructure\scripts\init-kafka-topics.bat

REM Build all services
echo.
echo [5/5] Building all services...
call mvn clean install -DskipTests
if errorlevel 1 (
    echo [WARNING] Build failed, but infrastructure is ready
)

echo.
echo =========================================
echo Setup completed successfully!
echo =========================================

echo.
echo Services Status:
docker-compose ps

echo.
echo Available URLs:
echo   * Kafka UI:     http://localhost:8090
echo   * Zipkin:       http://localhost:9411
echo   * Prometheus:   http://localhost:9090
echo   * Grafana:      http://localhost:3000 (admin/admin123)
echo.
echo   * PostgreSQL Auth:        localhost:5432
echo   * PostgreSQL TimeTracking: localhost:5433
echo   * PostgreSQL Timesheet:    localhost:5434
echo   * MongoDB:                 localhost:27017
echo   * Redis:                   localhost:6379

echo.
echo To start the microservices:
echo   cd auth-service ^&^& mvn spring-boot:run
echo   cd time-tracking-service ^&^& mvn spring-boot:run
echo   cd timesheet-service ^&^& mvn spring-boot:run
echo   cd billing-service ^&^& mvn spring-boot:run
echo   cd notification-service ^&^& mvn spring-boot:run
echo   cd api-gateway ^&^& mvn spring-boot:run

echo.
echo Default test users (after running auth-service):
echo   * Admin:      admin@timetracking.com / admin123
echo   * Supervisor: supervisor@timetracking.com / supervisor123
echo   * Worker:     worker@timetracking.com / worker123

echo.
echo Happy coding! 🚀
echo.
pause