# V5.11 changelog

- Synchronized app version metadata to V5.11.
- Added `tools/host_environment_report.py` for explicit SDK/NDK/JDK/Gradle/ADB/native-source readiness reporting.
- Added `tools/run_host_validation.sh`, a fail-closed host build gate.
- Added `docs/HOST_BUILD_HANDOFF.md` with the exact path from host setup to device/offline/research evidence.
- Added machine-generated `evidence/host_environment_report.json` showing the current environment state.
- Kept bounty readiness false because this environment still lacks the Android SDK, Gradle wrapper, vendored llama.cpp checkout and physical-device evidence.
