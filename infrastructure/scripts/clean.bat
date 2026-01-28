REM ============================================
REM clean.bat - Limpiar todo
REM ============================================
REM Guardar como: clean.bat en la raíz del proyecto
@echo off
echo Cleaning all Maven artifacts...
call mvn clean
echo.
echo Stopping and removing all containers...
docker-compose down -v
echo.
echo Cleanup completed!
pause