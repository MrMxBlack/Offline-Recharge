# Research Engine

The app is no longer structured as a plain chatbot.

## Pipeline

Question
-> ResearchPlanner
-> ResearchRetriever
-> Evidence set
-> ContradictionDetector
-> PromptBuilder
-> local GGUF model
-> cited synthesis

## Query classes

- SYNTHESIS
- COMPARISON
- MULTI_HOP
- TIMELINE

## Current retrieval

The bundled corpus is intentionally tiny and is only a development fixture.
Production competition requires a much larger local knowledge pack and a
proper indexed retrieval system.

## Important limitation

The current contradiction detector is heuristic. It is useful as a second
signal but is not a semantic truth detector. It must not be presented as
proof that two sources actually contradict one another.

## Next production layer

1. SQLite/FTS5 document index
2. chunk-level IDs
3. BM25 retrieval
4. dense embeddings
5. reranker
6. entity graph
7. citation span mapping
8. benchmark harness
9. RAM/latency instrumentation
