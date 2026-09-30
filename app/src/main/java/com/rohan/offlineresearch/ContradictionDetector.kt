package com.rohan.offlineresearch

object ContradictionDetector {
    fun detect(evidence: List<Evidence>): List<Conflict> {
        val conflicts = mutableListOf<Conflict>()

        for (i in evidence.indices) {
            for (j in i + 1 until evidence.size) {
                val a = evidence[i]
                val b = evidence[j]

                val aSaysNot = Regex("(?i)\\b(not|never|no|false|cannot)\\b").containsMatchIn(a.text)
                val bSaysNot = Regex("(?i)\\b(not|never|no|false|cannot)\\b").containsMatchIn(b.text)

                val shared = sharedTerms(a.text, b.text)
                if (shared.size >= 2 && aSaysNot != bSaysNot) {
                    conflicts += Conflict(a.id, b.id, shared)
                }
            }
        }

        return conflicts
    }

    private fun sharedTerms(a: String, b: String): Set<String> {
        val aa = words(a)
        val bb = words(b)
        return aa.intersect(bb).filter { it.length > 4 }.toSet()
    }

    private fun words(s: String): Set<String> =
        s.lowercase().split(Regex("[^\\p{L}\\p{N}]+"))
            .filter { it.length > 4 }
            .toSet()
}

data class Conflict(
    val sourceA: String,
    val sourceB: String,
    val sharedTerms: Set<String>
)
