# V5.5 Release Notes

V5.5 is the current release-candidate source package. It is intentionally conservative about what can be claimed without a physical Android test.

## Product objective

The application is an offline evidence-grounded research assistant, not merely a local chatbot. Its pipeline is:

`question → research plan → entity/query expansion → local retrieval → evidence/conflict analysis → grounded prompt → local GGUF generation → citation validation`

## What is implemented in source

- Local corpus and persistent SQLite lexical index
- Research modes: synthesis, comparison, multi-hop, timeline
- Entity-aware retrieval and query expansion
- Evidence sufficiency handling
- Contradiction heuristic
- Citation validation against retrieved IDs
- Local GGUF import, validation, SHA-256 and storage guard
- llama.cpp Android integration seam with pinned upstream revision
- Native-resource cleanup on success/failure
- Offline policy and release audit tooling
- Device benchmark/runbook documentation

## Required before calling it final

1. Integrate the pinned llama.cpp source.
2. Build a clean Release APK.
3. Load a real GGUF model.
4. Run hard research questions on physical Android hardware.
5. Record TTFT, load time, generation speed, PSS/RAM and storage.
6. Verify airplane-mode operation.
7. Capture zero network activity attributable to the app.
8. Publish checksums and reproducibility evidence.

Until these artifacts exist, `bounty_ready` must remain false.
