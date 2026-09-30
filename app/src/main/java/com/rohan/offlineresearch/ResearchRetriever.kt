package com.rohan.offlineresearch

class ResearchRetriever(private val corpus: CorpusRepository) {
    fun retrieve(plan: ResearchPlan, maxResults: Int = 12): List<Evidence> {
        corpus.ensureIndexed()
        val queries = linkedSetOf(plan.question).apply {
            addAll(plan.entities.filter { it.length >= 2 })
            if (plan.type == ResearchType.COMPARISON) {
                add(plan.entities.take(2).joinToString(" "))
            }
        }
        val candidates = queries.flatMap { corpus.search(it, maxResults * 2) }
            .plus(corpus.all())
            .distinctBy { it.id }
        val terms = ResearchPlanner.tokenize(plan.question)
        val entities = plan.entities.flatMap(ResearchPlanner::tokenize).distinct()
        return candidates.map { doc ->
            val title = doc.title.lowercase()
            val text = doc.text.lowercase()
            val all = "$title $text"
            val lexical = terms.count { term -> all.contains(term) }.toDouble()
            val titleHits = terms.count { term -> title.contains(term) } * 2.0
            val entityHits = entities.count { term -> all.contains(term) } * 1.75
            val phraseBoost = if (all.contains(plan.question.lowercase())) 4.0 else 0.0
            val typeBoost = when (plan.type to doc.kind) {
                ResearchType.COMPARISON to "comparison" -> 3.0
                ResearchType.MULTI_HOP to "relationship" -> 3.0
                ResearchType.TIMELINE to "timeline" -> 3.0
                ResearchType.SYNTHESIS to "reference" -> 1.0
                else -> 0.0
            }
            Evidence(doc.id, doc.title, doc.text, doc.kind, lexical + titleHits + entityHits + phraseBoost + typeBoost)
        }.filter { it.score >= 1.0 }.sortedByDescending { it.score }.take(maxResults)
    }
}
