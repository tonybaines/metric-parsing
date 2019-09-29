#!/usr/bin/env bash

echo "Expects a server running on http://localhost:8080"
http --verbose localhost:8080 record='another.metric.value.cpu% 1.23e-1 1562763195000'