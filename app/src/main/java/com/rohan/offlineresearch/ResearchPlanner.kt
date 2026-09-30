package com.rohan.offlineresearch

object ResearchPlanner {
    private val stop = setOf("what", "which", "when", "where", "how", "why", "does", "did", "the", "and", "for", "with", "from", "that", "this", "এর", "কি", "কী", "কেন", "কিভাবে", "কীভাবে", "এবং", "জন্য", "থেকে")

    fun plan(question: String, requested: ResearchType? = null): ResearchPlan {
        val q = question.trim()
        val lower = q.lowercase()
        val type = requested ?: when {
            Regex("\\b(compare|versus|vs\\.?|difference|similar|same as|তুলনা|পার্থক্য)\\b").containsMatchIn(lower) -> ResearchType.COMPARISON
            Regex("\\b(timeline|chronology|before|after|between|history|evolution|সময়রেখা|ইতিহাস|আগে|পরে)\\b").containsMatchIn(lower) -> ResearchType.TIMELINE
            Regex("\\b(why|how did|how does|what caused|relationship|connected|because|led to|influenced|কেন|কীভাবে|কারণ|সম্পর্ক|প্রভাব)\\b").containsMatchIn(lower) -> ResearchType.MULTI_HOP
            else -> ResearchType.SYNTHESIS
        }
        val entities = extractEntities(q)
        return ResearchPlan(q, type, entities)
    }

    fun tokenize(text: String): List<String> = text.lowercase()
        .split(Regex("[^\\p{L}\\p{N}]+"))
        .filter { it.length >= 2 && it !in stop }
        .distinct()

    private fun extractEntities(q: String): List<String> {
        val quoted = Regex("[\\\"'“”‘’]([^\\\"'“”‘’]{2,})[\\\"'“”‘’]")
            .findAll(q).map { it.groupValues[1].trim() }
        val latinProper = Regex("\\b[A-Z][A-Za-z0-9_-]{2,}(?:\\s+[A-Z][A-Za-z0-9_-]{2,}){0,3}\\b")
            .findAll(q).map { it.value.trim() }
        val meaningful = tokenize(q).sortedByDescending { it.length }.take(8)
        return (quoted + latinProper + meaningful).distinct().take(12).toList()
    }
}

data class ResearchPlan(val question: String, val type: ResearchType, val entities: List<String>)
enum class ResearchType { SYNTHESIS, COMPARISON, MULTI_HOP, TIMELINE }
