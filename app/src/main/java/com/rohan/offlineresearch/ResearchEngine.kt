package com.rohan.offlineresearch

import android.content.Context
import android.os.Debug

class ResearchEngine(context: Context) {
    private val corpus = CorpusRepository(context)
    private val retriever = ResearchRetriever(corpus)
    private val model = LocalModel(context)
    private val modelManager = ModelManager(context)

    suspend fun research(question: String, requestedMode: ResearchType? = null): ResearchResult {
        val start = System.nanoTime()
        val plan = ResearchPlanner.plan(question, requestedMode)
        val retrievalStart = System.nanoTime()
        val evidence = retriever.retrieve(plan)
        val retrievalMs = (System.nanoTime() - retrievalStart) / 1_000_000
        val conflicts = ContradictionDetector.detect(evidence)
        val prompt = PromptBuilder.build(plan, evidence, conflicts)

        if (evidence.isEmpty()) {
            return fallback(plan, evidence, conflicts, retrievalMs, start, "Insufficient local evidence")
        }

        if (!modelManager.hasModel()) return fallback(plan, evidence, conflicts, retrievalMs, start, "No verified local GGUF model installed")

        return try {
            model.load(modelManager.modelFile())
            model.setSystemPrompt(ResearchSystemPrompt.text)
            val chunks = mutableListOf<String>()
            model.generate(prompt, 768).collect { chunks += it }
            val generated = chunks.joinToString("")
            val validated = CitationValidator.sanitize(generated, evidence)
            ResearchResult(
                "LOCAL • grounded generation • ${evidence.size} evidence • ${conflicts.size} conflicts",
                validated, evidence.map { Source(it.id,it.title,it.text,it.score,it.kind) },
                ResearchMetrics(retrievalMs, elapsedMs(start), Debug.getPss()/1024, model.isNativeReady())
            )
        } catch (e: Exception) {
            fallback(plan,evidence,conflicts,retrievalMs,start,"Local model error: ${e.message ?: e::class.simpleName}")
        } finally {
            // Always release native inference resources, including cancellation/errors.
            runCatching { model.release() }
        }
    }

    private fun fallback(plan: ResearchPlan, evidence: List<Evidence>, conflicts: List<Conflict>, retrievalMs: Long, start: Long, reason: String): ResearchResult =
        ResearchResult("OFFLINE • $reason • retrieval ${retrievalMs}ms", EvidenceFormatter.fallback(plan,evidence,conflicts), evidence.map { Source(it.id,it.title,it.text,it.score,it.kind) }, ResearchMetrics(retrievalMs,elapsedMs(start),Debug.getPss()/1024,false))

    private fun elapsedMs(start: Long) = (System.nanoTime()-start)/1_000_000
}

data class ResearchResult(val status:String,val answer:String,val sources:List<Source>,val metrics:ResearchMetrics)
data class Source(val id:String,val title:String,val snippet:String,val score:Double,val kind:String)
data class ResearchMetrics(val retrievalMs:Long,val totalMs:Long,val pssMb:Int,val nativeInference:Boolean)
