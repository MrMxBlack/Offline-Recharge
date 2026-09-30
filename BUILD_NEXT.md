# Build Next

FINAL-1.3 is the final static build handoff.

Required next actions on a real build host:
1. Install/use the pinned Gradle Wrapper (Gradle 8.11.1 for AGP 8.9.1).
2. Vendor the exact llama.cpp commit recorded in `third_party/LLAMA_CPP_PIN.json`.
3. Run `OFFLINE_BUILD=1 bash tools/build_release_candidate.sh` after all build inputs are vendored.
4. Install the resulting release APK on a physical Android device.
5. Run the device benchmark and packet-proof protocol.
6. Validate the resulting evidence with the fail-closed validators.

Do not set `bounty_ready=true` until native inference, physical-device testing, packet-level zero-network proof, and research benchmark evidence are all real and reproducible.
