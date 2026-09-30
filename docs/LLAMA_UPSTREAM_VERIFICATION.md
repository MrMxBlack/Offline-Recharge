# llama.cpp upstream verification

The official llama.cpp Android documentation currently describes two supported paths:
- import `examples/llama.android` into Android Studio; and
- cross-compile with Android NDK/CMake for `arm64-v8a`.

The Android binding exposes `AiChat.getInferenceEngine(context)`, local GGUF model loading, token streaming through Kotlin `Flow`, and a benchmark API.

For reproducibility, this project pins one exact llama.cpp commit in `tools/integrate_llama_android.sh`. Before a bounty release, rerun the integration script and record the resulting Git SHA and build artifacts.
