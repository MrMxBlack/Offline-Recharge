package com.rohan.offlineresearch

import android.content.Context
import org.json.JSONArray

class LocalCorpus(private val context: Context) {
    private val seed: List<CorpusDoc> by lazy { loadSeedCorpus() }

    fun all(): List<CorpusDoc> = CorpusManager(context).loadImported().ifEmpty { seed }

    private fun loadSeedCorpus(): List<CorpusDoc> {
        val fallback = listOf(
            CorpusDoc("guide-1", "Research Method", "method", "A good research answer separates evidence, inference, and uncertainty."),
            CorpusDoc("guide-2", "Comparison Method", "method", "A comparison should identify shared dimensions and cite evidence for each side."),
            CorpusDoc("guide-3", "Multi-hop Method", "method", "A multi-hop answer should show each intermediate relationship rather than jumping to a conclusion.")
        )
        return try {
            val raw = context.assets.open("corpus.json").bufferedReader().use { it.readText() }
            val array = JSONArray(raw)
            buildList {
                for (i in 0 until array.length()) {
                    val o = array.getJSONObject(i)
                    add(CorpusDoc(o.getString("id"), o.getString("title"), o.optString("kind", "reference"), o.getString("text")))
                }
            }
        } catch (_: Throwable) { fallback }
    }
}

data class CorpusDoc(val id: String, val title: String, val kind: String, val text: String)
