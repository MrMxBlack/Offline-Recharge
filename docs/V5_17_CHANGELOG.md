# V5.17 Changelog

## CI reproducibility hardening

- Android CI now requires a committed official Gradle Wrapper before any build can run.
- CI no longer installs an arbitrary Gradle distribution through the `gradle/actions/setup-gradle` action.
- Android CI installs the pinned Android platform/NDK/CMake toolchain used by the project contract.
- Native CI now runs the same reproducibility audit and invokes `./gradlew` for tests and release builds.
- App version metadata is synchronized to `1.15-v5.17-ci-reproducibility`.
- No synthetic `gradle-wrapper.jar` is included. The project remains blocked until a real Gradle 8.11.1 installation generates the official wrapper files.

## Truth status

This release remains **not bounty-ready**. No physical-device inference, packet-level network proof, GrapheneOS validation, or research benchmark result is fabricated.
