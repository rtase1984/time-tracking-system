REM ============================================
REM infra-up.bat - Levantar infraestructura
REM ============================================
REM Guardar como: infra-up.bat en la raíz del proyecto
@echo off
echo Starting infrastructure...
docker-compose up -d
echo.
echo Waiting for services to be ready...
timeout /t 20 /nobreak >nul
echo.
echo Creating Kafka topics...
call infrastructure\scripts\init-kafka-topics.bat
echo.
echo Infrastructure is ready!
echo.
echo Available services:
docker-compose ps
echo.
echo Open:
echo   - Kafka UI:   http://localhost:8090
echo   - Zipkin:     http://localhost:9411
echo   - Prometheus: http://localhost:9090
echo   - Grafana:    http://localhost:3000
pause