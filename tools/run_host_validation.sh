#!/usr/bin/env bash
set -euo pipefail
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
cd "$ROOT"
python3 tools/host_environment_report.py
python3 tools/verify_offline_policy.py
python3 tools/verify_release_truth.py
python3 tools/verify_build_reproducibility.py
if [[ ! -x ./gradlew ]]; then
  echo "BLOCKED: gradlew is missing. Add the pinned Gradle wrapper before claiming reproducible build validation." >&2
  exit 20
fi
if [[ ! -d "third_party/llama.cpp/.git" ]]; then
  echo "BLOCKED: vendored llama.cpp is missing. Run tools/integrate_llama_android.sh on a connected build host, then commit the resulting source." >&2
  exit 21
fi
if [[ -z "${ANDROID_SDK_ROOT:-}${ANDROID_HOME:-}" ]]; then
  echo "BLOCKED: Android SDK root is not configured." >&2
  exit 22
fi
if [[ ! -d "${ANDROID_SDK_ROOT:-${ANDROID_HOME}}/ndk/29.0.13113456" ]]; then
  echo "BLOCKED: required NDK 29.0.13113456 is missing." >&2
  exit 23
fi
./gradlew --no-daemon :app:testDebugUnitTest :app:assembleRelease
