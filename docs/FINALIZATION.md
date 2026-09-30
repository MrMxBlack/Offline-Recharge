# Finalization Gate

A release may be called **verified bounty submission** only when every gate below has an evidence artifact.

- [ ] `tools/pin_llama_cpp.sh` checked out an exact 40-character upstream commit.
- [ ] `tools/integrate_llama_android.sh` completed without errors.
- [ ] Gradle release build succeeds on a clean machine.
- [ ] GGUF loads and generates tokens locally.
- [ ] No INTERNET permission and no network-dependent runtime path.
- [ ] Airplane Mode test completed with Wi-Fi/mobile data disabled.
- [ ] At least one GrapheneOS-compatible 8–12 GB RAM device tested.
- [ ] Peak PSS measured and recorded.
- [ ] Model + corpus + index + app storage measured and <= 50 GB.
- [ ] Hard benchmark set executed and raw results committed.
- [ ] Corpus licenses, versions, checksums and build scripts committed.
- [ ] Public GitHub clean-clone reproduction completed.
- [ ] Demo video/screenshots show the app operating offline.

Do not replace a checkbox with an assertion. Commit the artifact.
