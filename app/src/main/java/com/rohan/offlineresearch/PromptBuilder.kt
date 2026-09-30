package com.rohan.offlineresearch

object PromptBuilder {
    fun build(
        plan: ResearchPlan,
        evidence: List<Evidence>,
        conflicts: List<Conflict>
    ): String {
        val evidenceBlock = evidence.joinToString("\n\n") {
            "[${it.id}] ${it.title} (${it.kind}, score=${"%.2f".format(it.score)})\n${it.text}"
        }

        val conflictBlock = if (conflicts.isEmpty()) {
            "No automatically detected source conflict."
        } else {
            conflicts.joinToString("\n") {
                "Potential conflict: ${it.sourceA} vs ${it.sourceB}; shared terms=${it.sharedTerms.joinToString()}"
            }
        }

        return """
            RESEARCH QUESTION:
            ${plan.question}

            RESEARCH TYPE:
            ${plan.type}

            ENTITIES:
            ${plan.entities.joinToString(", ")}

            LOCAL EVIDENCE:
            $evidenceBlock

            CONFLICT CHECK:
            $conflictBlock

            RULES:
            1. Answer only from local evidence.
            2. Put [source-id] after claims supported by a source.
            3. If evidence is insufficient, explicitly say that.
            4. If sources conflict, describe the conflict instead of silently choosing.
            5. Do not invent facts or citations.
            6. For MULTI_HOP, explain each intermediate step.
            7. For COMPARISON, compare the same dimensions across the subjects.
            8. For TIMELINE, order dated evidence and flag gaps.
            9. Never cite a source ID that does not appear in LOCAL EVIDENCE.
            10. End with a short uncertainty note when evidence is incomplete.
        """.trimIndent()
    }
}
