#!/usr/bin/env python3
"""Generate reproducibility metadata without network access."""
from pathlib import Path
import hashlib, json, subprocess, datetime
ROOT=Path(__file__).resolve().parents[1]

def sha256(p):
    h=hashlib.sha256()
    with p.open('rb') as f:
        for b in iter(lambda:f.read(1024*1024), b''): h.update(b)
    return h.hexdigest()

def git(args):
    try: return subprocess.check_output(['git','-C',str(ROOT),*args],text=True,stderr=subprocess.DEVNULL).strip()
    except Exception: return 'not-a-git-checkout'

files=[]
for p in ROOT.rglob('*'):
    if p.is_file() and '.git' not in p.parts and '__pycache__' not in p.parts and p.suffix != '.pyc' and p.name not in {'RELEASE_MANIFEST.json'}:
        files.append({'path':str(p.relative_to(ROOT)),'bytes':p.stat().st_size,'sha256':sha256(p)})
manifest={
 'schema':'offline-research-release-v6-final-static-candidate',
 'generated_utc':datetime.datetime.now(datetime.timezone.utc).isoformat(),
 'git_commit':git(['rev-parse','HEAD']),
 'git_dirty':bool(git(['status','--porcelain'])),
 'runtime':{'internet_permission':False,'max_ram_gb':12,'max_storage_gb':50},
 'native':{'engine':'llama.cpp','commit':'RECORD_AFTER_IMPORT','source_sha256':'RECORD_AFTER_IMPORT'},
 'model':{'name':'Qwen3-1.7B GGUF development baseline','sha256':'RECORD_AFTER_MODEL_IMPORT'},
 'corpus':{'manifest':'corpus/manifest.json','checksum':sha256(ROOT/'corpus/manifest.json') if (ROOT/'corpus/manifest.json').exists() else None},
 'device_verification':'PENDING',
 'files':files
}
(ROOT/'RELEASE_MANIFEST.json').write_text(json.dumps(manifest,indent=2,ensure_ascii=False)+'\n')
print(json.dumps(manifest,indent=2))
