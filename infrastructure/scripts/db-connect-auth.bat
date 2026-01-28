REM ============================================
REM db-connect-auth.bat - Conectar a PostgreSQL auth
REM ============================================
REM Guardar como: db-connect-auth.bat en la raíz del proyecto
@echo off
echo Connecting to auth PostgreSQL database...
docker exec -it postgres-auth psql -U auth_user -d auth_db