#!/bin/bash
set -e

WATCH_DIRS="src/main"
POLL_INTERVAL_SECONDS="${POLL_INTERVAL_SECONDS:-5}"      # Default 5 seconds, can be set from env
DEBOUNCE_INTERVAL_SECONDS="${DEBOUNCE_INTERVAL_SECONDS:-2}" # Debounce 2 seconds after detecting changes
MIN_COMPILATION_INTERVAL_SECONDS="${MIN_COMPILATION_INTERVAL_SECONDS:-10}" # Minimum 10 seconds between compilations

echo "Starting polling watcher:"
echo "  - Watch dirs: $WATCH_DIRS"
echo "  - Poll interval: ${POLL_INTERVAL_SECONDS}s"
echo "  - Debounce interval: ${DEBOUNCE_INTERVAL_SECONDS}s"
echo "  - Min compilation interval: ${MIN_COMPILATION_INTERVAL_SECONDS}s"

# Background watcher: compile on changes (works on Windows bind mounts too)
(
  last_signature=""
  last_compilation_time=0
  change_detected_time=0
  is_processing=false

  while true; do
    current_signature=$(find $WATCH_DIRS -type f \( -name "*.java" -o -name "*.html" -o -name "*.css" -o -name "*.js" -o -name "*.yml" -o -name "*.yaml" -o -name "*.properties" \) \
      -printf "%T@ %p\n" | sort | sha256sum | awk '{print $1}')

    current_time=$(date +%s)

    # If changes detected
    if [ "$current_signature" != "$last_signature" ]; then
      if [ -n "$last_signature" ] && [ "$is_processing" = false ]; then
        change_detected_time=$current_time
        is_processing=true
        echo "[$(date '+%H:%M:%S')] Change detected, debouncing for ${DEBOUNCE_INTERVAL_SECONDS}s..."
      fi
      last_signature="$current_signature"
    
    # If change detected and debounce time passed
    elif [ "$is_processing" = true ]; then
      time_since_change=$((current_time - change_detected_time))
      time_since_compilation=$((current_time - last_compilation_time))
      
      if [ $time_since_change -ge $DEBOUNCE_INTERVAL_SECONDS ] && [ $time_since_compilation -ge $MIN_COMPILATION_INTERVAL_SECONDS ]; then
        echo "[$(date '+%H:%M:%S')] Compiling..."
        if mvn -DskipTests=true compile > /tmp/compile.log 2>&1; then
          echo "[$(date '+%H:%M:%S')] ✓ Compilation successful"
        else
          echo "[$(date '+%H:%M:%S')] ✗ Compilation failed:"
          tail -20 /tmp/compile.log
        fi
        last_compilation_time=$current_time
        is_processing=false
      fi
    fi

    sleep "$POLL_INTERVAL_SECONDS"
  done
) &

echo "Starting Spring Boot application on port 8001"
exec mvn spring-boot:run -Dspring-boot.run.fork=false -Dmaven.test.skip=true
