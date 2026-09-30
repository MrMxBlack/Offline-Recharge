# Native Integration Status

## What is now implemented

- `:llama` module is reserved for the official `ggml-org/llama.cpp`
  `examples/llama.android/lib` source.
- GGUF model import copies the selected file into app-private storage.
- Default model target is `Qwen3-1.7B-Q4_K_M.gguf`.
- Research pipeline consumes a streaming `Flow<String>` from the local model.
- App has no INTERNET permission.
- The model is not fetched from the network by the app.

## Why the native source is not embedded in this archive

This build environment cannot clone GitHub, so embedding an unverified copy
of a large native dependency would be unsafe and could make the project
unreproducible.

Run:

    ./tools/integrate_llama_android.sh <exact-llama.cpp-commit>

The official Android binding currently uses:
- compileSdk 36
- minSdk 33
- NDK 29
- arm64-v8a and x86_64
- CMake 3.31.6
- GGML_NATIVE=OFF
- GGML_BACKEND_DL=ON
- GGML_CPU_ALL_VARIANTS=ON
- GGML_LLAMAFILE=OFF

These values should be checked again against the pinned commit before release.

## Model

`ggml-org/Qwen3-1.7B-GGUF` provides a Q4_K_M GGUF of about 1.28 GB and is
Apache-2.0 licensed. It is the development baseline, not the final bounty
model. A stronger local model/corpus will be needed to compete for the stated
research-quality bar.
