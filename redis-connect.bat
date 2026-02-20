REM ============================================
REM redis-connect.bat - Conectar a Redis
REM ============================================
REM Guardar como: redis-connect.bat en la raíz del proyecto
@echo off
echo Connecting to Redis CLI...
docker exec -it redis redis-cli