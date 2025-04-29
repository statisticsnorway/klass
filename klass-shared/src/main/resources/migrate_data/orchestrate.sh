#!/bin/bash
set -e

set -a
source .env
set +a


# Run the scripts
#echo "Starting mariadb container..."
#./mariadb-local/init.sh

#echo "Starting postgres container..."
#./postgres-local/init.sh

#echo "Loading data from mariadb to postgres..."
#./pgloader/load.sh

#echo "Dumping data from postgres to file..."
#./postgres-local/dump_postgres.sh


# 1. Start MariaDB and load data
echo "Starting MariaDB..."
(cd mariadb-local && ./init.sh)

# 2. Start Postgres
echo "Starting Postgres..."
(cd postgres-local && ./init.sh)

# 3. Load data using pgloader
echo "Loading data from MariaDB to Postgres..."
(cd pgloader && ./load.sh)

# 4. Export Postgres to file
echo "Dumping Postgres data..."
(cd postgres-local && ./dump_postgres.sh)


