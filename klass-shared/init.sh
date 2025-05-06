#!/bin/bash

# Load variables from .env
set -a
source .env
set +a

# Start containers
docker compose up -d

#--build

# Wait
sleep 10

#echo "Waiting for MariaDB to be ready..."
#until docker compose exec mariadb mariadb-admin -uroot -p"rootpassword" ping --silent; do
#  sleep 1
#done

# Populate mariadb database with data from file
docker compose exec -T mariadb mariadb -u root -p"$MARIADB_ROOT_PASSWORD" --host=127.0.0.1 klass < "$MARIADB_LOCAL_FILEPATH"

sleep 10
# Migrate data from mariadb database to postgres database
migrate_data/pgloader/load.sh

# Check the data is indeed migrated/tables are created
sleep 10

docker cp alter_timestamp.sql "$(docker compose ps -q postgresql)":/tmp/alter_timestamp.sql
docker compose exec -T postgresql psql -U klass -d klass -f /tmp/alter_timestamp.sql

#docker compose exec -T postgresql psql -U klass -d klass <<EOF
#SELECT table_schema,
#       table_name,
#       privilege_type
#  FROM information_schema.role_table_grants
#  WHERE grantee = 'klass' AND table_schema = 'klass';
#EOF
#echo "Dump postgres"
# Dump data from postgres database to file
#pg_dump -U klass -h localhost -p 5432 klass > "$POSTGRES_LOCAL_FILEPATH"/"$POSTGRES_DUMP_FILENAME"

docker compose build
#echo "Restart klass-api"
docker compose restart klass-api
