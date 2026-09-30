#!/usr/bin/env bash
set -euo pipefail
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
cd "$ROOT"
echo "== Offline Research current release audit =="
python3 tools/verify_offline_policy.py
python3 tools/validate_offline_repo.py
python3 tools/run_benchmark.py >/dev/null
python3 tools/generate_release_manifest.py >/dev/null
if grep -Rni --exclude-dir=.git --exclude-dir=docs --exclude='AndroidManifest.xml' -E 'https?://|INTERNET' app/src/main 2>/dev/null; then
  echo "ERROR: network-like string found in runtime source"
  exit 3
fi
if [[ ! -d third_party/llama.cpp && ! -d llama ]]; then
  echo "PENDING: pinned llama.cpp source is not present in this archive."
else
  echo "FOUND: llama.cpp source tree present; exact commit must still be recorded."
fi
if [[ "$(python3 - <<'PY'
import json
x=json.load(open('RELEASE_STATUS.json')); print(x.get('bounty_ready',False))
PY
)" == "True" ]]; then
  echo "ERROR: release status cannot claim bounty_ready before physical verification"
  exit 4
fi
echo "PASS: static release checks completed; physical-device gates remain pending."
