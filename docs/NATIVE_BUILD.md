# Native llama.cpp build

This project pins llama.cpp to commit `d230ddd763ffe27781c7ffd237ea78b639b36b6d`.

`tools/integrate_llama_android.sh` clones that exact revision, copies the official `examples/llama.android/lib` binding into `:llama`, wires the module into the app, and installs the real `InferenceEngine` adapter.

## One-time developer setup

Requires an internet-connected development machine, Android SDK, JDK 17, Android NDK 29.0.13113456, and CMake 3.31.6.

```bash
./tools/integrate_llama_android.sh
./tools/verify_native_source.sh
gradle :app:assembleDebug
```

After source/model installation, runtime inference is local-only. The app manifest has no `android.permission.INTERNET`.
