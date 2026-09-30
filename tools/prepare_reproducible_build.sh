#!/usr/bin/env bash
set -euo pipefail
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
cd "$ROOT"

python3 tools/verify_android_build_contract.py
python3 tools/verify_build_reproducibility.py

if [[ ! -x "$ROOT/gradlew" || ! -f "$ROOT/gradle/wrapper/gradle-wrapper.jar" || ! -f "$ROOT/gradle/wrapper/gradle-wrapper.properties" ]]; then
  cat >&2 <<'EOF'
BLOCKED: Gradle Wrapper is not committed.

On a machine with Gradle installed, generate it with:
  gradle :wrapper --gradle-version <PINNED_VERSION>
Then commit:
  gradlew
  gradlew.bat
  gradle/wrapper/gradle-wrapper.jar
  gradle/wrapper/gradle-wrapper.properties

Do not hand-create or substitute the wrapper JAR.
EOF
  exit 20
fi

if [[ ! -d "$ROOT/third_party/llama.cpp/.git" ]]; then
  echo "BLOCKED: vendored llama.cpp checkout is missing." >&2
  exit 21
fi

if [[ "${OFFLINE_BUILD:-0}" == "1" ]]; then
  echo "OFFLINE_BUILD=1: no network bootstrap is permitted."
  bash tools/verify_native_source.sh
fi

echo "PASS: reproducible-build prerequisites are present."
