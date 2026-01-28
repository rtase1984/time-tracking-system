REM ============================================
REM build-all.bat - Compilar todos los servicios
REM ============================================
REM Guardar como: build-all.bat en la raíz del proyecto
@echo off
echo Building all services...
call mvn clean install -DskipTests
if errorlevel 1 (
    echo Build failed!
    pause
    exit /b 1
)
echo.
echo Build completed successfully!
pause