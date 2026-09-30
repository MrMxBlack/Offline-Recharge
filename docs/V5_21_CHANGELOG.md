# V5.21 Changelog

- Hardened benchmark evidence verification.
- Every benchmark row now requires an output artifact whose SHA-256 matches `output_sha256`.
- Benchmark evidence now requires `model_artifact` and verifies its SHA-256.
- Evidence IDs must be non-empty strings.
- Device-result validation applies the same output-artifact integrity checks.
- No synthetic benchmark, model hash, packet proof, native inference, or bounty-ready claim was added.
