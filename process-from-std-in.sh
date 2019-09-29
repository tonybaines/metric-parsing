#!/usr/bin/env bash

if [[ ! -f build/libs/metric-parsing-1.0-SNAPSHOT-all.jar ]]; then
  ./gradlew clean assemble
fi

# Output starts immediately
for i in {1..10000}; do cat src/test/resources/raw-metrics-data.txt ; done | java -jar build/libs/metric-parsing-1.0-SNAPSHOT-all.jar