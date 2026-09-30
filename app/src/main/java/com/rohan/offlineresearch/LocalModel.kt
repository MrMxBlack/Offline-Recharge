package com.rohan.offlineresearch

import android.content.Context
import kotlinx.coroutines.flow.Flow
import java.io.File

/**
 * Stable app-side abstraction over the optional, pinned llama.cpp Android binding.
 *
 * The app itself never opens a network connection. Before native integration,
 * the provider reports a deterministic unavailable state. The integration script
 * adds the real provider backed by the official llama.android module.
 */
class LocalModel(context: Context) {
    private val provider: NativeInferenceProvider = NativeInferenceProviderFactory.create(context.applicationContext)

    suspend fun load(model: File) = provider.load(model)
    suspend fun setSystemPrompt(prompt: String) = provider.setSystemPrompt(prompt)
    fun generate(prompt: String, predictLength: Int = 768): Flow<String> = provider.generate(prompt, predictLength)
    suspend fun benchmark(promptTokens: Int = 256, generationTokens: Int = 128): String =
        provider.benchmark(promptTokens, generationTokens)
    fun release() = provider.release()
    fun destroy() = provider.destroy()
    fun isNativeReady(): Boolean = provider.isReady()
}

interface NativeInferenceProvider {
    suspend fun load(model: File)
    suspend fun setSystemPrompt(prompt: String)
    fun generate(prompt: String, predictLength: Int): Flow<String>
    suspend fun benchmark(promptTokens: Int, generationTokens: Int): String
    fun release()
    fun destroy()
    fun isReady(): Boolean
}

object NativeInferenceProviderFactory {
    fun create(context: Context): NativeInferenceProvider {
        return runCatching {
            val clazz = Class.forName("com.rohan.offlineresearch.LlamaAndroidNativeProvider")
            clazz.getConstructor(Context::class.java).newInstance(context) as NativeInferenceProvider
        }.getOrElse { UnavailableNativeInferenceProvider() }
    }
}

private class UnavailableNativeInferenceProvider : NativeInferenceProvider {
    override suspend fun load(model: File) = error("Native llama.cpp binding is not integrated. Run tools/integrate_llama_android.sh.")
    override suspend fun setSystemPrompt(prompt: String) = Unit
    override fun generate(prompt: String, predictLength: Int): Flow<String> =
        kotlinx.coroutines.flow.emptyFlow()
    override suspend fun benchmark(promptTokens: Int, generationTokens: Int): String =
        error("Native llama.cpp binding is not integrated.")
    override fun release() = Unit
    override fun destroy() = Unit
    override fun isReady(): Boolean = false
}
