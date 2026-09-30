# Reproducible build

## Required environment

- JDK 17
- Android SDK with compile/target API 36
- Android NDK 29.0.13113456
- CMake 3.31.6
- Gradle/Android Gradle Plugin versions pinned by the project build files
- arm64-v8a device for bounty validation

## Native source policy

The release build must use the exact llama.cpp commit recorded in `third_party/LLAMA_CPP_PIN.json`.
For a genuinely reproducible and air-gapped build, `third_party/llama.cpp/` must be vendored at that exact commit. The setup script may be used on a connected development machine to obtain it, but the final public release should not require a network request during the build.

## Build

```bash
./tools/native_preflight.sh
./tools/verify_native_source.sh
./tools/verify_native_integration.sh
./gradlew :app:testDebugUnitTest
./gradlew :app:assembleRelease
```

If the Gradle wrapper is absent, use the exact Gradle version documented by the final release manifest rather than an arbitrary system version.

## Runtime offline verification

1. Install the release APK.
2. Import the pinned GGUF model and local corpus.
3. Record model and corpus SHA-256.
4. Enable Airplane Mode and disable Wi-Fi/mobile data.
5. Run the benchmark suite.
6. Capture PSS, TTFT, tokens/sec, total latency and storage.
7. Collect network evidence independently.

No benchmark number may be entered into the bounty evidence until it comes from a real device run.
