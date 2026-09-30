# V5.12 changelog

- Fixed the release preflight script to validate V5.11 instead of stale V5.5 state.
- Fixed offline llama.cpp integration so `OFFLINE_BUILD=1` never performs `git fetch`.
- Added fail-closed verification of vendored llama.cpp commit and clean working tree in offline mode.
- Added `tools/verify_build_reproducibility.py` to detect missing Gradle wrapper artifacts and stale release metadata.
- Updated static release audit labels to V5.11.
- Kept all runtime/device/native/network claims unverified.

## Current truth

This package is still not bounty-ready. A real Android build host must provide the pinned Gradle wrapper, vendored llama.cpp checkout, Android SDK/NDK, a successful release APK build, and physical-device evidence.
