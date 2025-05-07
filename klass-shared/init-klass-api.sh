#!/bin/bash

# This script depends on 'init-migration.sh'.
# Services can be stopped, but not removed

# Load variables from .env
set -a
source .env
set +a

# Check if postgresql container is running
DB_CONTAINER_NAME="postgresql"

DB_STATUS=$(docker compose ps --services --filter "status=running" | grep -w "$DB_CONTAINER_NAME")

if [ -z "$DB_STATUS" ]; then
  echo "Database container '$DB_CONTAINER_NAME' is not running. Restarting it..."
  docker compose restart "$DB_CONTAINER_NAME"
else
  echo "Database container '$DB_CONTAINER_NAME' is running."
fi

echo "Build klass-api image"
docker compose build

echo "Restart klass api"
docker compose restart klass-api