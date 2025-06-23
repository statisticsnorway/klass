#!/bin/bash

# Load variables from .env
set -a
source .env
set +a

# Start containers
docker compose --profile migration-testing up -d

echo "Wait for mariadb to be ready"
sleep 20

echo "Load data from file to mariadb database"
docker compose --profile migration-testing exec -T mariadb mariadb -u root -p"$MARIADB_ROOT_PASSWORD" --host=127.0.0.1 klass < "$MARIADB_LOCAL_FILEPATH"

echo "Wait for load from file to finish"
sleep 10

echo "Load data from file to postgresql database"
docker compose --profile migration-testing exec -T postgresql psql -U "$POSTGRES_USER" -d "klass" < "$POSTGRES_LOCAL_FILEPATH"

echo "Wait for load from file to finish"
sleep 10

echo "Restart klass api mariadb"
docker compose --profile migration-testing restart klass-api-mariadb

sleep 10

echo "Restart klass api"
docker compose --profile migration-testing restart klass-api