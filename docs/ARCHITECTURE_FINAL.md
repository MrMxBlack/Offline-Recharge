# Final architecture

UI -> ResearchViewModel -> ResearchEngine

ResearchEngine:
1. ResearchPlanner classifies the question.
2. LocalCorpus loads licensed local references.
3. ResearchRetriever ranks evidence using lexical, title, entity, phrase and query-type signals.
4. ContradictionDetector identifies potential source conflicts.
5. PromptBuilder creates a grounded research prompt containing stable source IDs.
6. LocalModel sends the prompt to the local GGUF backend when the native llama.cpp module is installed.
7. The UI exposes answer + evidence IDs + offline state + resource budget.

No network layer exists in the baseline application manifest.
