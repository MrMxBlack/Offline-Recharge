#!/usr/bin/env bash
set -euo pipefail
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
cd "$ROOT"
python3 tools/verify_offline_policy.py
python3 tools/validate_offline_repo.py
python3 tools/build_corpus.py
python3 tools/run_benchmark.py
python3 tools/generate_release_manifest.py
bash tools/check_release.sh
printf '\nV5 preparation complete. Native/device/offline packet gates are still pending until measured on real hardware.\n'
