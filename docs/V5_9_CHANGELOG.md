# V5.9 Change Log

V5.9 is a build-validation candidate, not a bounty-ready release.

## Added
- `tools/build_release_candidate.sh`: checks Android SDK/NDK, performs pinned llama.cpp integration when source is absent, runs native contract checks, unit tests and Release APK build.
- `tools/collect_device_evidence.sh`: collects device, package, memory, connectivity and route evidence without claiming zero network traffic.
- `tools/packet_proof_protocol.sh`: fail-closed protocol for packet-level offline proof.
- `evidence/release_artifact.schema.json`.

## Integrity rule
No native inference, device benchmark, GrapheneOS result, or zero-network result is marked verified without physical evidence.
