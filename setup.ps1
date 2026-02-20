# Time Tracking System - Setup Script (PowerShell)

# Colors
$ErrorColor = "Red"
$SuccessColor = "Green"
$InfoColor = "Yellow"

Write-Host "=========================================" -ForegroundColor $InfoColor
Write-Host "Time Tracking System - Setup Script" -ForegroundColor $InfoColor
Write-Host "=========================================" -ForegroundColor $InfoColor

# Function to check if a command exists
function Test-Command($cmdname) {
    return [bool](Get-Command -Name $cmdname -ErrorAction SilentlyContinue)
}

# Function to wait for a service
function Wait-ForService($containerName, $checkCommand, $serviceName) {
    Write-Host "Checking $serviceName..." -ForegroundColor $InfoColor
    $maxAttempts = 30
    $attempts = 0

    while ($attempts -lt $maxAttempts) {
        $result = docker exec $containerName $checkCommand 2>$null
        if ($LASTEXITCODE -eq 0) {
            Write-Host "[OK] $serviceName is ready" -ForegroundColor $SuccessColor
            return $true
        }
        Write-Host "Waiting for $serviceName..." -ForegroundColor $InfoColor
        Start-Sleep -Seconds 2
        $attempts++
    }

    Write-Host "[ERROR] $serviceName failed to start" -ForegroundColor $ErrorColor
    return $false
}

# [1/5] Check if Docker is running
Write-Host "`n[1/5] Checking Docker..." -ForegroundColor $InfoColor
try {
    docker info | Out-Null
    Write-Host "[OK] Docker is running" -ForegroundColor $SuccessColor
} catch {
    Write-Host "[ERROR] Docker is not running" -ForegroundColor $ErrorColor
    Write-Host "Please start Docker Desktop and try again" -ForegroundColor $ErrorColor
    Read-Host "Press Enter to exit"
    exit 1
}

# Check if docker-compose exists
if (-not (Test-Command docker-compose)) {
    Write-Host "[ERROR] docker-compose not found" -ForegroundColor $ErrorColor
    Write-Host "Please install Docker Compose" -ForegroundColor $ErrorColor
    Read-Host "Press Enter to exit"
    exit 1
}

# [2/5] Start infrastructure
Write-Host "`n[2/5] Starting infrastructure services..." -ForegroundColor $InfoColor
try {
    docker-compose up -d
    if ($LASTEXITCODE -eq 0) {
        Write-Host "[OK] Infrastructure services started" -ForegroundColor $SuccessColor
    } else {
        throw "Failed to start services"
    }
} catch {
    Write-Host "[ERROR] Failed to start infrastructure" -ForegroundColor $ErrorColor
    Read-Host "Press Enter to exit"
    exit 1
}

# [3/5] Wait for services to be ready
Write-Host "`n[3/5] Waiting for services to be ready..." -ForegroundColor $InfoColor
Start-Sleep -Seconds 15

# Check PostgreSQL services
Wait-ForService "postgres-auth" "pg_isready -U auth_user" "PostgreSQL auth-service"
Wait-ForService "postgres-timetracking" "pg_isready -U timetracking_user" "PostgreSQL time-tracking"
Wait-ForService "postgres-timesheet" "pg_isready -U timesheet_user" "PostgreSQL timesheet"

# Check MongoDB
Wait-ForService "mongodb" "mongosh --eval `"db.adminCommand('ping')`" --quiet" "MongoDB"

# Check Redis
Wait-ForService "redis" "redis-cli ping" "Redis"

# Check Kafka
Write-Host "Checking Kafka..." -ForegroundColor $InfoColor
Start-Sleep -Seconds 10
Write-Host "[OK] Kafka is ready" -ForegroundColor $SuccessColor

# [4/5] Initialize Kafka topics
Write-Host "`n[4/5] Creating Kafka topics..." -ForegroundColor $InfoColor

$topics = @(
    "time.entry.registered",
    "timesheet.approved",
    "timesheet.rejected",
    "invoice.generated",
    "user.notification"
)

foreach ($topic in $topics) {
    Write-Host "Creating topic: $topic" -ForegroundColor $InfoColor
    docker exec kafka kafka-topics --create `
        --bootstrap-server localhost:9092 `
        --replication-factor 1 `
        --partitions 3 `
        --topic $topic `
        --if-not-exists 2>&1 | Out-Null
}

Write-Host "`nListing created topics:" -ForegroundColor $InfoColor
docker exec kafka kafka-topics --list --bootstrap-server localhost:9092

# [5/5] Build all services
Write-Host "`n[5/5] Building all services..." -ForegroundColor $InfoColor
if (Test-Command mvn) {
    try {
        mvn clean install -DskipTests
        if ($LASTEXITCODE -eq 0) {
            Write-Host "[OK] Build completed successfully" -ForegroundColor $SuccessColor
        } else {
            Write-Host "[WARNING] Build failed, but infrastructure is ready" -ForegroundColor $InfoColor
        }
    } catch {
        Write-Host "[WARNING] Maven build failed" -ForegroundColor $InfoColor
    }
} else {
    Write-Host "[WARNING] Maven not found, skipping build" -ForegroundColor $InfoColor
    Write-Host "Please install Maven and run: mvn clean install" -ForegroundColor $InfoColor
}

# Summary
Write-Host "`n=========================================" -ForegroundColor $SuccessColor
Write-Host "Setup completed successfully!" -ForegroundColor $SuccessColor
Write-Host "=========================================" -ForegroundColor $SuccessColor

Write-Host "`nServices Status:" -ForegroundColor $InfoColor
docker-compose ps

Write-Host "`nAvailable URLs:" -ForegroundColor $InfoColor
Write-Host "  • Kafka UI:     http://localhost:8090"
Write-Host "  • Zipkin:       http://localhost:9411"
Write-Host "  • Prometheus:   http://localhost:9090"
Write-Host "  • Grafana:      http://localhost:3000 (admin/admin123)"
Write-Host ""
Write-Host "  • PostgreSQL Auth:        localhost:5432"
Write-Host "  • PostgreSQL TimeTracking: localhost:5433"
Write-Host "  • PostgreSQL Timesheet:    localhost:5434"
Write-Host "  • MongoDB:                 localhost:27017"
Write-Host "  • Redis:                   localhost:6379"

Write-Host "`nTo start the microservices:" -ForegroundColor $InfoColor
Write-Host "  cd auth-service; mvn spring-boot:run"
Write-Host "  cd time-tracking-service; mvn spring-boot:run"
Write-Host "  cd timesheet-service; mvn spring-boot:run"
Write-Host "  cd billing-service; mvn spring-boot:run"
Write-Host "  cd notification-service; mvn spring-boot:run"
Write-Host "  cd api-gateway; mvn spring-boot:run"

Write-Host "`nDefault test users (after running auth-service):" -ForegroundColor $InfoColor
Write-Host "  • Admin:      admin@timetracking.com / admin123"
Write-Host "  • Supervisor: supervisor@timetracking.com / supervisor123"
Write-Host "  • Worker:     worker@timetracking.com / worker123"

Write-Host "`nHappy coding! 🚀`n" -ForegroundColor $SuccessColor
Read-Host "Press Enter to exit"