# V5.13 changelog

- Synchronized all stale V5.11/V5.12 release-audit references to V5.13.
- Synchronized app version metadata to `1.11-v5.13-reproducibility-hardened`.
- Added `tools/verify_android_build_contract.py` for deterministic Android/llama.cpp build-contract checks.
- Added `tools/prepare_reproducible_build.sh` as the fail-closed handoff for generating and committing the real Gradle Wrapper.
- Preserved the rule that wrapper binaries must not be fabricated in this environment.
- Kept bounty readiness false until native source, APK build, device inference, packet proof, and benchmark evidence exist.
