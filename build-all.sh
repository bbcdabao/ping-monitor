#!/bin/bash

# 获取当前脚本的绝对目录（无论从哪里运行都有效）
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

# 切换到该目录
cd "$SCRIPT_DIR" || exit 1
echo "Switch to the project directory"
echo "$SCRIPT_DIR"

echo "mvn clean..."
mvn clean || { echo "mvn clean fail"; exit 1; }

echo "mvn install..."
mvn install || { echo "mvn install fail"; exit 1; }

echo "mvn install finish."

VERSION_DIR="$SCRIPT_DIR/ping-robot-version"
mkdir -p "$VERSION_DIR/configs" "$VERSION_DIR/pids" "$VERSION_DIR/logs"

echo "copy  files to ping-robot-version"

cp "$SCRIPT_DIR/ping-robot/ping-robot-man/target/ping-robot-man-1.0.0.jar" "$VERSION_DIR/ping-robot-man.jar" || { echo "copy ping-robot-man jar fail."; exit 1; }
cp -r "$SCRIPT_DIR/ping-robot/ping-robot-package/configs/." "$VERSION_DIR/configs/" || { echo "copy config fail."; exit 1; }
cp "$SCRIPT_DIR/ping-robot/man.sh" "$VERSION_DIR" || { echo "copy man.sh fail."; exit 1; }
chmod +x "$VERSION_DIR/man.sh" || { echo "set man.sh executable fail."; exit 1; }

echo "package ping-robot-version.tar.gz"
tar -czvf ping-robot-version.tar.gz ping-robot-version || { echo "package fail."; exit 1; }

echo "all done"