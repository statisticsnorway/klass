# Load env values from file
set -a
source .env
set +a

# Set pg_dump env password
export PGPASSWORD=${POSTGRES_PASSWORD}
# Dump data to file
pg_dump -U klass -h localhost -p 5432 klass > "$POSTGRES_LOCAL_FILEPATH"/klass-postgres-dump.sql
