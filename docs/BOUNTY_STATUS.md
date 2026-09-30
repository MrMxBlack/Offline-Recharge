# Bounty readiness status

This repository is a serious candidate implementation, not a claim of winning the bounty.

Implemented locally in source:
- Android Compose research UI
- no INTERNET permission
- local corpus and evidence retrieval
- research-type planning: synthesis/comparison/multi-hop/timeline
- evidence IDs and grounded prompt rules
- contradiction detection
- GGUF validation, SHA-256 and 50 GB managed-storage guard
- benchmark question set and architecture documentation
- native llama.cpp integration seam

Final verification gates still open before calling it bounty-ready:
- import a specific pinned llama.cpp commit
- successful Gradle/native build with that binding
- real GGUF load + generation on physical Android hardware
- GrapheneOS-compatible device test
- airplane-mode/no-network test
- measured peak RAM, TTFT, tok/s, load time and storage
- production-scale open-license corpus and indexed artifact
- benchmark results on hard research questions
- public GitHub reproduction from a clean machine
- public demo/proof post
