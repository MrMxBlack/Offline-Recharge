#!/usr/bin/env python3
"""Static policy audit for a genuinely offline runtime.

This does not prove that a binary makes no network traffic. It catches common
network capabilities and dependencies before physical-device verification.
"""
from pathlib import Path
import re, sys

ROOT = Path(__file__).resolve().parents[1]
ERRORS=[]; WARNINGS=[]

manifest = ROOT/'app/src/main/AndroidManifest.xml'
xml = manifest.read_text(errors='ignore') if manifest.exists() else ''
if re.search(r'android\.permission\.INTERNET', xml):
    ERRORS.append('AndroidManifest declares INTERNET permission')
if re.search(r'android\.permission\.ACCESS_(NETWORK_STATE|WIFI_STATE)', xml):
    WARNINGS.append('Manifest reads network state; remove unless a documented benchmark needs it')
if 'usesCleartextTraffic="true"' in xml:
    ERRORS.append('Cleartext traffic is explicitly enabled')

source_ext={'.kt','.java','.kts','.xml','.gradle','.properties','.py','.sh'}
for p in ROOT.rglob('*'):
    if not p.is_file() or '.git' in p.parts or p.suffix not in source_ext:
        continue
    text=p.read_text(errors='ignore')
    rel=p.relative_to(ROOT)
    if re.search(r'(?i)\bimport\s+(java\.net|javax\.net|android\.net\.(http|wifi)|okhttp|retrofit|com\.google\.firebase|com\.google\.android\.gms)', text):
        ERRORS.append(f'network/cloud import: {rel}')
    if re.search(r'(?i)\b(HttpURLConnection|DownloadManager|WebView|Cronet|Firebase|Retrofit|OkHttpClient)\b', text) and p.name != 'verify_offline_policy.py':
        # Documentation and build scripts may mention these as forbidden examples.
        if rel.parts[:2] not in [('docs',''),]:
            WARNINGS.append(f'network-capable symbol needs review: {rel}')

for p in [ROOT/'app/build.gradle.kts', ROOT/'build.gradle.kts', ROOT/'settings.gradle.kts']:
    if p.exists():
        t=p.read_text(errors='ignore')
        for bad in ('firebase','play-services','okhttp','retrofit','ktor','coil-network'):
            if bad.lower() in t.lower(): ERRORS.append(f'network/cloud dependency string: {p.relative_to(ROOT)} -> {bad}')

print('OFFLINE POLICY AUDIT v5')
print(f'errors: {len(ERRORS)}')
print(f'warnings: {len(WARNINGS)}')
for x in ERRORS: print('ERROR:', x)
for x in WARNINGS[:50]: print('WARN :', x)
if not ERRORS: print('PASS: no forbidden runtime capability detected statically')
print('NOTE: static policy is not proof of zero packets; perform device network verification.')
sys.exit(1 if ERRORS else 0)
