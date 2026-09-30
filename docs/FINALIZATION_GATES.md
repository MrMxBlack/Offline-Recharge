# V5 Finalization Gates

The project is not bounty-ready until every applicable gate is evidenced.

| Gate | Required evidence | Current V5 state |
|---|---|---|
| Static offline audit | `tools/verify_offline_policy.py` output | automated |
| Android release build | reproducible Gradle release build | pending environment/device |
| Native llama.cpp source | exact upstream commit + SHA-256 manifest | pending import |
| Native GGUF inference | real model loads and generates on device | pending physical device |
| Retrieval | local corpus search works in airplane mode | code path present |
| Citation integrity | generated citation IDs are validated against retrieved evidence | automated in app |
| Research modes | synthesis/comparison/multi-hop/timeline | code path present |
| Contradiction handling | conflicting evidence is surfaced | code path present; heuristic |
| Insufficient evidence | no-evidence case is explicit | code path present |
| RAM | measured physical-device PSS | pending |
| Latency | TTFT/tokens/sec/total latency | pending native device |
| Storage | measured managed + model + corpus size | pending final corpus |
| Offline proof | airplane mode + packet capture/ADB evidence | pending |
| Reproducibility | clean checkout build instructions | documented |
| Public proof | demo + logs + checksums | pending |

A passing static audit never counts as a passing device gate.
