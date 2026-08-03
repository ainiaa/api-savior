# Api Savior Contributor Guide

## Project baseline

- This repository builds an IntelliJ IDEA plugin. Keep production bytecode compatible with Java 11 so the plugin can run on the supported older IDE baseline as well as current IDEA releases.
- The current Gradle wrapper is 6.7. Build with JDK 11 unless the wrapper is upgraded as part of a separate, verified change.
- The plugin must not depend on implementation libraries supplied by a particular IDEA version. Every third-party class imported by plugin code must be declared in `build.gradle` and packaged in the plugin ZIP.

## Verification

With JDK 11 selected, run the default checks before handing off a change:

```bash
sh gradlew check
```

`verifyPluginRuntimeLibraries` is part of `check`; it asserts that the plugin distribution includes the third-party runtime libraries used by the plugin.

For IDEA compatibility changes, install the generated ZIP from `build/distributions/` in both the oldest supported IDEA version and the target current IDEA version, then manually exercise the affected actions.

## Change scope

- Keep plugin API usage within the range declared by `src/main/resources/META-INF/plugin.xml`.
- Prefer small, explicit dependency and compatibility changes. Do not rely on transitive or IDE-bundled libraries.
- Add or update an executable verification when changing runtime packaging or behavior.
