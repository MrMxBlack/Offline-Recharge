# V5.5 Change Log

## Research/runtime hardening

- Native model cleanup now runs in `finally`, including generation failures and coroutine cancellation paths.
- ViewModel converts unexpected engine exceptions into an explicit local error state instead of leaving the UI in a running state.
- Runtime status wording no longer implies that a static manifest check is the same as physical zero-network verification.
- App status chip now says `NO NETWORK PERMISSION`; physical packet-level offline proof remains a separate gate.
- Release version advanced to `1.5-v5.5-release-candidate`.
- `RELEASE_STATUS.json` corrected to V5.5 and explicitly keeps bounty readiness false.

## Truth boundary

This release still does **not** claim:

- native llama.cpp inference verified on a physical Android device;
- GrapheneOS verification;
- zero runtime network packets;
- real benchmark measurements;
- bounty readiness.
