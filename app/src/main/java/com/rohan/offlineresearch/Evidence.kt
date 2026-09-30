package com.rohan.offlineresearch

data class Evidence(
    val id: String,
    val title: String,
    val text: String,
    val kind: String,
    val score: Double
)
