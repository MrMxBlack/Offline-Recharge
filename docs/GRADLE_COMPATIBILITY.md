# Gradle / AGP Compatibility

Project AGP: **8.9.1**

Required Gradle contract: **8.11.1** (or a later compatible 8.x release). V5.16 pins the release handoff to Gradle 8.11.1 to remove ambiguity.

Before reproducible build validation, the repository must contain:
- `gradlew`
- `gradlew.bat`
- `gradle/wrapper/gradle-wrapper.jar`
- `gradle/wrapper/gradle-wrapper.properties`

The properties file must contain an exact Gradle distribution URL and `distributionSha256Sum`.

Do not hand-create or substitute the wrapper JAR. Generate it using Gradle's `:wrapper` task on a trusted build machine, then commit the generated files.
