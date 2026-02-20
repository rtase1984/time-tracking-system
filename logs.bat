REM ============================================
REM logs.bat - Ver logs de infraestructura
REM ============================================
REM Guardar como: logs.bat en la raíz del proyecto
@echo off
echo Showing logs from all services...
echo Press Ctrl+C to stop
docker-compose logs -f