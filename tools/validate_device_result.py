#!/usr/bin/env python3
import hashlib, json, sys
from pathlib import Path

if len(sys.argv) != 2:
    raise SystemExit('usage: validate_device_result.py evidence.json')

path = Path(sys.argv[1]).resolve()
d = json.loads(path.read_text())
required = [
    'device','android','physical_ram_gb','model_sha256','airplane_mode',
    'wifi_disabled','mobile_data_disabled','native_inference',
    'network_proof','benchmarks','packet_capture_sha256','benchmark_manifest_sha256',
    'benchmark_manifest','model_artifact'
]
missing = [x for x in required if x not in d]
if missing:
    raise SystemExit('FAIL: missing fields: ' + ', '.join(missing))

if not all(d[x] is True for x in ['airplane_mode','wifi_disabled','mobile_data_disabled']):
    raise SystemExit('FAIL: device connectivity state is not fully offline')
if d.get('native_inference') is not True:
    raise SystemExit('FAIL: native inference evidence is not confirmed')
if not isinstance(d['benchmarks'], list) or len(d['benchmarks']) < 10:
    raise SystemExit('FAIL: at least 10 benchmark results are required')

repo_root = path.parent.parent
catalog_candidates=[repo_root/'corpus/chunks.json', repo_root/'app/src/main/assets/corpus.json']
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

required_benchmark_fields = {'id','query','mode','retrieval_ms','ttft_ms','generation_ms','total_ms','tokens_generated','output_sha256','evidence_ids','output_artifact'}
seen_ids=set()
for i, item in enumerate(d['benchmarks']):
    if not isinstance(item, dict) or not required_benchmark_fields.issubset(item):
        raise SystemExit(f'FAIL: benchmark {i+1} is not traceable; required fields: {sorted(required_benchmark_fields)}')
    if item.get('id') in seen_ids: raise SystemExit(f'FAIL: duplicate benchmark id: {item.get("id")}')
    seen_ids.add(item.get('id'))
    if not isinstance(item['query'], str) or not item['query'].strip():
        raise SystemExit(f'FAIL: benchmark {i+1} query is empty')
    if not isinstance(item['evidence_ids'], list) or not item['evidence_ids']:
        raise SystemExit(f'FAIL: benchmark {i+1} has no evidence IDs')
    unknown=[x for x in item['evidence_ids'] if x not in catalog_ids]
    if unknown:
        raise SystemExit(f'FAIL: benchmark {i+1} references unknown local evidence IDs: {unknown}')
    for f in ('retrieval_ms','ttft_ms','generation_ms','total_ms','tokens_generated'):
        if not isinstance(item[f], (int,float)) or item[f] < 0:
            raise SystemExit(f'FAIL: benchmark {i+1} has invalid {f}')
    if not isinstance(item['output_sha256'], str) or len(item['output_sha256']) != 64 or any(c not in '0123456789abcdef' for c in item['output_sha256']):
        raise SystemExit(f'FAIL: benchmark {i+1} has invalid output_sha256')
    artifact=Path(item['output_artifact'])
    if not artifact.is_absolute(): artifact=(path.parent / artifact).resolve()
    if not artifact.is_file() or artifact.stat().st_size == 0:
        raise SystemExit(f'FAIL: benchmark {i+1} output artifact missing')
    if hashlib.sha256(artifact.read_bytes()).hexdigest() != item['output_sha256']:
        raise SystemExit(f'FAIL: benchmark {i+1} output artifact SHA-256 mismatch')

model_artifact = Path(d['model_artifact'])
if not model_artifact.is_absolute(): model_artifact=(path.parent/model_artifact).resolve()
if not model_artifact.is_file() or model_artifact.stat().st_size == 0:
    raise SystemExit('FAIL: model artifact missing')
if hashlib.sha256(model_artifact.read_bytes()).hexdigest() != d['model_sha256']:
    raise SystemExit('FAIL: model artifact SHA-256 mismatch')

for field in ('model_sha256','packet_capture_sha256','benchmark_manifest_sha256'):
    value=d[field]
    if not isinstance(value,str) or len(value)!=64 or any(c not in '0123456789abcdef' for c in value):
        raise SystemExit(f'FAIL: invalid SHA-256 in {field}')

manifest = Path(d['benchmark_manifest'])
if not manifest.is_absolute():
    manifest = (path.parent / manifest).resolve()
if not manifest.is_file() or manifest.stat().st_size == 0:
    raise SystemExit('FAIL: benchmark manifest artifact missing')
if hashlib.sha256(manifest.read_bytes()).hexdigest() != d['benchmark_manifest_sha256']:
    raise SystemExit('FAIL: benchmark manifest SHA-256 mismatch')

proof = Path(d['network_proof'])
if not proof.is_absolute():
    proof = (path.parent / proof).resolve()
if not proof.is_file() or proof.stat().st_size == 0:
    raise SystemExit('FAIL: packet-level network proof artifact missing')
if hashlib.sha256(proof.read_bytes()).hexdigest() != d['packet_capture_sha256']:
    raise SystemExit('FAIL: packet capture SHA-256 mismatch')

print('PASS: device evidence has offline state, native inference, 10+ benchmarks, and packet-proof integrity')
