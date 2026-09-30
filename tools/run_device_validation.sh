#!/usr/bin/env bash
set -euo pipefail
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
cd "$ROOT"
PKG="${PACKAGE_NAME:-com.rohan.offlineresearch}"
APK="${1:-app/build/outputs/apk/release/app-release.apk}"
OUT="${2:-evidence/device-run-$(date -u +%Y%m%dT%H%M%SZ)}"
mkdir -p "$OUT"
command -v adb >/dev/null || { echo 'FAIL: adb missing' >&2; exit 2; }
adb get-state >/dev/null 2>&1 || { echo 'FAIL: no authorized physical Android device' >&2; exit 3; }
test -f "$APK" || { echo "FAIL: APK not found: $APK" >&2; exit 4; }

adb install -r "$APK" > "$OUT/install.txt"
adb shell am force-stop "$PKG" || true
adb shell monkey -p "$PKG" 1 > "$OUT/launch.txt"
sleep 2
adb shell dumpsys package "$PKG" > "$OUT/package_dump.txt"
adb shell dumpsys meminfo "$PKG" > "$OUT/meminfo_before.txt" || true
adb shell getprop ro.product.model > "$OUT/device_model.txt"
adb shell getprop ro.build.version.release > "$OUT/android_version.txt"
adb shell getprop ro.product.cpu.abilist > "$OUT/abis.txt"
adb shell settings get global airplane_mode_on > "$OUT/airplane_mode.txt" || true
adb shell dumpsys connectivity > "$OUT/connectivity.txt" || true
adb shell cat /proc/net/route > "$OUT/routes.txt" || true
adb shell cat /proc/net/tcp > "$OUT/tcp.txt" || true
adb shell cat /proc/net/tcp6 > "$OUT/tcp6.txt" || true
sha256sum "$APK" > "$OUT/apk.sha256"

cat > "$OUT/DEVICE_RUN_STATUS.txt" <<EOF
DEVICE VALIDATION RUN
package=$PKG
apk=$APK
status=COLLECTED_NOT_VERIFIED

This script installs and launches the release APK and collects device/runtime evidence.
It does NOT claim native inference, zero-network traffic, or bounty readiness.
Those claims require the completed benchmark and packet-capture evidence described in docs/DEVICE_TEST_PROTOCOL.md.
EOF

echo "PASS: device validation collection completed: $OUT"
