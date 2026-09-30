package com.rohan.offlineresearch

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ResearchViewModel(app: Application) : AndroidViewModel(app) {
    private val engine = ResearchEngine(app.applicationContext)
    private val _state = MutableStateFlow(ResearchState())
    val state = _state.asStateFlow()

    fun ask(question: String, mode: ResearchType? = null) {
        val q = question.trim()
        if (q.isBlank()) return
        viewModelScope.launch {
            _state.value = _state.value.copy(running = true, status = "Planning → indexing → retrieving → grounding…")
            try {
                val r = engine.research(q, mode)
                _state.value = ResearchState(false, r.status, r.answer, r.sources, r.metrics, mode)
            } catch (t: Throwable) {
                _state.value = ResearchState(false, "LOCAL ERROR • ${t.message ?: t::class.simpleName}", "", emptyList(), null, mode)
            }
        }
    }
    fun clear() { _state.value = ResearchState() }
}

data class ResearchState(
    val running: Boolean = false,
    val status: String = "Ready • local-only mode",
    val answer: String = "",
    val sources: List<Source> = emptyList(),
    val metrics: ResearchMetrics? = null,
    val mode: ResearchType? = null
)
