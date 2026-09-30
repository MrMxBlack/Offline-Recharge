#!/usr/bin/env python3
"""Offline benchmark manifest runner.

This script intentionally does not call a model or network. It validates the benchmark
suite and produces a reproducible run manifest that can later be filled with device
measurements from the Android app.
"""
import json, pathlib, hashlib, datetime
root=pathlib.Path(__file__).resolve().parents[1]
src=root/'bench/benchmark_questions.json'
out=root/'bench/results/benchmark_manifest.json'
data=json.loads(src.read_text())
questions=data if isinstance(data,list) else data.get('questions', [])
manifest={
 'created_utc': datetime.datetime.now(datetime.timezone.utc).isoformat(),
 'network': 'disabled by design',
 'question_count': len(questions),
 'question_sha256': hashlib.sha256(src.read_bytes()).hexdigest(),
 'device_results': [],
 'status': 'MANIFEST_ONLY_UNTIL_REAL_DEVICE_TEST'
}
out.parent.mkdir(parents=True,exist_ok=True)
out.write_text(json.dumps(manifest,indent=2,ensure_ascii=False)+'\n')
print(json.dumps(manifest,indent=2))
