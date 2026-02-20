REM ============================================
REM reset.bat - Reset completo del ambiente
REM ============================================
REM Guardar como: reset.bat en la raíz del proyecto
@echo off
echo WARNING: This will delete all data and restart everything!
echo.
set /p confirm="Are you sure? (y/n): "
if /i not "%confirm%"=="y" (
    echo Reset cancelled.
    pause
    exit /b 0
)

echo.
echo Stopping and removing all containers and volumes...
docker-compose down -v

echo.
echo Starting fresh infrastructure...
docker-compose up -d

echo.
echo Waiting for services...
timeout /t 20 /nobreak >nul

echo.
echo Creating Kafka topics...
call infrastructure\scripts\init-kafka-topics.bat

echo.
echo Reset completed! Infrastructure is ready.
pause