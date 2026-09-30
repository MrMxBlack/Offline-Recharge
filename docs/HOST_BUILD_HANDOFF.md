# V5.11 Host Build Handoff

V5.11 does not claim an APK build, native inference, physical-device validation, or packet-level offline proof. This document defines the exact handoff required to obtain those facts on a real Android build host.

## Required host

- JDK 17 for the upstream Android binding/build path.
- Android SDK with API 36.
- Android NDK `29.0.13113456`.
- CMake `3.31.6` for the current upstream Android library configuration.
- A pinned Gradle wrapper committed to the repository.
- Git for the one-time native-source vendor step.
- ADB for physical-device validation.

The current upstream llama.cpp Android library declares NDK `29.0.13113456`, API 33 minimum, arm64-v8a support, and CMake `3.31.6`. See the official Android binding configuration before changing these values.

## One-time connected step

From the repository root:

```bash
./tools/integrate_llama_android.sh
```

This must leave a real git checkout at `third_party/llama.cpp/` at the exact commit recorded in `third_party/LLAMA_CPP_PIN.json`. Commit the vendored source for the air-gapped release; do not make the final release depend on fetching source at build time.

## Build gate

```bash
./tools/run_host_validation.sh
```

The script intentionally stops if any of the following are absent:

- Gradle wrapper
- Android SDK
- required NDK
- vendored llama.cpp source
- static offline-policy proof
- release-truth proof

A successful host build still does **not** prove runtime offline behavior.

## Device gate

Install the release APK on a real arm64 Android device, import the exact GGUF, and collect:

- model load time
- TTFT
- generation tokens/sec
- total generation time
- peak PSS / RAM
- storage usage
- native backend state
- device/ABI/Android version

Use `tools/collect_device_evidence.sh` and validate with `tools/validate_device_result.py`.

## Offline network gate

Run the app with Wi-Fi, mobile data and other network transports disabled, then perform packet-level capture according to `tools/packet_proof_protocol.sh`. A missing capture is not evidence of zero network traffic.

## Research-quality gate

Run the hard benchmark set in `benchmarks/` and record accuracy, citation validity, evidence sufficiency, contradiction handling, and multi-hop/timeline performance. Do not substitute model benchmark numbers for research-system results.

## Final claim rule

Only after all gates pass may `RELEASE_STATUS.json` be changed to `bounty_ready: true`. Never change status fields merely because a script exists.
