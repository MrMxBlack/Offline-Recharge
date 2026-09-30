# V5.18 Changelog

- Added `tools/run_device_validation.sh` as a reproducible physical-device evidence collection entry point.
- Added `docs/DEVICE_VALIDATION_RUNNER.md` documenting the evidence/proof boundary.
- Hardened reproducibility audit so app-version expectations derive from release status instead of a permanently stale version literal.
- Kept bounty readiness fail-closed: no native inference, packet proof, physical-device verification, or benchmark result is claimed by static repository state.
