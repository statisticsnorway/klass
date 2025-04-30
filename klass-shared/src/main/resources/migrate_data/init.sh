# Load variables from .env
set -a
source .env
set +a

# Start containers
docker compose up -d

# Wait
sleep 5

# Populate mariadb database with data from file
docker compose exec -T mariadb mariadb -u root -p"$MARIADB_ROOT_PASSWORD" --host=127.0.0.1 klass < "$MARIADB_LOCAL_FILEPATH"