docker compose up -d
sleep 5
docker compose exec -T mariadb mariadb -u root -prootpassword --host=127.0.0.1 klass < "$MARIADB_LOCAL_FILEPATH"