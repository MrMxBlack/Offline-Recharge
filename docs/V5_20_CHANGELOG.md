# V5.20 Changelog

## Evidence traceability hardening

- Device benchmark rows now require query, mode, retrieval latency, TTFT, generation latency, total latency, token count, output SHA-256, and retrieved evidence IDs.
- Benchmark manifest itself is now hash-linked into device evidence validation.
- Added `tools/verify_benchmark_evidence.py` to reject manifest-only or synthetic benchmark claims.
- Validation remains fail-closed until real device/native inference evidence exists.
