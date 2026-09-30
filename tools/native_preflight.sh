#!/usr/bin/env bash
set -euo pipefail
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
fail(){ echo "FAIL: $*" >&2; exit 1; }
command -v git >/dev/null || fail "git is required"
command -v cmake >/dev/null || echo "WARN: cmake not installed; native build cannot be run here"
[[ -n "${ANDROID_SDK_ROOT:-}${ANDROID_HOME:-}" ]] || echo "WARN: ANDROID_SDK_ROOT/ANDROID_HOME not set"
[[ -n "${ANDROID_NDK_ROOT:-}" ]] || echo "WARN: ANDROID_NDK_ROOT not set"
PIN="$ROOT/third_party/LLAMA_CPP_PIN.json"
[[ -f "$PIN" ]] || fail "missing llama.cpp pin file"
python3 - "$PIN" <<'PY'
import json,sys,re
p=json.load(open(sys.argv[1]))
c=p.get('commit','')
assert re.fullmatch(r'[0-9a-f]{40}',c), 'llama.cpp commit must be a full SHA'
assert p.get('repository') == 'https://github.com/ggml-org/llama.cpp'
print('PASS: llama.cpp pin is a full SHA')
PY
if [[ -d "$ROOT/third_party/llama.cpp/.git" ]]; then
  expected=$(python3 - "$PIN" <<'PY'
import json,sys; print(json.load(open(sys.argv[1]))['commit'])
PY
)
  actual=$(git -C "$ROOT/third_party/llama.cpp" rev-parse HEAD)
  [[ "$actual" == "$expected" ]] || fail "vendored llama.cpp HEAD $actual != pinned $expected"
  echo "PASS: vendored llama.cpp commit matches pin"
else
  echo "INFO: llama.cpp is not vendored yet; run tools/integrate_llama_android.sh on an online build host"
fi
if [[ -d "$ROOT/llama" ]]; then
  [[ -f "$ROOT/llama/build.gradle.kts" ]] || fail "llama module missing build.gradle.kts"
  [[ -f "$ROOT/llama/src/main/cpp/CMakeLists.txt" ]] || fail "llama module missing CMakeLists.txt"
  [[ -f "$ROOT/llama/src/main/cpp/ai_chat.cpp" ]] || fail "llama module missing ai_chat.cpp"
  echo "PASS: Android llama binding structure present"
else
  echo "INFO: Android llama module not installed yet"
fi
