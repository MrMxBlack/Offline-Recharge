#!/usr/bin/env bash
set -euo pipefail
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
SRC="$ROOT/third_party/llama.cpp"
MOD="$ROOT/llama"
fail(){ echo "FAIL: $1" >&2; exit 2; }
[[ -d "$SRC/.git" ]] || fail "llama.cpp is not vendored"
[[ -d "$MOD/src/main/java/com/arm/aichat" ]] || fail "Android binding Kotlin package missing"
[[ -f "$MOD/src/main/java/com/arm/aichat/AiChat.kt" ]] || fail "AiChat.kt missing"
[[ -f "$MOD/src/main/java/com/arm/aichat/InferenceEngine.kt" ]] || fail "InferenceEngine.kt missing"
[[ -f "$MOD/src/main/cpp/ai_chat.cpp" ]] || fail "ai_chat.cpp missing"
[[ -f "$MOD/src/main/cpp/CMakeLists.txt" ]] || fail "CMakeLists missing"
grep -q 'third_party/llama.cpp' "$MOD/src/main/cpp/CMakeLists.txt" || fail "CMake does not point to vendored llama.cpp"
grep -q 'AiChat.getInferenceEngine' "$ROOT/app/src/main/java/com/rohan/offlineresearch/LlamaAndroidNativeProvider.kt" || fail "app provider not wired to AiChat"
grep -q 'sendUserPrompt' "$ROOT/app/src/main/java/com/rohan/offlineresearch/LlamaAndroidNativeProvider.kt" || fail "token generation API not wired"
echo "PASS: upstream Android binding contract"
