# V5.24 Changelog

## Release-candidate handoff hardening

- Bumped release metadata to V5.24 and Android versionCode/versionName.
- Removed the stale hard-coded V5.18 app-version mapping from `verify_build_reproducibility.py`.
- Reproducibility audit now derives the expected `v5.N` marker from `RELEASE_STATUS.json`.
- Kept the audit fail-closed when the real Gradle Wrapper is absent.
- Kept runtime/native/device/network claims explicitly unverified until measured on real hardware.
- No synthetic benchmark, model, packet capture, or device evidence was added.
