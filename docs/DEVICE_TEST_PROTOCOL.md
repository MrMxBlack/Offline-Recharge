# Physical Device Test Protocol

1. Install the release APK from a clean build.
2. Record device model, Android version, RAM, CPU ABI and free storage.
3. Import the exact documented GGUF model.
4. Confirm the app has no network permission.
5. Enable Airplane Mode before opening the research screen.
6. Run at least 10 benchmark questions, including comparison and multi-hop cases.
7. Record model load time, time-to-first-token, tokens/sec, peak PSS and total managed storage.
8. Repeat one query after a cold restart.
9. Capture evidence of offline operation and the benchmark output.
10. For GrapheneOS, repeat the same protocol on the compatible device.

Do not substitute Android's "extended RAM"/swap for physical RAM when reporting the 12 GB target.
