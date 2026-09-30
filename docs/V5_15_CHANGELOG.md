# V5.15 Changelog

## Build compatibility hardening

- Corrected the reproducibility gate to require an exact Gradle 8.11.1-or-newer 8.x distribution for AGP 8.9.1.
- Added a required Gradle distribution SHA-256 pin in the wrapper contract.
- Kept Gradle Wrapper files fail-closed: no fake wrapper JAR is generated.
- Synchronized Android build-contract and release metadata to V5.15.
- `bounty_ready` remains false until native inference, release APK, physical-device, and zero-network evidence exist.

## Verified facts

AGP 8.9 requires Gradle 8.11.1 or newer according to Android's compatibility table. The project therefore pins the V5.15 contract to Gradle 8.11.1 rather than accepting arbitrary 8.x versions.
