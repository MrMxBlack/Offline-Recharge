#!/usr/bin/env python3
"""Fail-closed validation for traceable real-device research benchmarks."""
import hashlib, json, re, sys
from pathlib import Path

if len(sys.argv) not in (2,3):
    raise SystemExit('usage: verify_benchmark_evidence.py benchmark_manifest.json [device_result.json]')

path = Path(sys.argv[1]).resolve()
d = json.loads(path.read_text())
if d.get('status') == 'MANIFEST_ONLY_UNTIL_REAL_DEVICE_TEST':
    raise SystemExit('FAIL: manifest is a test plan, not real device evidence')
rows = d.get('device_results')
if not isinstance(rows, list) or len(rows) < 10:
    raise SystemExit('FAIL: at least 10 real device benchmark rows are required')
required = {'id','query','mode','retrieval_ms','ttft_ms','generation_ms','total_ms','tokens_generated','output_sha256','evidence_ids','output_artifact'}
# Evidence IDs must resolve to the deterministic local corpus, not merely be non-empty strings.
repo_root = path.parent.parent
catalog_candidates = [repo_root/'corpus/chunks.json', repo_root/'app/src/main/assets/corpus.json']
catalog_ids=set()
for cp in catalog_candidates:
    if cp.is_file():
        try:
            payload=json.loads(cp.read_text())
            if isinstance(payload,list):
                catalog_ids.update(str(x.get('id')) for x in payload if isinstance(x,dict) and x.get('id'))
        except Exception:
            pass
if not catalog_ids:
    raise SystemExit('FAIL: no local evidence catalog is available')
ids=set()
for i,row in enumerate(rows):
    if not isinstance(row,dict) or not required.issubset(row): raise SystemExit(f'FAIL: row {i+1} is not traceable')
    if row['id'] in ids: raise SystemExit(f'FAIL: duplicate benchmark id {row["id"]}')
    ids.add(row['id'])
    for k in ('retrieval_ms','ttft_ms','generation_ms','total_ms','tokens_generated'):
        if not isinstance(row[k],(int,float)) or row[k] < 0: raise SystemExit(f'FAIL: row {i+1} invalid {k}')
    if row['total_ms'] < row['ttft_ms'] or row['generation_ms'] > row['total_ms']:
        raise SystemExit(f'FAIL: row {i+1} has inconsistent latency fields')
    if not isinstance(row['evidence_ids'],list) or not row['evidence_ids'] or not all(isinstance(x,str) and x.strip() for x in row['evidence_ids']):
        raise SystemExit(f'FAIL: row {i+1} evidence IDs are invalid')
    unknown=[x for x in row['evidence_ids'] if x not in catalog_ids]
    if unknown:
        raise SystemExit(f'FAIL: row {i+1} references unknown local evidence IDs: {unknown}')
    h=row['output_sha256']
    if not isinstance(h,str) or not re.fullmatch(r'[0-9a-f]{64}',h): raise SystemExit(f'FAIL: row {i+1} invalid output SHA-256')
    artifact=Path(row['output_artifact']); artifact=artifact if artifact.is_absolute() else (path.parent/artifact).resolve()
    if not artifact.is_file() or artifact.stat().st_size==0: raise SystemExit(f'FAIL: row {i+1} output artifact missing')
    if hashlib.sha256(artifact.read_bytes()).hexdigest()!=h: raise SystemExit(f'FAIL: row {i+1} output artifact SHA-256 mismatch')
if d.get('native_inference') is not True: raise SystemExit('FAIL: native_inference must be true in real benchmark evidence')
if not re.fullmatch(r'[0-9a-f]{64}', str(d.get('model_sha256',''))): raise SystemExit('FAIL: model SHA-256 missing or invalid')
model_path=d.get('model_artifact')
if not model_path: raise SystemExit('FAIL: model_artifact missing')
model=Path(model_path); model=model if model.is_absolute() else (path.parent/model).resolve()
if not model.is_file() or model.stat().st_size==0: raise SystemExit('FAIL: model artifact missing')
if hashlib.sha256(model.read_bytes()).hexdigest()!=d['model_sha256']: raise SystemExit('FAIL: model artifact SHA-256 mismatch')
if len(sys.argv)==3:
    devp=Path(sys.argv[2]).resolve(); dev=json.loads(devp.read_text())
    if dev.get('model_sha256') != d['model_sha256']: raise SystemExit('FAIL: device/model SHA-256 mismatch')
    if dev.get('native_inference') is not True: raise SystemExit('FAIL: device evidence does not confirm native inference')
    if dev.get('physical_ram_gb',99) > 12: raise SystemExit('FAIL: device exceeds 12 GB physical RAM budget')
    if dev.get('airplane_mode') is not True or dev.get('wifi_disabled') is not True or dev.get('mobile_data_disabled') is not True: raise SystemExit('FAIL: network isolation flags are not all true')
    dev_ids={b.get('id') for b in dev.get('benchmarks',[]) if isinstance(b,dict) and b.get('id')}
    missing=ids-dev_ids
    if missing: raise SystemExit('FAIL: device evidence missing benchmark IDs: '+','.join(sorted(missing)))
print(f'PASS: {len(rows)} traceable benchmark rows validated')
