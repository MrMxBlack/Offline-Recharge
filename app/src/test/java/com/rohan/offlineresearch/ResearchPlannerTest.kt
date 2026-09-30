package com.rohan.offlineresearch

import org.junit.Assert.assertEquals
import org.junit.Test

class ResearchPlannerTest {
    @Test fun comparisonIsDetected(){ assertEquals(ResearchType.COMPARISON, ResearchPlanner.plan("Compare Alpha vs Beta").type) }
    @Test fun timelineIsDetected(){ assertEquals(ResearchType.TIMELINE, ResearchPlanner.plan("History and timeline of Alpha").type) }
    @Test fun multihopIsDetected(){ assertEquals(ResearchType.MULTI_HOP, ResearchPlanner.plan("How did Alpha influence Beta?").type) }
}
