package com.rohan.offlineresearch

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CitationValidatorTest {
    private val evidence = listOf(
        Evidence("SRC-001", "One", "Evidence one", "reference", 2.0),
        Evidence("SRC-002", "Two", "Evidence two", "reference", 1.0)
    )

    @Test fun acceptsRetrievedCitation() {
        assertTrue(CitationValidator.validate("Claim [SRC-001]", evidence).valid)
    }

    @Test fun rejectsInventedCitation() {
        assertFalse(CitationValidator.validate("Claim [SRC-999]", evidence).valid)
    }
}
