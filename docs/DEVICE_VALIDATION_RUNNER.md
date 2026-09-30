# Device Validation Runner

`tools/run_device_validation.sh` is the single entry point for collecting reproducible release/device evidence after a real APK exists.

## Usage

```bash
./tools/run_device_validation.sh app/build/outputs/apk/release/app-release.apk evidence/device-run
```

The runner requires an authorized physical Android device through `adb`. It installs the APK, launches the package, records device/ABI/Android/network-state information, captures `/proc/net` snapshots, records memory information, and hashes the APK.

## Important boundary

Collection is **not proof**. The runner deliberately does not set `native_inference=true` or `zero_network_runtime_proof=true`. Native GGUF inference must be demonstrated by the benchmark run, and zero-network behavior requires the packet-capture protocol in `docs/DEVICE_TEST_PROTOCOL.md`.

A device result may only be promoted to the final bounty evidence after `tools/validate_device_result.py` passes against a completed evidence JSON and the packet capture/hash are preserved.
