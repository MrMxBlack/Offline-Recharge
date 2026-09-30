#!/usr/bin/env python3
"""Build deterministic chunk JSON from a simple corpus.json document list.

Input records: id,title,kind,text plus optional source/license/version.
Output: corpus/chunks.json with stable doc_id#chunk_index identifiers.
No network access is performed.
"""
import argparse,json,pathlib,hashlib
p=argparse.ArgumentParser(); p.add_argument('--input',default='app/src/main/assets/corpus.json'); p.add_argument('--output',default='corpus/chunks.json'); p.add_argument('--chunk-chars',type=int,default=900); a=p.parse_args()
root=pathlib.Path(__file__).resolve().parents[1]
inp=root/a.input; out=root/a.output
items=json.loads(inp.read_text())
chunks=[]
for d in items:
    text=' '.join(str(d['text']).split())
    words=text.split()
    buf=[]; n=0; idx=0
    for w in words:
        if n+len(w)+(1 if buf else 0)>a.chunk_chars and buf:
            chunks.append({'id':f"{d['id']}#{idx}",'doc_id':d['id'],'chunk_index':idx,'title':d['title'],'kind':d.get('kind','reference'),'text':' '.join(buf),'source':d.get('source','local corpus'),'license':d.get('license','SEE DATASET MANIFEST'),'version':d.get('version','1')}); idx+=1; buf=[]; n=0
        buf.append(w); n+=len(w)+(1 if len(buf)>1 else 0)
    if buf: chunks.append({'id':f"{d['id']}#{idx}",'doc_id':d['id'],'chunk_index':idx,'title':d['title'],'kind':d.get('kind','reference'),'text':' '.join(buf),'source':d.get('source','local corpus'),'license':d.get('license','SEE DATASET MANIFEST'),'version':d.get('version','1')})
out.parent.mkdir(parents=True,exist_ok=True); out.write_text(json.dumps(chunks,indent=2,ensure_ascii=False)+'\n')
print(f'wrote {len(chunks)} chunks to {out}')
