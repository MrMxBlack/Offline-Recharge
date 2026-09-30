# Native inference status

## What is implemented in V5.2

- `LocalModel` uses the official `AiChat` / `InferenceEngine` Android API exposed by the pinned llama.cpp Android example.
- The integration script checks out an exact llama.cpp commit, copies the Android binding, and patches its CMake source-root path to the project's `third_party/llama.cpp` location.
- A CI workflow performs source integration, native-source verification, unit tests, and an Android release build.
- The app has no `INTERNET` permission.

The upstream Android documentation describes the same binding path: GGUF models are loaded from app-private files and generated tokens are returned through a Kotlin `Flow`. citeturn0view0turn3view1

## What is NOT claimed

This repository does **not** claim that native inference has been physically verified on a real Android phone until a real-device run records successful model loading and generation.

It also does not claim GrapheneOS validation, zero-network verification, benchmark performance, or bounty readiness without corresponding evidence.

## Required next test

1. Run `./tools/integrate_llama_android.sh` while online.
2. Build the release APK.
3. Install on a compatible arm64 Android device.
4. Import a verified GGUF model.
5. Disable Wi-Fi/mobile data and enable Airplane Mode.
6. Run a research query.
7. Capture model-load success, generated output, TTFT/tokens/sec, PSS and storage.
8. Repeat with the network disabled and preserve the evidence bundle.
