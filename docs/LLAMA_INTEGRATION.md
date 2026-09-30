# Native llama.cpp integration

The app intentionally ships without a copied native llama.cpp tree because the upstream project is large and changes independently. The official Android documentation says the `examples/llama.android` project can be imported into Android Studio and that its binding exposes GGUF loading, an `InferenceEngine`, token streaming and benchmarks. The binding currently targets Android API 33+ and uses Android NDK 29 in its Gradle configuration.

## Reproduce

1. Install Android Studio, JDK 17, Android SDK 36, NDK 29 and CMake 3.31.x.
2. Set an exact llama.cpp commit in `LLAMA_COMMIT`.
3. Run `tools/integrate_llama_android.sh`.
4. Add the generated `:llama` module to `settings.gradle.kts` and `implementation(project(":llama"))` to `app/build.gradle.kts`.
5. Replace `LocalModel.kt` with the provided native adapter template in `tools/LocalModelLlamaBackend.kt.template`.
6. Build and test on a physical arm64 Android device.

The baseline app has no `INTERNET` permission. Native inference must remain entirely local; do not add a server, socket, HTTP client, telemetry SDK, or cloud API.
