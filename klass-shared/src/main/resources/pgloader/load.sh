#!/bin/bash

# Load variables from .env
set -a
source .env
set +a

# Loop over all template load files
for template in load*.load; do
  echo "Processing $template..."

  # Expand environment variables and run pgloader
  envsubst < "$template" | pgloader /dev/stdin

done