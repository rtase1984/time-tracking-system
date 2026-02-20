REM ============================================
REM run-auth.bat - Ejecutar auth-service
REM ============================================
@echo off
echo Starting auth-service...
cd auth-service
call mvn spring-boot:run