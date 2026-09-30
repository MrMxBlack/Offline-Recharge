# Final App Gate

The project is considered a **final bounty candidate** only when every gate below has a recorded artifact.

1. `tools/integrate_llama_android.sh` succeeds against the pinned llama.cpp commit.
2. `tools/verify_native_integration.sh` passes with the vendored source at exactly that commit.
3. `./gradlew :app:assembleRelease` succeeds from a clean checkout.
4. A real GGUF model loads through the native binding.
5. At least 8 hard research questions produce streamed local output.
6. Citation validation rejects unknown source IDs.
7. Airplane Mode + Wi-Fi off + mobile data off still permits research queries.
8. A network monitor/packet capture records zero runtime network traffic attributable to the app.
9. Device RAM/PSS, TTFT, generation speed, load time and storage are recorded.
10. The test device's physical RAM is recorded separately from Android extended/virtual RAM.
11. The exact model SHA-256, corpus checksum, llama.cpp commit and release APK SHA-256 are published.
12. A clean-machine reproducibility test succeeds.

Until all eleven are evidenced, `bounty_ready` must remain false.
