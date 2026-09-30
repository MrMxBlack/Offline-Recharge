# V5.14 Changelog

## Offline build gating
- Synchronized application version and build-contract checks to V5.14.
- `prepare_reproducible_build.sh` now verifies pinned native source when `OFFLINE_BUILD=1`; it no longer suppresses native verification failures.
- `build_release_candidate.sh` now fails closed if offline mode is requested without vendored llama.cpp; it will not fetch source in an offline build.
- Reproducibility checks remain fail-closed when Gradle Wrapper files are absent.

## Truth status
This release remains a host-build handoff candidate. No native inference, physical-device, packet-level, GrapheneOS, or bounty-ready claim is made.
