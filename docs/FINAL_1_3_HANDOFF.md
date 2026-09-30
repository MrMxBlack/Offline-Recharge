# Final 1.3 Build Handoff

## Goal
Turn this static candidate into the tested offline Android research app without changing the architecture or inventing evidence.

## Required execution order
1. Install/use Gradle 8.11.1 and generate the official Wrapper; commit `gradlew`, `gradlew.bat`, `gradle/wrapper/gradle-wrapper.jar`, and `gradle-wrapper.properties` with distribution SHA-256.
2. Vendor `llama.cpp` at the pinned revision in `third_party/llama.cpp`; verify the clean checkout and Android binding contract.
3. Prepare an open-license corpus, populate `corpus/manifest.json` with provenance/license/version/checksums, then build the local index.
4. Import a verified GGUF model and record its SHA-256.
5. Run `tools/final_release_gate.sh` and build the Release APK.
6. Install the APK on an arm64 Android device with <=12 GB physical RAM.
7. Run the 10+ benchmark suite across Answer/Synthesis/Comparison/Multi-hop/Timeline modes.
8. Record model load time, TTFT, generation speed, total latency, PSS/RAM and storage.
9. Run packet-level capture while Wi-Fi, mobile data and other transports are disabled.
10. Validate every artifact with the evidence validators. Only then set `bounty_ready=true`.
