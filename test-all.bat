REM ============================================
REM test-all.bat - Ejecutar todos los tests
REM ============================================
REM Guardar como: test-all.bat en la raíz del proyecto
@echo off
echo Running all tests...
call mvn test
if errorlevel 1 (
    echo Tests failed!
    pause
    exit /b 1
)
echo.
echo All tests passed!
pause