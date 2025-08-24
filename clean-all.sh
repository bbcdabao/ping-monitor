#!/bin/bash

set -euo pipefail

log() {
  echo "[$(date '+%H:%M:%S')] $1" >&2
}

# Get the directory where the script is located
# 获取脚本所在目录
BASE_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$BASE_DIR"

clean_all() {
  log "Running mvn clean..."
  mvn clean

  log "Removing version directories and tarballs..."
  rm -rf "$BASE_DIR"/ping-manager-version*
  rm -rf "$BASE_DIR"/ping-robot-version*
  rm -f "$BASE_DIR"/manager.tar.gz
  rm -f "$BASE_DIR"/robot.tar.gz

  log "Clean finished."
}

clean_all