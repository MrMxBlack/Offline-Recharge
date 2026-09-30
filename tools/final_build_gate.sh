#!/usr/bin/env bash
set -euo pipefail
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
cd "$ROOT"

[[ -x ./gradlew ]] || { echo "BLOCKED: committed Gradle Wrapper missing" >&2; exit 20; }
[[ -s ./gradle/wrapper/gradle-wrapper.jar ]] || { echo "BLOCKED: Gradle Wrapper JAR missing" >&2; exit 21; }
[[ -s ./gradle/wrapper/gradle-wrapper.properties ]] || { echo "BLOCKED: Gradle Wrapper properties missing" >&2; exit 22; }
[[ -d third_party/llama.cpp/.git ]] || { echo "BLOCKED: pinned llama.cpp source is not vendored" >&2; exit 23; }
[[ -d llama ]] || { echo "BLOCKED: Android llama module is missing" >&2; exit 24; }

python3 tools/verify_build_reproducibility.py
python3 tools/verify_android_build_contract.py
bash tools/verify_native_integration.sh
bash tools/verify_native_source.sh
bash tools/verify_upstream_contract.sh
python3 tools/verify_offline_policy.py
python3 tools/verify_release_truth.py

./gradlew --no-daemon :app:testDebugUnitTest :app:assembleRelease

APK="$(find app/build/outputs/apk/release -maxdepth 1 -type f -name '*.apk' | head -1)"
[[ -n "$APK" && -s "$APK" ]] || { echo "FAIL: release APK not produced" >&2; exit 30; }
sha256sum "$APK" | tee evidence/release_apk.sha256
printf 'PASS: final release build completed: %s\n' "$APK"
