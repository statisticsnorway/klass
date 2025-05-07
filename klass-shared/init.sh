#!/bin/bash

# Load variables from .env
set -a
source .env
set +a

# Start containers
docker compose up -d

# Wait for mariadb to be ready
sleep 10

# Populate mariadb database with data from file
docker compose exec -T mariadb mariadb -u root -p"$MARIADB_ROOT_PASSWORD" --host=127.0.0.1 klass < "$MARIADB_LOCAL_FILEPATH"

# Wait for load from file to finish
sleep 10

# Migrate data from mariadb database to postgres database
migrate_data/pgloader/load.sh

# Wait for migration to finish
sleep 10

# Run necessary postgresql alteration on type timestamp to remove microseconds
docker cp alter_timestamp.sql "$(docker compose ps -q postgresql)":/tmp/alter_timestamp.sql
docker compose exec -T postgresql psql -U klass -d klass -f /tmp/alter_timestamp.sql

#echo "Dump postgres"
# Dump data from postgres database to file
pg_dump -U klass -h localhost -p 5432 klass > "$POSTGRES_LOCAL_FILEPATH"/"$POSTGRES_DUMP_FILENAME"

# Build klass-api image
docker compose build

# Restart klass-api because the application will crash when db are not ready
#echo "Restart klass-api"
docker compose restart klass-api
