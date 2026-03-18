#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
ALERT_DIR="$ROOT_DIR/.automation/alerts"
STATE_DIR="$ROOT_DIR/.automation/state"
STATE_FILE="$STATE_DIR/backend-gh-autofix.last"
SELFHEAL="$ROOT_DIR/.automation/scripts/backend-gh-selfheal.sh"
LOG_FILE="$ROOT_DIR/.automation/state/backend-gh-autofix.log"

mkdir -p "$STATE_DIR"

# Pick the newest backend auth fail marker (if any)
LATEST_FAIL="$(ls -1t "$ALERT_DIR"/gh-auth-fail-backend-*.marker 2>/dev/null | head -n1 || true)"
if [[ -z "$LATEST_FAIL" ]]; then
  exit 0
fi

LAST_DONE=""
if [[ -f "$STATE_FILE" ]]; then
  LAST_DONE="$(cat "$STATE_FILE" 2>/dev/null || true)"
fi

# Avoid reprocessing same marker forever
if [[ "$LATEST_FAIL" == "$LAST_DONE" ]]; then
  exit 0
fi

{
  echo "[$(date -Iseconds)] detected fail marker: $LATEST_FAIL"
  "$SELFHEAL"
  echo "[$(date -Iseconds)] self-heal finished for: $LATEST_FAIL"
} >> "$LOG_FILE" 2>&1 || {
  echo "[$(date -Iseconds)] self-heal failed for: $LATEST_FAIL" >> "$LOG_FILE" 2>&1
  exit 1
}

echo "$LATEST_FAIL" > "$STATE_FILE"
