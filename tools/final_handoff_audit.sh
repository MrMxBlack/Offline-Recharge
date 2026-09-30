#!/usr/bin/env bash
set -euo pipefail
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
cd "$ROOT"
python3 tools/verify_archive_layout.py
python3 tools/verify_offline_policy.py
python3 tools/validate_offline_repo.py
python3 tools/verify_android_build_contract.py
python3 tools/verify_release_truth.py
python3 -m py_compile tools/*.py
for f in tools/*.sh; do bash -n "$f"; done
printf '\nPASS: final static handoff audit completed. Runtime/build/device gates remain explicit.\n'
