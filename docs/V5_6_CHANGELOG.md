# V5.6 Changelog

## Reliability
- Model import now enforces the 50 GB budget while streaming, before finalization.
- Removed the arbitrary 100 MB minimum model size; GGUF validation checks the magic/header instead.
- Temporary model files are deleted on failed imports.
- Output is flushed and synced before finalizing the imported model.

## Native integration
- Added `tools/verify_native_integration.sh`.
- Native verification now checks the pinned llama.cpp Git commit, Android JNI bridge, AiChat API, InferenceEngine API, and app provider wiring.
- Integration setup refuses to silently adapt to an unknown upstream CMake layout.

## Truth boundary
- No physical-device inference, GrapheneOS validation, packet-capture proof, or benchmark results are fabricated.
