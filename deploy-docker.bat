@echo off
echo =========================================
echo Time Tracking System - Docker Deployment
echo =========================================
echo.

echo [1/3] Building application with Maven (skipping tests)...
call mvn clean package -DskipTests
if errorlevel 1 (
    echo [!] Build failed!
    pause
    exit /b 1
)

echo.
echo [2/3] Stopping existing containers...
docker-compose down

echo.
echo [3/3] Building and starting containers...
docker-compose up -d --build

echo.
echo =========================================
echo Deployment started!
echo =========================================
echo.
echo Please wait 1-2 minutes for services to initialize.
echo You can use 'infrastructure\scripts\check-services.bat' to verify status.
echo.
pause
