#!/usr/bin/env python3
import json, os, platform, shutil, subprocess
from pathlib import Path

root = Path(__file__).resolve().parents[1]

def cmd(name, args=None):
    exe = shutil.which(name)
    if not exe:
        return None
    try:
        p = subprocess.run([exe] + (args or ["--version"]), text=True, stdout=subprocess.PIPE, stderr=subprocess.STDOUT, timeout=10)
        return p.stdout.strip().splitlines()[0] if p.stdout.strip() else exe
    except Exception as e:
        return f"ERROR: {e}"

def first_existing(*paths):
    for p in paths:
        if p and Path(p).exists():
            return str(Path(p))
    return None

sdk = first_existing(os.environ.get("ANDROID_SDK_ROOT"), os.environ.get("ANDROID_HOME"), str(Path.home()/"Android/Sdk"), str(Path.home()/"android-sdk"))
ndk_root = Path(sdk)/"ndk" if sdk else None
ndk_versions = []
if ndk_root and ndk_root.exists():
    ndk_versions = sorted([p.name for p in ndk_root.iterdir() if p.is_dir()])
required_ndk = "29.0.13113456"

report = {
    "project": "OfflineResearch",
    "version": "FINAL-1.3",
    "host": {
        "os": platform.platform(),
        "machine": platform.machine(),
        "java": cmd("java"),
        "gradle": cmd("gradle"),
        "cmake": cmd("cmake"),
        "ninja": cmd("ninja"),
        "adb": cmd("adb"),
        "git": cmd("git"),
        "python": cmd("python3"),
    },
    "android": {
        "sdk_root": sdk,
        "ndk_versions": ndk_versions,
        "required_ndk": required_ndk,
        "required_ndk_present": required_ndk in ndk_versions,
        "gradle_wrapper_present": (root/"gradlew").is_file() and (root/"gradle/wrapper/gradle-wrapper.jar").is_file() and (root/"gradle/wrapper/gradle-wrapper.properties").is_file(),
    },
    "native_source": {
        "vendored": (root/"third_party/llama.cpp/.git").is_dir(),
        "pin_file": (root/"third_party/LLAMA_CPP_PIN.json").is_file(),
    },
    "status": "READY_FOR_HOST_BUILD" if (root/"gradlew").is_file() and sdk and required_ndk in ndk_versions else "BLOCKED_HOST_ENVIRONMENT",
}
(root/"evidence/host_environment_report.json").write_text(json.dumps(report, indent=2)+"\n")
print(json.dumps(report, indent=2))
