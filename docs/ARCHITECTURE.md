# Offline Research Architecture

Question
  -> local query classifier
  -> local retrieval
  -> evidence selection
  -> local LLM synthesis
  -> citation/uncertainty layer
  -> answer

Hard requirement:
- no INTERNET permission
- no API calls
- no remote inference
- no web search
- no Google Play Services dependency for core research

The native model layer is isolated behind LocalModel so llama.cpp can be
pinned independently of the UI and research pipeline.
