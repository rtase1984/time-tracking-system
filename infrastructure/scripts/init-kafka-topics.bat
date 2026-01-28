@echo off
echo Waiting for Kafka to be ready...
timeout /t 10 /nobreak >nul

echo Creating Kafka topics...

REM Create topic: time.entry.registered
docker exec kafka kafka-topics --create --bootstrap-server localhost:9092 --replication-factor 1 --partitions 3 --topic time.entry.registered --if-not-exists

REM Create topic: timesheet.approved
docker exec kafka kafka-topics --create --bootstrap-server localhost:9092 --replication-factor 1 --partitions 3 --topic timesheet.approved --if-not-exists

REM Create topic: timesheet.rejected
docker exec kafka kafka-topics --create --bootstrap-server localhost:9092 --replication-factor 1 --partitions 3 --topic timesheet.rejected --if-not-exists

REM Create topic: invoice.generated
docker exec kafka kafka-topics --create --bootstrap-server localhost:9092 --replication-factor 1 --partitions 3 --topic invoice.generated --if-not-exists

REM Create topic: user.notification
docker exec kafka kafka-topics --create --bootstrap-server localhost:9092 --replication-factor 1 --partitions 3 --topic user.notification --if-not-exists

echo.
echo Listing created topics...
docker exec kafka kafka-topics --list --bootstrap-server localhost:9092

echo.
echo Topics created successfully!