#!/bin/bash

BASE_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
JAR_DIR="$BASE_DIR"
CONFIG_DIR="$BASE_DIR/config"

get_pid() {
  local jar_name=$1
  ps -ef | grep "$jar_name" | grep -v grep | awk '{print $2}'
}

start_app() {
  local name=$1
  local jar_path="$JAR_DIR/$name/power-trading-$name.jar"
  local config_file="$CONFIG_DIR/$name.yml"

  if [[ ! -f "$jar_path" ]]; then
    echo "JAR 文件不存在: $jar_path"
    exit 1
  fi
  if [[ ! -f "$config_file" ]]; then
    echo "配置文件不存在: $config_file"
    exit 1
  fi

  local pid
  pid=$(get_pid "$jar_path")
  if [[ -n "$pid" ]]; then
    echo "$name 已经运行，PID: $pid"
    return
  fi

  echo "启动 $name ..."
  nohup java -jar "$jar_path" --spring.config.location="$config_file" > /dev/null 2>&1 &
  sleep 1
  pid=$(get_pid "$jar_path")
  if [[ -n "$pid" ]]; then
    echo "$name 启动成功，PID: $pid"
  else
    echo "启动失败"
  fi
}

stop_app() {
  local name=$1
  local jar_path="$JAR_DIR/$name/power-trading-$name.jar"
  local pid
  pid=$(get_pid "$jar_path")
  if [[ -z "$pid" ]]; then
    echo "$name 未运行"
    return
  fi
  echo "停止 $name, PID: $pid"
  kill "$pid"
  sleep 1
  if kill -0 "$pid" 2>/dev/null; then
    echo "停止失败，尝试强制终止"
    kill -9 "$pid"
  else
    echo "已停止"
  fi
}

status_app() {
  local name=$1
  local jar_path="$JAR_DIR/$name/power-trading-$name.jar"
  local pid
  pid=$(get_pid "$jar_path")
  if [[ -n "$pid" ]]; then
    echo "$name 正在运行，PID: $pid"
  else
    echo "$name 未运行"
  fi
}

case "$1" in
  server|web)
    APP="$1"
    case "$2" in
      start)
        start_app "$APP"
        ;;
      stop)
        stop_app "$APP"
        ;;
      status)
        status_app "$APP"
        ;;
      *)
        echo "用法: $0 {server|web} {start|stop|status}"
        exit 1
        ;;
    esac
    ;;
  *)
    echo "用法: $0 {server|web} {start|stop|status}"
    exit 1
    ;;
esac
