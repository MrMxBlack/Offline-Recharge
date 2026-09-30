# Native API wiring

The current official llama.cpp Android binding exposes:

`AiChat.getInferenceEngine(context)`

and `InferenceEngine` methods:

- `loadModel(pathToModel: String)`
- `setSystemPrompt(systemPrompt: String)`
- `sendUserPrompt(message: String, predictLength: Int): Flow<String>`
- `bench(pp, tg, pl, nr)`
- `cleanUp()`
- `destroy()`

The adapter in `app/.../LocalModel.kt` now targets those exact methods.

The remaining repository operation is mechanical:
copy the official `examples/llama.android/lib` module into `:llama`
from one exact, reviewed llama.cpp commit.

Official docs and API:
https://github.com/ggml-org/llama.cpp/blob/master/docs/android.md
