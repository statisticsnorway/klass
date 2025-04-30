#!/bin/bash

# Resolve directory
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"

# Loop over all template load files
for template in "$SCRIPT_DIR"/load*.load; do
  echo "Processing $template..."

  # Expand environment variables and run pgloader
  envsubst < "$template" | pgloader /dev/stdin

done