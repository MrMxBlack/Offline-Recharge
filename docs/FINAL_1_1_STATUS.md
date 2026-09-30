# Final Candidate 1.1 Status

This package is the final static/build-handoff candidate for the Offline Research Android app.

## Verified in the repository
- No Android `INTERNET` permission.
- Static offline-policy audit passes with zero errors and zero warnings.
- Android build contract passes.
- Release-truth audit passes.
- Archive layout is self-contained.
- GGUF import validation is local and fail-closed.
- Corpus import/index path is local.
- Benchmark/device evidence validators are fail-closed and do not manufacture runtime results.

## Not verified here
- A clean Android Release APK build: this execution environment does not contain the required Android SDK/NDK/Gradle toolchain.
- Vendored llama.cpp source at the pinned commit: source vendoring requires the build host or a connected preparation step.
- Native GGUF inference on physical Android hardware.
- GrapheneOS validation.
- Packet-level zero-network runtime proof.
- Real-device research benchmark results.

These are intentionally not marked verified. The app is not labelled bounty-ready until the final gates are backed by physical evidence.

## Final execution order
1. Generate and commit the official Gradle Wrapper with Gradle 8.11.1.
2. Vendor llama.cpp at the repository's exact pinned commit.
3. Run the offline build gate and produce the signed/unsigned release APK.
4. Install the APK on an arm64 Android device with <=12 GB physical RAM.
5. Import a real GGUF model and execute the research benchmark suite.
6. Capture memory, TTFT, generation speed, total latency and evidence/citation correctness.
7. Run packet-level network capture with all network transports disabled.
8. Validate the complete evidence bundle and only then update `RELEASE_STATUS.json` to bounty-ready.
