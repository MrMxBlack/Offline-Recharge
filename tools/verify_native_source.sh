#!/usr/bin/env bash
set -euo pipefail
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
EXPECTED="${LLAMA_COMMIT:-d230ddd763ffe27781c7ffd237ea78b639b36b6d}"
[[ -d "$ROOT/third_party/llama.cpp/.git" ]] || { echo "FAIL: llama.cpp source not integrated"; exit 1; }
ACTUAL="$(git -C "$ROOT/third_party/llama.cpp" rev-parse HEAD)"
[[ "$ACTUAL" == "$EXPECTED" ]] || { echo "FAIL: expected $EXPECTED, got $ACTUAL"; exit 1; }
[[ -f "$ROOT/llama/src/main/cpp/CMakeLists.txt" ]] || { echo "FAIL: Android binding missing"; exit 1; }
grep -q 'third_party/llama.cpp' "$ROOT/llama/src/main/cpp/CMakeLists.txt"
grep -q 'implementation(project(":llama"))' "$ROOT/app/build.gradle.kts"
grep -q 'include(":llama")' "$ROOT/settings.gradle.kts"
! grep -q 'android.permission.INTERNET' "$ROOT/app/src/main/AndroidManifest.xml"
echo "PASS: pinned source + corrected CMake root + Android binding + offline manifest"
