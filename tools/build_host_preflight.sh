#!/usr/bin/env bash
set -euo pipefail
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
cd "$ROOT"
fail(){ echo "FAIL: $*" >&2; exit 2; }
command -v java >/dev/null || fail "Java/JDK is missing"
command -v python3 >/dev/null || fail "python3 is missing"
[[ -x ./gradlew ]] || fail "Gradle wrapper missing; add a pinned wrapper before claiming reproducible builds"
[[ -n "${ANDROID_HOME:-${ANDROID_SDK_ROOT:-}}" ]] || fail "ANDROID_HOME/ANDROID_SDK_ROOT is missing"
SDK="${ANDROID_HOME:-$ANDROID_SDK_ROOT}"
[[ -d "$SDK" ]] || fail "Android SDK not found: $SDK"
[[ -n "${ANDROID_NDK_HOME:-}" ]] || echo "WARN: ANDROID_NDK_HOME is not set; build script will use the pinned NDK path"
python3 tools/verify_offline_policy.py
python3 tools/verify_release_truth.py
if [[ -d third_party/llama.cpp/.git ]]; then
  bash tools/verify_native_source.sh
  bash tools/verify_native_integration.sh
  bash tools/verify_upstream_contract.sh
else
  echo "INFO: llama.cpp source not vendored; online integration bootstrap is required once before air-gapped release builds"
fi
echo "PASS: build host preflight"
