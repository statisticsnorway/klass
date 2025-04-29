set -a
source .env
set +a

export PGPASSWORD=${POSTGRES_PASSWORD}
pg_dump -U klass -h localhost -p 5432 klass > "$POSTGRES_LOCAL_FILEPATH"/klass-postgres-dump.sql
