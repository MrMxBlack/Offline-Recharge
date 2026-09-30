#!/usr/bin/env bash
set -euo pipefail
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
cd "$ROOT"
OUT="${1:-evidence/device-run-$(date -u +%Y%m%dT%H%M%SZ)}"
mkdir -p "$OUT"
command -v adb >/dev/null || { echo 'FAIL: adb missing' >&2; exit 2; }
adb get-state >/dev/null 2>&1 || { echo 'FAIL: no authorized Android device' >&2; exit 3; }
PKG="${PACKAGE_NAME:-com.rohan.offlineresearch}"
adb shell getprop ro.product.model > "$OUT/device_model.txt"
adb shell getprop ro.build.version.release > "$OUT/android_version.txt"
adb shell getprop ro.product.cpu.abilist > "$OUT/abis.txt"
adb shell dumpsys package "$PKG" > "$OUT/package_dump.txt"
adb shell dumpsys meminfo "$PKG" > "$OUT/meminfo.txt" || true
adb shell dumpsys connectivity > "$OUT/connectivity.txt" || true
adb shell settings get global airplane_mode_on > "$OUT/airplane_mode.txt" || true
adb shell dumpsys netstats > "$OUT/netstats.txt" || true
adb shell "cat /proc/net/route" > "$OUT/routes.txt" || true
adb shell "cat /proc/net/tcp" > "$OUT/tcp.txt" || true
adb shell "cat /proc/net/tcp6" > "$OUT/tcp6.txt" || true
python3 - "$OUT" <<'PY'
from pathlib import Path
import json, re, sys
p=Path(sys.argv[1])
package=(p/'package_dump.txt').read_text(errors='ignore')
manifest_internet='android.permission.INTERNET' in package
airplane=(p/'airplane_mode.txt').read_text(errors='ignore').strip()
obj={
 'package':'com.rohan.offlineresearch',
 'internet_permission_seen_in_package_dump':manifest_internet,
 'airplane_mode_setting':airplane,
 'network_packet_capture':'NOT_PROVIDED',
 'status':'EVIDENCE_COLLECTED_NOT_PROOF'
}
(p/'summary.json').write_text(json.dumps(obj,indent=2)+'\n')
print(json.dumps(obj,indent=2))
PY
echo "NOTE: this collector is evidence collection only. It does NOT claim zero network traffic. Packet-level proof requires a capture-capable test environment."
