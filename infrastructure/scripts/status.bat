REM ============================================
REM status.bat - Ver estado de servicios
REM ============================================
REM Guardar como: status.bat en la raíz del proyecto
@echo off
echo Infrastructure Status:
echo.
docker-compose ps
echo.
echo Available URLs:
echo   - Auth Service:  http://localhost:8081
echo   - Swagger:       http://localhost:8081/swagger-ui.html
echo   - Kafka UI:      http://localhost:8090
echo   - Zipkin:        http://localhost:9411
echo   - Prometheus:    http://localhost:9090
echo   - Grafana:       http://localhost:3000
echo.
pause
