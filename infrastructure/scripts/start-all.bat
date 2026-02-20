@echo off
echo =========================================
echo Iniciando Time Tracking System
echo =========================================
echo.

echo [1/7] Verificando infraestructura...
docker-compose ps | findstr "Up" >nul
if errorlevel 1 (
    echo [!] Infraestructura no esta corriendo
    echo     Iniciando infraestructura...
    docker-compose up -d
    echo     Esperando 20 segundos...
    timeout /t 20 /nobreak >nul
) else (
    echo [OK] Infraestructura corriendo
)

echo.
echo [2/7] Iniciando Auth Service (puerto 8081)...
start "Auth Service" cmd /c "cd auth-service && mvn spring-boot:run"
timeout /t 10 /nobreak >nul

echo [3/7] Iniciando Time Tracking Service (puerto 8082)...
start "Time Tracking" cmd /c "cd time-tracking-service && mvn spring-boot:run"
timeout /t 10 /nobreak >nul

echo [4/7] Iniciando Timesheet Service (puerto 8083)...
start "Timesheet" cmd /c "cd time-sheet-service && mvn spring-boot:run"
timeout /t 10 /nobreak >nul

echo [5/7] Iniciando Billing Service (puerto 8084)...
start "Billing" cmd /c "cd billing-service && mvn spring-boot:run"
timeout /t 10 /nobreak >nul

echo [6/7] Iniciando Notification Service (puerto 8085)...
start "Notification" cmd /c "cd notification-service && mvn spring-boot:run"
timeout /t 10 /nobreak >nul

echo [7/7] Iniciando API Gateway (puerto 8080)...
start "API Gateway" cmd /c "cd api-gateway && mvn spring-boot:run"

echo.
echo =========================================
echo Todos los servicios estan iniciando!
echo =========================================
echo.
echo Cada servicio tardara 30-60 segundos en estar listo.
echo.
echo Para verificar que todos esten corriendo:
echo   1. Espera 2-3 minutos
echo   2. Ejecuta: check-all-services.bat
echo.
echo URLs importantes:
echo   - API Gateway:  http://localhost:8080
echo   - Swagger Auth: http://localhost:8081/swagger-ui.html
echo   - Kafka UI:     http://localhost:8090
echo   - Zipkin:       http://localhost:9411
echo.
echo Para detener todos los servicios:
echo   - Cierra todas las ventanas de servicios
echo   - O ejecuta: stop-all.bat
echo.
pause