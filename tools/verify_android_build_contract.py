#!/usr/bin/env python3
import json, re, sys
from pathlib import Path

root = Path(__file__).resolve().parents[1]
errors=[]

def need(path):
    if not path.exists(): errors.append(f'missing: {path.relative_to(root)}')

for rel in ['settings.gradle.kts','build.gradle.kts','app/build.gradle.kts','app/src/main/AndroidManifest.xml','tools/integrate_llama_android.sh']:
    need(root/rel)

app=(root/'app/build.gradle.kts').read_text() if (root/'app/build.gradle.kts').exists() else ''
settings=(root/'settings.gradle.kts').read_text() if (root/'settings.gradle.kts').exists() else ''
if 'include(":app")' not in settings: errors.append('settings.gradle.kts must include :app')
if 'implementation(project(":llama"))' in app and 'include(":llama")' not in settings: errors.append('app depends on :llama but settings does not include :llama')
status_path=root/'RELEASE_STATUS.json'
if status_path.exists():
    try:
        status=json.loads(status_path.read_text())
        version=status.get('version','')
        m=re.fullmatch(r'V5\.(\d+)', version)
        app_match=re.search(r'versionName\s*=\s*"([^"]+)"', app)
        if m and app_match:
            app_version=app_match.group(1)
            if ('v5.' + m.group(1)) not in app_version.lower():
                errors.append(f'app version metadata {app_version!r} is not synchronized with {version}')
    except Exception as e: errors.append(f'bad RELEASE_STATUS.json: {e}')

manifest=(root/'app/src/main/AndroidManifest.xml').read_text() if (root/'app/src/main/AndroidManifest.xml').exists() else ''
if re.search(r'android:name=["\']android\.permission\.INTERNET["\']', manifest): errors.append('INTERNET permission present')

pin=root/'third_party/LLAMA_CPP_PIN.json'
if pin.exists():
    try:
        x=json.loads(pin.read_text())
        c=x.get('commit','')
        if not re.fullmatch(r'[0-9a-f]{40}', c): errors.append('llama.cpp pin is not a 40-char SHA')
    except Exception as e: errors.append(f'bad LLAMA_CPP_PIN.json: {e}')

# If the native module is present, require the current upstream contract we intentionally mirror.
mod=root/'llama/build.gradle.kts'
if mod.exists():
    t=mod.read_text()
    for needle in ['compileSdk = 36','ndkVersion = "29.0.13113456"','minSdk = 33','"arm64-v8a"','CMAKE_BUILD_TYPE=Release','LLAMA_OPENSSL=OFF','GGML_NATIVE=OFF','version = "3.31.6"']:
        if needle not in t: errors.append(f'llama module missing contract: {needle}')

if errors:
    print('FAIL: Android build contract')
    for e in errors: print(' -',e)
    sys.exit(2)
print('PASS: Android build contract')
