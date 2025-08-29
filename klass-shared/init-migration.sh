#!/bin/bash

# Load variables from .env
set -a
source .env
set +a

# Start containers
docker compose --profile migrate-data up -d

echo "Wait for mariadb to be ready"
sleep 10

echo "Load data from file to mariadb database"
docker compose --profile migrate-data exec -T mariadb mariadb -u root -p"$MARIADB_ROOT_PASSWORD" --host=127.0.0.1 klass < "$MARIADB_LOCAL_FILEPATH"

echo "Wait for load from file to finish"
sleep 10

echo "Migrate data from mariadb database to postgres database"
migrate_data/pgloader/load.sh

echo "Wait for migration to finish"
sleep 10

# Run necessary postgresql alteration on type timestamp to remove microseconds
echo "Alter columns of type timestamp"
docker cp alter_timestamp.sql "$(docker compose ps -q postgresql)":/tmp/alter_timestamp.sql
docker compose --profile migrate-data exec -T postgresql psql -U klass -d klass -f /tmp/alter_timestamp.sql

echo "Dump data from postgres database to file"
pg_dump -U klass -h localhost -p 5432 klass > "$POSTGRES_LOCAL_FILEPATH"