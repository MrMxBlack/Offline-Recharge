# Offline Research Android — Final 1.2 Build Handoff

This is the final static/build-handoff package. It is intentionally **not** marked bounty-ready.

## Verified in this environment
- Android `INTERNET` permission absent.
- Static offline policy: PASS, 0 errors, 0 warnings.
- Android build contract: PASS.
- Release truth: PASS.
- Archive layout: PASS.
- Shell/Python validation scripts are present.
- GGUF import validation is local and fail-closed.
- Local corpus import/index path is present.
- Benchmark/device evidence validators are fail-closed.

## Environment blockers that cannot be honestly simulated
- No Gradle executable / official Gradle Wrapper artifacts available here.
- No Android SDK/NDK/CMake toolchain available here.
- No vendored llama.cpp checkout at the pinned commit.
- No physical Android device/ADB available here.
- No real GGUF model artifact and no native inference run.
- Corpus is a development seed; production open-license corpus must be prepared and checksummed.

## Final execution order on a real build host
1. Generate official Gradle Wrapper with Gradle 8.11.1 and commit all Wrapper files.
2. Vendor llama.cpp at the exact pinned commit and verify its clean tree.
3. Prepare an open-license corpus with provenance/license/version/checksums; build the local index.
4. Add a verified GGUF model within the 50 GB budget and record SHA-256.
5. Run the offline build gate and produce the release APK.
6. Install on arm64 Android hardware with <=12 GB physical RAM.
7. Run research benchmark questions in Answer/Synthesis/Comparison/Multi-hop/Timeline modes.
8. Record model load time, TTFT, tokens/sec, total latency, PSS/RAM and storage.
9. Run packet capture with Wi-Fi, mobile data and other network transports disabled.
10. Validate every evidence artifact and only then change `bounty_ready` to true.
