# Load variables from .env
set -a
source .env
set +a

docker compose up -d
sleep 5
docker compose exec -T mariadb mariadb -u root -p"$MARIADB_ROOT_PASSWORD" --host=127.0.0.1 klass < "$MARIADB_LOCAL_FILEPATH"