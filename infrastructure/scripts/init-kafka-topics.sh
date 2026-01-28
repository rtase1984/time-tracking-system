#!/bin/bash

echo "Esperando a que Kafka esté listo..."
sleep 10

echo "Creando topics de Kafka..."

# Crear topics
docker exec kafka kafka-topics --create \
  --bootstrap-server localhost:9092 \
  --replication-factor 1 \
  --partitions 3 \
  --topic time.entry.registered \
  --if-not-exists

docker exec kafka kafka-topics --create \
  --bootstrap-server localhost:9092 \
  --replication-factor 1 \
  --partitions 3 \
  --topic timesheet.approved \
  --if-not-exists

docker exec kafka kafka-topics --create \
  --bootstrap-server localhost:9092 \
  --replication-factor 1 \
  --partitions 3 \
  --topic timesheet.rejected \
  --if-not-exists

docker exec kafka kafka-topics --create \
  --bootstrap-server localhost:9092 \
  --replication-factor 1 \
  --partitions 3 \
  --topic invoice.generated \
  --if-not-exists

docker exec kafka kafka-topics --create \
  --bootstrap-server localhost:9092 \
  --replication-factor 1 \
  --partitions 3 \
  --topic user.notification \
  --if-not-exists

echo "Listando topics creados..."
docker exec kafka kafka-topics --list --bootstrap-server localhost:9092

echo "Topics creados exitosamente!"