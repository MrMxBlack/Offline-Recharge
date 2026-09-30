#!/usr/bin/env bash
set -euo pipefail
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
cd "$ROOT"
python3 tools/verify_offline_policy.py
python3 tools/validate_offline_repo.py
python3 tools/verify_android_build_contract.py
python3 tools/verify_build_reproducibility.py
python3 tools/verify_release_truth.py
printf 'PASS: static release audit completed; runtime evidence remains unverified.\n'
