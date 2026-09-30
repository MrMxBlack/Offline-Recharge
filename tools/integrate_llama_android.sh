#!/usr/bin/env bash
set -euo pipefail
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
COMMIT="${LLAMA_COMMIT:-d230ddd763ffe27781c7ffd237ea78b639b36b6d}"
SRC="$ROOT/third_party/llama.cpp"
MODULE="$ROOT/llama"
UPSTREAM="$SRC/examples/llama.android/lib"

[[ "$COMMIT" =~ ^[0-9a-fA-F]{40}$ ]] || { echo "FAIL: LLAMA_COMMIT must be a 40-character SHA" >&2; exit 2; }

if [[ -d "$SRC/.git" ]]; then
  if [[ "${OFFLINE_BUILD:-0}" == "1" ]]; then
    ACTUAL_LOCAL="$(git -C "$SRC" rev-parse HEAD)"
    [[ "$ACTUAL_LOCAL" == "$COMMIT" ]] || { echo "FAIL: OFFLINE_BUILD=1 and vendored llama.cpp HEAD ($ACTUAL_LOCAL) does not match pinned commit ($COMMIT)" >&2; exit 11; }
    git -C "$SRC" diff --quiet || { echo "FAIL: OFFLINE_BUILD=1 and vendored llama.cpp has uncommitted changes" >&2; exit 12; }
  else
    git -C "$SRC" fetch --depth 1 origin "$COMMIT"
    git -C "$SRC" checkout --detach "$COMMIT"
  fi
else
  if [[ "${OFFLINE_BUILD:-0}" == "1" ]]; then
    echo "FAIL: OFFLINE_BUILD=1 but third_party/llama.cpp is not vendored" >&2
    exit 10
  fi
  rm -rf "$SRC"
  git clone --filter=blob:none --no-checkout https://github.com/ggml-org/llama.cpp.git "$SRC"
  git -C "$SRC" fetch --depth 1 origin "$COMMIT"
  git -C "$SRC" checkout --detach "$COMMIT"
fi

ACTUAL="$(git -C "$SRC" rev-parse HEAD)"
[[ "$ACTUAL" == "$COMMIT" ]] || { echo "FAIL: checkout mismatch" >&2; exit 3; }
[[ -d "$UPSTREAM/src/main" ]] || { echo "FAIL: upstream Android binding missing" >&2; exit 4; }
[[ -f "$UPSTREAM/src/main/cpp/CMakeLists.txt" ]] || { echo "FAIL: upstream Android CMakeLists missing" >&2; exit 5; }
[[ -f "$UPSTREAM/src/main/cpp/ai_chat.cpp" ]] || { echo "FAIL: upstream JNI source missing" >&2; exit 6; }

rm -rf "$MODULE"
mkdir -p "$MODULE"
cp -R "$UPSTREAM" "$MODULE/"
python3 - "$MODULE/src/main/cpp/CMakeLists.txt" <<'PY'
from pathlib import Path
import sys, re
p=Path(sys.argv[1]); s=p.read_text()
pat=r'(?m)^\s*set\(LLAMA_SRC\s+[^)]+\)\s*$'
s2,n=re.subn(pat,'set(LLAMA_SRC ${CMAKE_CURRENT_LIST_DIR}/../../../../third_party/llama.cpp)',s)
if n != 1:
    raise SystemExit('FAIL: expected exactly one LLAMA_SRC assignment; refusing unsafe patch')
p.write_text(s2)
PY
cat > "$MODULE/build.gradle.kts" <<'GRADLE'
plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}
android {
    namespace = "com.arm.aichat"
    compileSdk = 36
    ndkVersion = "29.0.13113456"
    defaultConfig {
        minSdk = 33
        consumerProguardFiles("consumer-rules.pro")
        ndk { abiFilters += listOf("arm64-v8a") }
        externalNativeBuild {
            cmake {
                arguments += "-DCMAKE_BUILD_TYPE=Release"
                arguments += "-DCMAKE_MESSAGE_LOG_LEVEL=DEBUG"
                arguments += "-DCMAKE_VERBOSE_MAKEFILE=ON"
                arguments += "-DBUILD_SHARED_LIBS=ON"
                arguments += "-DLLAMA_BUILD_APP=OFF"
                arguments += "-DLLAMA_BUILD_COMMON=ON"
                arguments += "-DLLAMA_OPENSSL=OFF"
                arguments += "-DGGML_NATIVE=OFF"
                arguments += "-DGGML_BACKEND_DL=ON"
                arguments += "-DGGML_CPU_ALL_VARIANTS=ON"
                arguments += "-DGGML_LLAMAFILE=OFF"
            }
        }
    }
    externalNativeBuild { cmake { path = file("src/main/cpp/CMakeLists.txt"); version = "3.31.6" } }
    compileOptions { sourceCompatibility = JavaVersion.VERSION_17; targetCompatibility = JavaVersion.VERSION_17 }
    kotlin { jvmToolchain(17) }
    packaging { resources.excludes += "/META-INF/{AL2.0,LGPL2.1}" }
}
dependencies {
    implementation("androidx.core:core-ktx:1.17.0")
    implementation("androidx.datastore:datastore-preferences:1.2.0")
}
GRADLE
PROVIDER="$ROOT/app/src/main/java/com/rohan/offlineresearch/LlamaAndroidNativeProvider.kt"
cp "$ROOT/tools/LlamaAndroidNativeProvider.kt.template" "$PROVIDER"
python3 - "$ROOT/settings.gradle.kts" "$ROOT/app/build.gradle.kts" <<'PY'
from pathlib import Path
import sys
sp,ap=map(Path,sys.argv[1:]); s=sp.read_text(); a=ap.read_text()
if 'include(":llama")' not in s: s += '\ninclude(":llama")\n'
if 'implementation(project(":llama"))' not in a: a=a.replace('dependencies {','dependencies {\n    implementation(project(":llama"))',1)
sp.write_text(s); ap.write_text(a)
PY
cat > "$ROOT/third_party/LLAMA_CPP_PIN.json" <<EOF
{
  "repository": "https://github.com/ggml-org/llama.cpp",
  "commit": "$COMMIT",
  "integration": "examples/llama.android/lib",
  "runtime_network": false,
  "build_network": "forbidden_in_final_release",
  "vendored_required_for_airgapped_build": true
}
EOF
echo "PASS: llama.cpp $ACTUAL integrated"
