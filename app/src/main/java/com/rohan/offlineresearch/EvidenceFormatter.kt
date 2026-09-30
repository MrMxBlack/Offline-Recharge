package com.rohan.offlineresearch

object EvidenceFormatter {
    fun fallback(
        plan: ResearchPlan,
        evidence: List<Evidence>,
        conflicts: List<Conflict>
    ): String {
        if (evidence.isEmpty()) {
            return "The local corpus does not contain enough evidence to answer this question."
        }

        val body = buildString {
            append("Local evidence found for: ${plan.question}\n\n")
            evidence.take(6).forEach {
                append("[${it.id}] ${it.title}: ${it.text}\n\n")
            }
            if (conflicts.isNotEmpty()) {
                append("Potential source conflicts were detected and require model-level review.\n")
            }
        }
        return body
    }
}
