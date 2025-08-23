#!/bin/bash

BASE_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
JAR_DIR="$BASE_DIR"
CONFIG_DIR="$BASE_DIR/configs"
LOG_DIR="$BASE_DIR/logs"
PID_DIR="$BASE_DIR/pids"

mkdir -p "$PID_DIR"

start_app() {
  local name=$1

  local jar_path="$JAR_DIR/ping-$name.jar"
  local config_file="$CONFIG_DIR/$name.yml"

  local pid_file="$PID_DIR/${name}.pid"

  if [[ ! -f "$config_file" ]]; then
    echo "❌ config not exist: $config_file"
    exit 1
  fi

  if [[ ! -f "$jar_path" ]]; then
    echo "❌ JAR not exist: $jar_path"
    exit 1
  fi
  
  if [[ -f "$pid_file" ]]; then
    echo "⚠️ pid file exist: $pid_file maybe running, stop or check frist please."
    exit 1
  fi

  echo "🚀 start $name ..."
  nohup java -DAPPNAME="$name" -DLOG_PATH="$LOG_DIR" -jar "$jar_path" --spring.config.location="$config_file" > /dev/null 2>&1 &
  echo $! > "$pid_file"
  echo "✅ $name started (PID: $(cat "$pid_file"))"
}

check_status() {
  echo -e "📋Status：\n"
  printf "%-35s %-10s %-20s\n" "PID File" "PID" "Status"
  echo "---------------------------------------------------------------"

  for pid_file in "$PID_DIR"/*.pid; do
    [[ -f "$pid_file" ]] || continue
    local pid=$(cat "$pid_file")
    local fname=$(basename "$pid_file")
    if [[ -n "$pid" && -d "/proc/$pid" ]]; then
      printf "%-35s %-10s %-10s\n" "$fname" "$pid" "runing ✅"
    else
	  printf "%-35s %-10s %-10s\n" "$fname" "$pid" "over  (Had delete PID file) ❌"
      rm -f "$pid_file"
    fi
  done
}

stop_app() {
  local name=$1
  local pid_file="$PID_DIR/${name}.pid"

  if [[ ! -f "$pid_file" ]]; then
    echo "❌ pid file not exist: $pid_file"
    exit 1
  fi
  
  local pid=$(cat "$pid_file")
  if [[ -z "$pid" || ! -d "/proc/$pid" ]]; then
    echo "⚠️ Instance $name Exited or invalid PID !"
  else
    echo "🛑 Stop instance $name (PID: $pid)"
    kill "$pid"
  fi
  rm -f "$pid_file"
}

case "$1" in
  start)
    start_app "$2"
    ;;
  stop)
    stop_app "$2"
    ;;
  check)
    check_status
    ;;
  *)
  	echo -e "📋Helps:\n"
	echo "1) $0 start xxx"
	echo "2) $0 stop xxx"
	echo "3) $0 check"
    ;;
esac
