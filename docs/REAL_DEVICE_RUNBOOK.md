# Real Device Validation Runbook

## Goal
Prove that the application performs research locally on an Android/GrapheneOS-compatible arm64 device with no runtime network dependency.

## Record first
- device model
- SoC/CPU
- physical RAM
- Android version
- GrapheneOS version if applicable
- available storage
- APK SHA-256
- model filename, size and SHA-256
- corpus manifest checksum
- exact llama.cpp commit
- app version/build number

## Clean setup
1. Build the release APK from a clean checkout.
2. Install the APK with ADB.
3. Import the GGUF model locally.
4. Build/import the corpus locally.
5. Record storage before and after model/corpus setup.
6. Reboot the device.

## Offline test
1. Enable Airplane Mode.
2. Explicitly confirm Wi-Fi is off.
3. Explicitly confirm mobile data is off.
4. Do not grant network permissions; the app should not request them.
5. Open the app.
6. Run all benchmark categories.
7. Confirm model generation, retrieval and source IDs still work.
8. Repeat after force-stop/relaunch.

## Network proof
Use at least one independent observation method available on the test device, such as:
- packet capture at the test network boundary,
- router/AP traffic logs,
- GrapheneOS/Android network diagnostics where available.

Record the observation method and time window. A UI label saying “offline” is not proof by itself.

## Performance measurements
For each benchmark question record:
- model load time
- TTFT if exposed by the native backend
- generation tokens
- tokens/sec
- retrieval latency
- total latency
- peak process PSS
- managed storage usage

Do not include Android “extended/virtual RAM” as physical RAM.

## Research quality
Score each question for:
- factual grounding
- citation correctness
- evidence coverage
- multi-hop correctness
- contradiction handling
- uncertainty handling
- unsupported-claim rate

Keep the raw answers and scores in the release evidence folder.

## Pass criteria
A release can only claim verified offline native inference after all of the above are completed on a real device. Static scripts and emulator tests do not substitute for this gate.
