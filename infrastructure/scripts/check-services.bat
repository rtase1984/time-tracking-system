@echo off
setlocal EnableDelayedExpansion

echo =========================================
echo Verificando estado de los servicios...
echo =========================================
echo.

set "SERVICES=API-Gateway:8080 Auth-Service:8081 Time-Tracking:8082 Timesheet:8083 Billing:8084 Notification:8085"

for %%s in (%SERVICES%) do (
    for /f "tokens=1,2 delims=:" %%a in ("%%s") do (
        set "NAME=%%a"
        set "PORT=%%b"
        
        <nul set /p="Checking !NAME! (Port !PORT!)... "
        
        powershell -Command "try { $response = Invoke-WebRequest -Uri http://localhost:!PORT!/actuator/health -UseBasicParsing -TimeoutSec 2; if ($response.StatusCode -eq 200) { write-host '[OK]' -NoNewline } else { write-host ('[FAIL] ' + $response.StatusCode) -NoNewline } } catch { write-host '[DOWN]' -NoNewline }"
        echo.
    )
)

echo.
echo =========================================
echo Verificacion completa.
echo =========================================
pause
