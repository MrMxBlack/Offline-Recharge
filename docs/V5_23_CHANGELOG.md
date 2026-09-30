# V5.23 Changelog

## Evidence integrity hardening

- Fixed a real bug in `verify_benchmark_evidence.py`: missing `re` import.
- Benchmark evidence IDs must resolve to the deterministic local corpus catalog.
- Device evidence now requires a real model artifact and verifies its SHA-256.
- Device benchmark evidence IDs are cross-checked against the local evidence catalog.
- Android build contract now dynamically checks app `versionName` against `RELEASE_STATUS.json` instead of a stale hard-coded version map.
- Release audit banner no longer reports an obsolete V5.13 version.
- No runtime/device/native/network claims were promoted to verified.
