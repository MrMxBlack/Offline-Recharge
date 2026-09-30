#!/usr/bin/env bash
set -euo pipefail
command -v adb >/dev/null || { echo 'FAIL: adb missing' >&2; exit 2; }
adb get-state >/dev/null 2>&1 || { echo 'FAIL: no authorized device' >&2; exit 3; }
if ! adb shell 'command -v tcpdump' >/dev/null 2>&1; then
  echo 'FAIL: tcpdump is not available on the target device; no packet-level zero-network claim is permitted.' >&2
  exit 10
fi
cat <<'EOF'
Packet-proof protocol:
1. Start capture before launching the app.
2. Enable Airplane Mode and explicitly confirm Wi-Fi/mobile interfaces are disabled.
3. Launch the app.
4. Import/use an already-installed model and corpus.
5. Run the complete hard benchmark locally.
6. Stop capture.
7. Preserve the capture file and SHA-256.
8. Inspect for non-loopback outbound traffic during the benchmark.
9. Store the capture hash and device metadata in evidence/.
10. Only then may zero_network_runtime_proof be changed from NOT_VERIFIED.
EOF
