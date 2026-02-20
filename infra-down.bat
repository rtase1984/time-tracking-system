REM ============================================
REM infra-down.bat - Detener infraestructura
REM ============================================
REM Guardar como: infra-down.bat en la raíz del proyecto
@echo off
echo Stopping infrastructure...
docker-compose down
echo Infrastructure stopped!
pause