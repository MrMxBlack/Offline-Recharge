# V5.25 Final Build Handoff

This release is a **build/device handoff**, not a bounty-ready claim.

## Required sequence on a real build host

1. Install JDK 17, Android SDK API 36, NDK `29.0.13113456`, CMake `3.31.6`, and Gradle 8.11.1.
2. Generate and commit the official Gradle Wrapper. Do not fabricate `gradle-wrapper.jar`.
3. Vendor llama.cpp at the exact SHA in `third_party/LLAMA_CPP_PIN.json`.
4. Run `OFFLINE_BUILD=1 bash tools/integrate_llama_android.sh` and verify the native integration.
5. Run `bash tools/final_handoff_audit.sh`.
6. Build with `./gradlew --offline :app:testDebugUnitTest :app:assembleRelease` after all required dependencies are cached.
7. Install the release APK on a physical device with <=12 GB physical RAM.
8. Import a verified local GGUF and run the research benchmark.
9. Capture packet-level evidence while Wi-Fi/mobile data are disabled and retain the raw capture artifact plus SHA-256.
10. Only after all runtime evidence is independently validated may `RELEASE_STATUS.json` be changed to verified states.

## Important

The repository must never claim native inference, device validation, zero-network runtime proof, or bounty readiness merely because static scripts pass.
