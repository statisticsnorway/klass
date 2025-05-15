#!/bin/bash

# Load variables from .env
set -a
source .env
set +a

# Start containers
docker compose up -d

echo "Wait for mariadb to be ready"
sleep 10

echo "Load data from file to mariadb database"
docker compose exec -T mariadb mariadb -u root -p"$MARIADB_ROOT_PASSWORD" --host=127.0.0.1 klass < "$MARIADB_LOCAL_FILEPATH"

echo "Wait for load from file to finish"
sleep 10

echo "Load data from file to postgresql database"
docker compose exec -T postgresql psql -U "$POSTGRES_USER" -d "klass" < "$POSTGRES_LOCAL_FILEPATH"

echo "Wait for load from file to finish"
sleep 10

echo "Build klass-api image"
docker compose build

sleep 10

echo "Restart klass api mariadb"
docker compose restart klass-api-mariadb

sleep 10

echo "Restart klass api"
docker compose restart klass-api