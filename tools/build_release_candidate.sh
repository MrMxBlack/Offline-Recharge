#!/usr/bin/env bash
set -euo pipefail
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
cd "$ROOT"
fail(){ echo "FAIL: $1" >&2; exit 2; }
command -v bash >/dev/null || fail "bash missing"
command -v python3 >/dev/null || fail "python3 missing"
[[ -x ./gradlew ]] || fail "Gradle wrapper missing. Install a pinned Gradle wrapper before release build."
[[ -n "${ANDROID_HOME:-${ANDROID_SDK_ROOT:-}}" ]] || fail "ANDROID_HOME/ANDROID_SDK_ROOT not set"
SDK="${ANDROID_HOME:-$ANDROID_SDK_ROOT}"
[[ -d "$SDK" ]] || fail "Android SDK not found: $SDK"
NDK="${ANDROID_NDK_HOME:-}"
if [[ -z "$NDK" ]]; then
  NDK_VERSION="29.0.13113456"
  NDK="$SDK/ndk/$NDK_VERSION"
fi
[[ -d "$NDK" ]] || fail "Required NDK missing: $NDK"
export ANDROID_NDK_HOME="$NDK"

python3 tools/verify_offline_policy.py
python3 tools/verify_release_truth.py
bash tools/preflight_release.sh
if [[ ! -d third_party/llama.cpp/.git ]]; then
  if [[ "${OFFLINE_BUILD:-0}" == "1" ]]; then
    fail "OFFLINE_BUILD=1 forbids fetching llama.cpp; vendor the exact pinned source first."
  fi
  echo "INFO: llama.cpp is not vendored; running pinned integration bootstrap."
  bash tools/integrate_llama_android.sh
fi
bash tools/verify_native_source.sh
bash tools/verify_native_integration.sh
bash tools/verify_upstream_contract.sh
./gradlew --no-daemon :app:testDebugUnitTest :app:assembleRelease
APK="app/build/outputs/apk/release/app-release.apk"
[[ -f "$APK" ]] || fail "Release APK not produced"
python3 - "$APK" <<'PY'
from pathlib import Path
import hashlib, json, sys
p=Path(sys.argv[1]); h=hashlib.sha256(p.read_bytes()).hexdigest()
out=Path('evidence/release_artifact.json')
out.write_text(json.dumps({'apk':str(p),'bytes':p.stat().st_size,'sha256':h},indent=2)+'\n')
print(f'APK_SHA256={h}')
PY
python3 tools/verify_release_truth.py
echo "PASS: release candidate APK built: $APK"
