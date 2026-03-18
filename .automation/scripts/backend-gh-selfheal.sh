#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
ALERT_DIR="$ROOT_DIR/.automation/alerts"
CMD_FILE="$ROOT_DIR/.automation/backend.resume.cmd"
TODAY="$(date +%F)"
NOW="$(date --iso-8601=seconds 2>/dev/null || date -Iseconds)"

mkdir -p "$ALERT_DIR"

echo "[1/5] Checking GitHub CLI auth..."
if ! gh auth status -h github.com >/dev/null 2>&1; then
  echo "❌ gh auth is not valid for github.com"
  echo "   Run: gh auth login -h github.com -p https -s repo,workflow,read:org,gist"
  exit 1
fi

echo "[2/5] Verifying API token works..."
if ! gh api user --jq '.login' >/dev/null 2>&1; then
  echo "❌ gh token exists but API call failed"
  exit 1
fi

if git -C "$ROOT_DIR" remote get-url origin >/dev/null 2>&1; then
  REPO_SLUG="$(git -C "$ROOT_DIR" remote get-url origin | sed -E 's#(git@github.com:|https://github.com/)##; s#\.git$##')"
  if [[ -n "$REPO_SLUG" ]]; then
    echo "[3/5] Verifying repo access for $REPO_SLUG..."
    gh repo view "$REPO_SLUG" --json name >/dev/null
  fi
fi

echo "[4/5] Marking backend auth recovered + clearing fail marker(s)..."
shopt -s nullglob
for f in "$ALERT_DIR"/gh-auth-fail-backend-*.marker; do
  rm -f "$f"
  echo "   removed $(basename "$f")"
done
shopt -u nullglob

echo "$NOW gh auth recovered for backend automation" > "$ALERT_DIR/gh-auth-recovered-backend-$TODAY.marker"

RESUME_CMD="${BOXVISTA_BACKEND_RESUME_CMD:-}"
if [[ -z "$RESUME_CMD" && -f "$CMD_FILE" ]]; then
  RESUME_CMD="$(grep -vE '^\s*(#|$)' "$CMD_FILE" | head -n1 || true)"
fi

echo "[5/5] Resume backend automation..."
if [[ -n "$RESUME_CMD" ]]; then
  echo "   running: $RESUME_CMD"
  bash -lc "$RESUME_CMD"
  echo "✅ Backend automation resume command finished"
else
  echo "⚠️ No resume command configured."
  echo "   Set BOXVISTA_BACKEND_RESUME_CMD or write command to $CMD_FILE"
  echo "   Example: boxvista automation resume --domain backend"
fi

echo "✅ Self-heal completed"
