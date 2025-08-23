#!/bin/bash

set -euo pipefail

log() {
  echo "[$(date '+%H:%M:%S')] $1" >&2
}

mvn_build() {
  log "Cleaning project..."
  mvn clean
  log "Installing project..."
  mvn install
  log "Maven build finished."
}

# Create a version package directory
# 创建版本包目录
create_version_dir() {
  local version_name=$1
  local version_dir="$BASE_DIR/${version_name}-version"

  if [ -d "$version_dir" ]; then
    log "Removing old $version_dir"
    rm -rf "$version_dir"
  fi

  mkdir -p "$version_dir/configs" "$version_dir/pids" "$version_dir/logs"
  echo "$version_dir"
}

# Batch copy files/directories
# Arg1: target version package directory
# Arg2...: mapping array (source:destination)
# 批量复制文件/目录
# 参数1: 目标版本包目录
# 参数2...: 映射数组 (源:目标)
copy_files() {
  local version_dir=$1
  shift
  local mappings=("$@")

  for mapping in "${mappings[@]}"; do
    local src=$(echo "$mapping" | cut -d':' -f1)
    local dst=$(echo "$mapping" | cut -d':' -f2)

    if [ -d "$src" ]; then
      # 源是目录 → 整个目录复制到目标目录
      log "Copying directory $src -> $version_dir/$dst"
      cp -r "$src" "$version_dir/$dst"
    else
      # 源是文件
      log "Copying file $src -> $version_dir/$dst"
      cp "$src" "$version_dir/$dst"
    fi
  done
}

# begin--------------------

# Get the directory where the script is located
# 获取脚本所在目录
BASE_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$BASE_DIR"

# run build
# 执行构建
# mvn_build

###################
# manager version #
###################
VERSION_MANAGER_DIR=$(create_version_dir "ping-manager")
copy_files "$VERSION_MANAGER_DIR" \
  "$BASE_DIR/ping-manager/target/ping-manager-1.0.0.jar:ping-manager.jar" \
  "$BASE_DIR/ping-manager-web/target/ping-manager-web.jar:ping-manager-web.jar" \
  "$BASE_DIR/ping-manager/src/main/resources/application.yml:configs/manager.yml" \
  "$BASE_DIR/ping-manager-web/src/main/resources/application.yml:configs/manager-web.yml" \
  "$BASE_DIR/version-package-related/ctl.sh:ctl.sh"

chmod +x "$VERSION_MANAGER_DIR/ctl.sh"
tar -czvf ping-manager-version.tar.gz "$(basename "$VERSION_MANAGER_DIR")"

#################
# robot version #
#################
VERSION_ROBOT_DIR=$(create_version_dir "ping-robot")
copy_files "$VERSION_ROBOT_DIR" \
  "$BASE_DIR/ping-robot/ping-robot-man/target/ping-robot-man-1.0.0.jar:ping-robot-man.jar" \
  "$BASE_DIR/version-package-related/robot-sample-configs/:configs/" \
  "$BASE_DIR/version-package-related/man.sh:man.sh"

chmod +x "$VERSION_ROBOT_DIR/man.sh"
tar -czvf ping-robot-version.tar.gz "$(basename "$VERSION_ROBOT_DIR")"

# overd--------------------

echo "all done"