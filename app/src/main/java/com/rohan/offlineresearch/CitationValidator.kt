package com.rohan.offlineresearch

/** Validates citations against the exact evidence set supplied to the local model. */
object CitationValidator {
    private val citation = Regex("\\[([A-Za-z0-9._:-]+)]")

    fun validate(answer: String, evidence: List<Evidence>): CitationValidation {
        val allowed = evidence.map { it.id }.toSet()
        val found = citation.findAll(answer).map { it.groupValues[1] }.toSet()
        val unknown = found.filterNot(allowed::contains).toSet()
        return CitationValidation(unknown.isEmpty(), found.intersect(allowed), unknown)
    }

    fun sanitize(answer: String, evidence: List<Evidence>): String {
        val validation = validate(answer, evidence)
        if (validation.valid) return answer
        val allowed = evidence.map { it.id }.toSet()
        val cleaned = citation.replace(answer) { match ->
            if (match.groupValues[1] in allowed) match.value else ""
        }.trim()
        return "$cleaned\n\n[LOCAL CITATION WARNING] Unsupported source references were removed: ${validation.unknownIds.sorted().joinToString(", ")}".trim()
    }
}

data class CitationValidation(
    val valid: Boolean,
    val citedIds: Set<String>,
    val unknownIds: Set<String>
)
