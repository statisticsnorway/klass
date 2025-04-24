#!/bin/bash

LOAD_DIR="./pgloader"

# Log file for errors
LOG_FILE="./pgloader_errors.log" > "$LOG_FILE"

echo "Starting pgloader load scripts..."

for file in "$LOAD_DIR"/*.load; do
  if [ -f "$file" ]; then
    echo "Running: $file"
    pgloader "$file" >> "${file}.log" 2>>"$LOG_FILE"

    if [ $? -ne 0 ]; then
      echo "Failed: $file (see $LOG_FILE)"
    else
      echo "Success: $file"
    fi
  fi
done

echo "All done."