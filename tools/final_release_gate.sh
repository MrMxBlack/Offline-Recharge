#!/usr/bin/env bash
set -euo pipefail
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
cd "$ROOT"
python3 tools/verify_offline_policy.py
python3 tools/verify_release_truth.py
bash tools/check_release.sh
if [[ "${REQUIRE_NATIVE:-0}" == "1" ]]; then
  bash tools/verify_native_source.sh
  bash tools/verify_native_integration.sh
  bash tools/verify_upstream_contract.sh
fi
if [[ "${REQUIRE_BUILD:-0}" == "1" ]]; then
  [[ -x ./gradlew ]] || { echo 'FAIL: Gradle wrapper missing; build cannot be claimed reproducible' >&2; exit 20; }
  ./gradlew :app:testDebugUnitTest :app:assembleRelease
fi
python3 tools/verify_release_truth.py
echo 'PASS: final release gate'
