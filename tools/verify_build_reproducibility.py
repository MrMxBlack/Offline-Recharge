#!/usr/bin/env python3
"""Fail-closed reproducibility + Android Gradle compatibility audit."""
from pathlib import Path
import json, re, sys

ROOT=Path(__file__).resolve().parents[1]
errors=[]
wrapper=ROOT/'gradlew'
wrapper_jar=ROOT/'gradle/wrapper/gradle-wrapper.jar'
wrapper_props=ROOT/'gradle/wrapper/gradle-wrapper.properties'
if not wrapper.exists() or not wrapper.is_file(): errors.append('gradlew missing')
if not wrapper_jar.exists() or wrapper_jar.stat().st_size < 10000: errors.append('gradle-wrapper.jar missing or implausibly small')
if not wrapper_props.exists(): errors.append('gradle-wrapper.properties missing')
else:
    t=wrapper_props.read_text(errors='ignore')
    m=re.search(r'gradle-(\d+)\.(\d+)\.(\d+)-(?:bin|all)\.zip', t)
    if not m: errors.append('Gradle distributionUrl must pin an exact X.Y.Z version')
    else:
        version=tuple(map(int,m.groups()))
        if version < (8,11,1): errors.append(f'Gradle {version[0]}.{version[1]}.{version[2]} is too old for AGP 8.9.x; require >= 8.11.1')
        if version[0] >= 9: errors.append('Gradle 9.x is outside the pinned V5.17 build contract; use Gradle 8.11.1 for AGP 8.9.1')
    if 'distributionUrl=' not in t: errors.append('Gradle distributionUrl missing')
    if 'distributionSha256Sum=' not in t: errors.append('Gradle distribution SHA-256 is not pinned')
settings=(ROOT/'settings.gradle.kts').read_text(errors='ignore')
if 'include(":app")' not in settings: errors.append('app module missing from settings')
root_build=(ROOT/'build.gradle.kts').read_text(errors='ignore')
agp=re.search(r'id\("com\.android\.application"\) version "([0-9.]+)"', root_build)
if not agp: errors.append('AGP version not pinned')
else:
    if tuple(map(int,agp.group(1).split('.')))[:2] != (8,9): errors.append(f'Unexpected AGP version: {agp.group(1)}')
app=(ROOT/'app/build.gradle.kts').read_text(errors='ignore')

status=json.loads((ROOT/'RELEASE_STATUS.json').read_text())
expected_version=status.get('version')
if not expected_version:
    errors.append('RELEASE_STATUS.version missing')
else:
    m=re.fullmatch(r'V5\.(\d+)', str(expected_version))
    if not m:
        m=re.fullmatch(r'FINAL-(\d+)\.(\d+)', str(expected_version))
    app_match=re.search(r'versionName\s*=\s*"([^"]+)"', app)
    if not m:
        errors.append(f'invalid release version: {expected_version!r}')
    elif not app_match:
        errors.append(f'app version metadata is not synchronized with {expected_version}')
    elif str(expected_version).startswith('V5.') and ('v5.' + m.group(1)) not in app_match.group(1).lower():
        errors.append(f'app version metadata is not synchronized with {expected_version}')
    elif str(expected_version).startswith('FINAL-') and 'final-' + m.group(1) + '.' + m.group(2) not in app_match.group(1).lower():
        errors.append(f'app version metadata is not synchronized with {expected_version}')
if status.get('bounty_ready') is not False: errors.append('bounty_ready must remain false until runtime gates pass')
print('BUILD REPRODUCIBILITY + AGP COMPATIBILITY AUDIT')
for e in errors: print('ERROR:',e)
print('errors:',len(errors))
if errors: sys.exit(1)
print('PASS: exact Gradle/AGP compatibility contract is defined')
