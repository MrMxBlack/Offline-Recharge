# Final Candidate 1.0

This is the final static source candidate. It is intentionally not marked bounty-ready until runtime evidence is produced on a real Android device.

### Included
- Offline-only Android manifest.
- Local SQLite/FTS retrieval.
- Synthesis, comparison, multi-hop and timeline planning.
- Evidence IDs, citation validation and contradiction detection.
- GGUF v2/v3 structural validation and SHA-256.
- Offline JSON corpus import with validation and atomic replacement.
- 50 GB managed-storage budget.
- Native llama.cpp integration contract pinned to an exact commit.
- Fail-closed build, benchmark, device-evidence and release-truth gates.

### Required to finish the app
- Commit official Gradle 8.11.1 Wrapper files.
- Vendor exact llama.cpp commit.
- Build a clean Release APK.
- Run native GGUF inference on physical Android hardware <=12 GB RAM.
- Measure load time, TTFT, tokens/s, total latency and PSS.
- Run 10+ traceable research questions.
- Capture zero runtime network traffic with packet-level evidence.
- Publish model/corpus/native/APK checksums and reproducibility evidence.
