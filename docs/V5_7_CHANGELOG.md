# V5.7 changelog

V5.7 is a release-candidate hardening pass. It does not claim physical native inference.

- Added `tools/native_preflight.sh` for deterministic environment/source checks.
- Made llama.cpp CMake source-root patching fail-closed and semantic.
- Added a structured device-evidence schema and validator.
- Added an example evidence record for the real-device gate.
- Bumped Android versionCode/versionName to V5.7.
- Preserved the offline/no-network runtime boundary.

Still unverified until run on a real build host/device:
- native llama.cpp build
- GGUF inference
- physical Android/GrapheneOS run
- zero-network proof
- measured latency/tokens/sec/PSS/storage
