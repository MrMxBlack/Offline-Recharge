#!/usr/bin/env python3
"""Fail-closed release truth checker.

This checks only claims that can be established from repository state. It never
turns static checks into device/network/native-inference claims.
"""
from pathlib import Path
import json, re, sys

ROOT = Path(__file__).resolve().parents[1]
errors=[]
status_path=ROOT/'RELEASE_STATUS.json'
status=json.loads(status_path.read_text())
version=status.get('version','')
expected_pattern=r"^(?:V5\.\d+|FINAL-\d+\.\d+)$"
if not re.fullmatch(expected_pattern, version):
    errors.append(f'RELEASE_STATUS version is {version!r}, expected a V5.x or FINAL-x.y release version')

# Native source is only "vendored" when the actual git checkout exists and matches the pin.
pin_path=ROOT/'third_party/LLAMA_CPP_PIN.json'
pin=json.loads(pin_path.read_text()) if pin_path.exists() else {}
commit=pin.get('commit','')
source=ROOT/'third_party/llama.cpp'
vendored=source.is_dir() and (source/'.git').is_dir()
if status.get('native_llama_cpp_source') == 'vendored_and_verified' and not vendored:
    errors.append('status claims vendored llama.cpp but source checkout is absent')
if vendored:
    import subprocess
    actual=subprocess.check_output(['git','-C',str(source),'rev-parse','HEAD'],text=True).strip()
    if actual != commit:
        errors.append(f'vendored llama.cpp {actual} != pinned {commit}')

# A repository cannot claim native inference or device proof solely from static files.
for key in ('native_inference','physical_android_device','grapheneos','zero_network_runtime_proof'):
    if status.get(key) in {'verified','passed','verified_runtime'}:
        errors.append(f'{key} claims runtime verification; repository cannot establish this by itself')

if status.get('bounty_ready') is True:
    required = [
        status.get('native_inference') == 'verified_runtime',
        status.get('physical_android_device') == 'verified',
        status.get('zero_network_runtime_proof') == 'verified',
        status.get('research_benchmark') == 'verified',
    ]
    if not all(required):
        errors.append('bounty_ready=true without all required runtime evidence')

print('RELEASE TRUTH AUDIT')
print('version:', version)
print('llama.cpp vendored:', vendored)
print('bounty_ready:', status.get('bounty_ready'))
print('errors:', len(errors))
for e in errors:
    print('ERROR:', e)
if errors:
    sys.exit(1)
print('PASS: release claims are consistent with repository evidence')
