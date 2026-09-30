# Bounty Verification Checklist

## Offline
- [x] Android manifest contains no INTERNET permission
- [ ] Verify no transitive library performs network requests
- [ ] Airplane-mode demo
- [ ] Network traffic capture showing zero app requests

## Model
- [ ] Pin llama.cpp commit
- [ ] Load real GGUF
- [ ] Stream tokens
- [ ] Measure TTFT
- [ ] Measure tokens/sec
- [ ] Measure peak RAM

## Research
- [x] Retrieval -> evidence -> synthesis pipeline
- [ ] Production knowledge corpus
- [ ] Multi-hop retrieval
- [ ] Contradiction detection
- [ ] Citation spans
- [ ] Query router
- [ ] Hard benchmark set

## Resource limits
- [ ] <=12GB RAM target device
- [ ] <=50GB total installed model/index/db/assets
- [ ] Real compatible GrapheneOS device
- [ ] Install/run from GitHub in minutes

## Proof
- [ ] Public GitHub repo
- [ ] Public X/Farcaster offline demo
- [ ] Multiple benchmark queries
- [ ] Include GitHub link
- [ ] Submit proof to POIDH

This checklist deliberately separates source-code readiness from verified
real-device bounty compliance.
