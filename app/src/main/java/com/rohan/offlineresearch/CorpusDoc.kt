package com.rohan.offlineresearch

/** Immutable local-corpus record used by retrieval, indexing and evidence formatting. */
data class CorpusDoc(
    val id: String,
    val title: String,
    val kind: String,
    val text: String,
)
