# FINAL STATUS — Offline Research Android

This repository is the **maximum reproducible source package** prepared for the POIDH offline research bounty.

## Implemented
- Offline-only Android manifest with no INTERNET permission.
- Local GGUF import with atomic replacement, GGUF magic validation, SHA-256 and 50 GB storage guard.
- Persistent Android SQLite lexical index and deterministic local corpus repository.
- Research planner: synthesis, comparison, multi-hop and timeline.
- Entity-aware lexical retrieval, evidence objects and contradiction detection.
- Grounded prompt rules that forbid fabricated source IDs.
- Local inference backend seam for the official llama.cpp Android binding.
- Retrieval/total latency and PSS measurement in the UI.
- Benchmark and repository audit tooling.

## Not honestly claimable from source alone
- Successful native llama.cpp compilation in this environment.
- Successful inference on a physical Android device.
- GrapheneOS device verification.
- Zero-network verification under airplane mode.
- Production-scale corpus quality/coverage.
- A bounty win or Vitalik confirmation.

These require a real build machine/device and must be recorded as evidence before submission.
