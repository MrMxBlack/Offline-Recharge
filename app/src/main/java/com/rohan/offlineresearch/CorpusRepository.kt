package com.rohan.offlineresearch

import android.content.Context

class CorpusRepository(context: Context) {
    private val corpus = LocalCorpus(context)
    private val index = CorpusIndex(context)
    @Volatile private var ready = false
    @Volatile private var indexedSignature = ""
    private val contextRef = context.applicationContext

    fun ensureIndexed() {
        val signature = CorpusManager(contextRef).sha256().ifBlank { "asset-corpus" }
        if (ready && signature == indexedSignature) return
        synchronized(this) {
            if (!ready || signature != indexedSignature) {
                index.rebuild(corpus.all())
                indexedSignature = signature
                ready = true
            }
        }
    }

    fun all(): List<CorpusDoc> { ensureIndexed(); return corpus.all() }
    fun search(query: String, limit: Int = 12): List<CorpusDoc> {
        ensureIndexed()
        return index.search(query, limit)
    }
}
