#!/usr/bin/env python3
"""Ensure the source archive has a clean reproducible root layout."""
from pathlib import Path
import sys
root=Path(__file__).resolve().parents[1]
required=['settings.gradle.kts','build.gradle.kts','app/build.gradle.kts','RELEASE_STATUS.json','tools']
errors=[]
for x in required:
    if not (root/x).exists(): errors.append(f'missing root artifact: {x}')
if (root/'src').exists(): errors.append('unexpected nested src/ directory at repository root')
if (root/'OfflineResearch_V5.25').exists(): errors.append('unexpected nested project directory')
print('ARCHIVE LAYOUT AUDIT')
for e in errors: print('ERROR:',e)
print('errors:',len(errors))
if errors: sys.exit(1)
print('PASS: repository root is self-contained')
