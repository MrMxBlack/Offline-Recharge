# V5.16 Changelog

- Fixed the AGP version parser in `verify_build_reproducibility.py` so it correctly reads `id("com.android.application") version "8.9.1"`.
- Synchronized app version metadata to `1.14-v5.16-wrapper-bootstrap`.
- Added `tools/bootstrap_gradle_wrapper.sh`, which requires a real Gradle 8.11.1 installation and uses the official Wrapper task.
- Added `docs/GRADLE_WRAPPER_BOOTSTRAP.md`.
- Kept Wrapper generation fail-closed: no fabricated JAR, no placeholder, no false reproducibility claim.
