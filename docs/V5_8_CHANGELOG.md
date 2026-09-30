# V5.8 Changelog

- Hardened native integration preflight.
- Added explicit air-gapped build mode: the build must fail if the pinned llama.cpp source is not already vendored.
- Added upstream Android binding contract checks before Gradle compilation.
- Added model pin manifest for the official Qwen3-1.7B GGUF baseline.
- Added a reproducible build checklist covering JDK 17, Android SDK/NDK, CMake, ABI and dependency state.
- Added a final pre-device gate that refuses to mark native inference verified without actual evidence.
- Updated release truth status to V5.8.

V5.8 is still not bounty-ready until native inference, a physical Android/GrapheneOS run, real benchmarks and zero-network runtime evidence are produced.
