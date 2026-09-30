# V5.10 Changelog

## Purpose
V5.10 is a build-validation hardening release. It does not claim native inference, physical-device validation, zero-network runtime proof, or bounty readiness.

## Fixes
- Corrected the release build script to execute `verify_offline_policy.py` with Python rather than Bash.
- Added `tools/verify_release_truth.py` to prevent runtime-validation claims from being inferred from static repository state.
- Added `tools/build_host_preflight.sh` for deterministic build-host prerequisite checks.
- Added truth checks before and after release build steps.
- Updated release status and documentation from V5.9 to V5.10.
- Kept native llama.cpp source vendoring fail-closed: an air-gapped final build cannot silently download upstream source.

## Still pending
- Exact pinned llama.cpp source vendored and verified.
- Gradle wrapper committed and verified.
- Clean Android Release APK build.
- Real GGUF inference on physical Android hardware.
- GrapheneOS validation.
- Packet-level zero-network runtime proof.
- Research-quality benchmark results.
