#!/usr/bin/env bash
set -euo pipefail
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
PIN="$ROOT/third_party/LLAMA_CPP_PIN.json"
SRC="$ROOT/third_party/llama.cpp"
MODULE="$ROOT/llama"
PROVIDER="$ROOT/app/src/main/java/com/rohan/offlineresearch/LlamaAndroidNativeProvider.kt"

[[ -f "$PIN" ]] || { echo "FAIL: missing LLAMA_CPP_PIN.json"; exit 2; }
python3 - "$PIN" <<'PY'
import json,sys,re
p=json.load(open(sys.argv[1]))
c=p.get('commit','')
assert re.fullmatch(r'[0-9a-f]{40}', c), 'invalid llama.cpp commit'
print('PIN:', c)
PY
[[ -d "$SRC/.git" ]] || { echo "FAIL: llama.cpp source is not vendored; run integration setup on an online build machine."; exit 3; }
ACTUAL="$(git -C "$SRC" rev-parse HEAD)"
PINNED="$(python3 -c 'import json,sys; print(json.load(open(sys.argv[1]))["commit"])' "$PIN")"
[[ "$ACTUAL" == "$PINNED" ]] || { echo "FAIL: source commit $ACTUAL != pinned $PINNED"; exit 4; }
[[ -f "$MODULE/src/main/cpp/ai_chat.cpp" ]] || { echo "FAIL: Android JNI binding missing"; exit 5; }
[[ -f "$MODULE/src/main/java/com/arm/aichat/AiChat.kt" ]] || { echo "FAIL: AiChat API missing"; exit 6; }
[[ -f "$MODULE/src/main/java/com/arm/aichat/InferenceEngine.kt" ]] || { echo "FAIL: InferenceEngine API missing"; exit 7; }
[[ -f "$PROVIDER" ]] || { echo "FAIL: app native provider missing"; exit 8; }
grep -q 'AiChat.getInferenceEngine' "$PROVIDER" || { echo "FAIL: provider is not wired to AiChat"; exit 9; }
grep -q 'sendUserPrompt' "$PROVIDER" || { echo "FAIL: token generation is not wired"; exit 10; }
echo "PASS: native integration structure and pinned commit verified"
