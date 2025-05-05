# Load variables from .env
set -a
source .env
set +a

# Start containers
docker compose up -d --build

# Wait
#sleep 5

echo "Waiting for MariaDB to be ready..."
until docker compose exec mariadb mariadb-admin -uroot -p"rootpassword" ping --silent; do
  sleep 1
done

# Populate mariadb database with data from file
docker compose exec -T mariadb mariadb -u root -p"rootpassword" --host=127.0.0.1 klass < "$MARIADB_LOCAL_FILEPATH"

# Migrate data from mariadb database to postgres database
migrate_data/pgloader/load.sh

#echo "Dump postgres"
# Dump data from postgres database to file
#pg_dump -U klass -h localhost -p 5432 klass > "$POSTGRES_LOCAL_FILEPATH"/"$POSTGRES_DUMP_FILENAME"

#echo "Restart klass-api"
#docker compose restart klass-api
