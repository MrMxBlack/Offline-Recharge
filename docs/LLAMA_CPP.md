# llama.cpp Android integration

Use the official `examples/llama.android` binding from ggml-org/llama.cpp.

Official Android documentation:
https://github.com/ggml-org/llama.cpp/blob/master/docs/android.md

The binding supports:
- GGUF metadata parsing
- loading a model from an app-private path
- `InferenceEngine` through `AiChat`
- prompt submission
- Kotlin Flow token streaming
- hardware acceleration based on device CPU capabilities

Integration target:
1. Pin a specific llama.cpp commit.
2. Add its Android binding as a source module/submodule.
3. Replace LocalModel.generate with an adapter around InferenceEngine.
4. Store GGUF files only in app-private storage.
5. Benchmark TTFT, tokens/sec and peak RAM on real devices.

Do not claim bounty completion until the app has been built and tested on a
compatible Android/GrapheneOS device.
