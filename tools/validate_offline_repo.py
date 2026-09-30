#!/usr/bin/env python3
import json,pathlib,re,sys
root=pathlib.Path(__file__).resolve().parents[1]; errors=[]; warnings=[]
try:
 m=json.loads((root/'corpus/manifest.json').read_text());
 for k in ('schema_version','datasets','total_budget_gb'):
  if k not in m: warnings.append(f'manifest missing {k}')
except Exception as e: errors.append(f'bad corpus manifest: {e}')
xml=(root/'app/src/main/AndroidManifest.xml').read_text(errors='ignore')
if 'android.permission.INTERNET' in xml: errors.append('INTERNET permission present')
for p in root.rglob('*'):
 if p.is_file() and '.git' not in p.parts:
  t=p.read_text(errors='ignore') if p.suffix in {'.kt','.kts','.xml','.java','.gradle','.py','.sh','.md'} else ''
  if re.search(r'(?i)\b(http|https)://',t) and p.name not in {'AndroidManifest.xml'} and p.parts[-2:] not in [('docs','LLAMA_INTEGRATION.md')]: warnings.append(f'network-looking URL in {p.relative_to(root)} (review; build-time docs are allowed)')
for req in ['docs/BOUNTY_CHECKLIST.md','docs/SUBMISSION_CHECKLIST.md','docs/FINALIZATION.md','docs/FINALIZATION_GATES.md','docs/REAL_DEVICE_RUNBOOK.md','docs/COMPETITIVE_POSITIONING.md','docs/BOUNTY_CLAIM_TEMPLATE.md','bench/benchmark_questions.json','bench/hard_questions.json','tools/build_corpus.py','tools/verify_offline_policy.py','tools/generate_release_manifest.py','RELEASE_STATUS.json']:
 if not (root/req).exists(): errors.append(f'missing {req}')
print('OFFLINE REPOSITORY AUDIT'); print('errors:',len(errors),'warnings:',len(warnings))
for x in errors: print('ERROR:',x)
for x in warnings[:20]: print('WARN :',x)
if not errors: print('PASS: static repository checks')
sys.exit(1 if errors else 0)
