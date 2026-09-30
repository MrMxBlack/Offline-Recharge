# Offline Research — Final Static Candidate

An Android-first offline AI research assistant for the POIDH offline research bounty.

## What is implemented

`Question → Research Planner → Entity Extraction → Local SQLite/FTS Retrieval → Evidence + Conflict Analysis → Grounded Prompt → Local GGUF → Citation Validation`

The app includes:

- synthesis, comparison, multi-hop and timeline research modes;
- deterministic local retrieval with stable evidence IDs;
- citation validation and unsupported-citation removal;
- contradiction detection and explicit insufficient-evidence fallback;
- local GGUF import with GGUF v2/v3 header validation and SHA-256;
- local JSON corpus import with validation and atomic replacement;
- persistent SQLite/FTS indexing;
- 50 GB managed-storage guard;
- no `INTERNET` permission and no cloud inference dependency;
- fail-closed benchmark, device-evidence and release-truth tooling.

## Honest status

This archive is the **Final 1.3 static build handoff**, not a verified bounty submission.

The following remain runtime/build gates and are deliberately not fabricated:

- official Gradle Wrapper generated and committed;
- exact pinned llama.cpp source vendored;
- clean Release APK build;
- native GGUF inference on a physical Android device;
- measured TTFT, generation speed, load time, PSS/RAM and storage;
- 10+ traceable research benchmark runs;
- packet-level zero-network proof;
- GrapheneOS verification, if used as a target;
- clean-machine reproducibility.

`RELEASE_STATUS.json` therefore correctly keeps `bounty_ready=false`.

## Build order

1. On a build machine with **Gradle 8.11.1**, run `tools/bootstrap_gradle_wrapper.sh`.
2. Integrate the exact pinned llama.cpp commit with `tools/integrate_llama_android.sh`.
3. Commit the resulting Wrapper and native source checkout.
4. Run `tools/final_build_gate.sh` from the clean checkout.
5. Install the resulting Release APK on a real device.
6. Run `tools/run_device_validation.sh`, the benchmark suite and packet-proof protocol.
7. Run the final release gate only after all runtime evidence artifacts exist.

## Runtime constraints

- Android arm64 target
- maximum 12 GB physical RAM
- maximum 50 GB managed app storage
- no network permission
- local model, local corpus and local retrieval
- no cloud inference or web search during normal use

## Development model

Qwen3-1.7B GGUF Q4_K_M remains the development baseline. The final model must be selected from measured device performance and research quality, not assumed support.
