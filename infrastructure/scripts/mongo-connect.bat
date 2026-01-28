REM ============================================
REM mongo-connect.bat - Conectar a MongoDB
REM ============================================
REM Guardar como: mongo-connect.bat en la raíz del proyecto
@echo off
echo Connecting to MongoDB...
docker exec -it mongodb mongosh -u admin -p admin123 --authenticationDatabase admin
