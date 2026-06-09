#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "$0")" && pwd)"

if ! command -v java >/dev/null 2>&1; then
  echo "Java 17+ is required."
  exit 1
fi

if ! command -v mvn >/dev/null 2>&1; then
  echo "Maven 3.9+ is required."
  exit 1
fi

if [ ! -d "$ROOT_DIR/frontend/node_modules" ]; then
  npm --prefix "$ROOT_DIR/frontend" install
fi

cleanup() {
  kill "${BACKEND_PID:-}" "${FRONTEND_PID:-}" 2>/dev/null || true
}
trap cleanup EXIT INT TERM

(cd "$ROOT_DIR/backend" && mvn spring-boot:run) &
BACKEND_PID=$!

npm --prefix "$ROOT_DIR/frontend" run dev &
FRONTEND_PID=$!

wait "$BACKEND_PID" "$FRONTEND_PID"
