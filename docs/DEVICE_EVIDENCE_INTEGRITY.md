# Device Evidence Integrity — V5.19

A device run is not considered verified merely because the APK installed or the app launched.

The final evidence JSON must contain:

- physical RAM (not extended RAM)
- model GGUF SHA-256
- Airplane Mode enabled
- Wi-Fi disabled
- mobile data disabled
- confirmed native GGUF inference
- at least 10 benchmark results
- a packet-capture artifact with SHA-256
- benchmark-manifest SHA-256

`tools/validate_device_result.py` is fail-closed. A textual `network_proof` field alone is insufficient.

## Required evidence chain

1. Build the APK from the committed Gradle Wrapper.
2. Install the exact APK on the physical device.
3. Import the exact GGUF and record its SHA-256.
4. Disable all network paths and record their state.
5. Run the research benchmark suite, including comparison and multi-hop questions.
6. Capture packets for the complete app session using the documented packet-capture protocol.
7. Record native inference metrics from the app/runtime.
8. Produce the evidence JSON and run `validate_device_result.py`.

Only after all eight steps pass may the final release gate consume the evidence.
