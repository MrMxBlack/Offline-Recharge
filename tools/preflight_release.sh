#!/usr/bin/env bash
set -euo pipefail
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
cd "$ROOT"

echo "== Offline policy =="
python3 tools/verify_offline_policy.py

echo "== Repository audit =="
python3 tools/validate_offline_repo.py

echo "== Python syntax =="
python3 -m py_compile tools/*.py

echo "== Shell syntax =="
bash -n tools/*.sh scripts/*.sh

echo "== Release truth checks =="
python3 - <<'PY'
import json, pathlib, re
root=pathlib.Path('.')
status=json.loads((root/'RELEASE_STATUS.json').read_text())
assert status['version']=='FINAL-1.3', status
assert status['bounty_ready'] is False
for k in ('native_inference_verified','physical_device_verified','offline_network_verified','real_benchmark_verified','reproducible_release_verified'):
    assert status[k] is False, (k,status[k])
text=(root/'docs/FINAL_TRUTH_STATUS.md').read_text()
for phrase in ('NOT VERIFIED','NOT RUN','NOT AVAILABLE','Bounty readiness'):
    assert phrase in text
print('PASS: FINAL-1.3 truth boundary is internally consistent')
PY

echo "PRELIGHT PASS: source package is statically consistent; physical-device gates remain open."
