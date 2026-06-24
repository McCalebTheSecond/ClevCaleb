# AGENTS.md

## Cursor Cloud specific instructions

### Project overview

**ClevCaleb** is a native **Android calculator app** (an ad-free clone of *ClevCalc*) using a custom
"Hermes Nous Blue Dark" theme. It is a standard **Gradle + Kotlin** Android project:

- `app/` — the Android application module
  - `Calculator.kt` — pure, Android-free arithmetic logic (unit-testable on the JVM)
  - `MainActivity.kt` — the calculator UI wiring (View Binding)
  - `src/test/.../CalculatorTest.kt` — JVM unit tests for the arithmetic logic
- Build config: Android Gradle Plugin 8.5.2, Kotlin 1.9.24, Gradle 8.7, `compileSdk`/`targetSdk` 34, `minSdk` 24.

### Toolchain (pre-installed in the VM snapshot)

- **JDK 21** at `/usr/lib/jvm/java-21-openjdk-amd64` (`JAVA_HOME`).
- **Android SDK** at `~/android-sdk` (`ANDROID_HOME`/`ANDROID_SDK_ROOT`): `platform-tools`, `platforms;android-34`, `build-tools;34.0.0`.
- These env vars are exported in `~/.bashrc`, so **interactive** shells have them automatically.

Non-obvious gotchas:
- The Gradle build locates the SDK via `local.properties` (`sdk.dir=$HOME/android-sdk`). This file is
  git-ignored (machine-specific); the startup update script recreates it if missing. If a build fails with
  "SDK location not found", recreate it: `echo "sdk.dir=$HOME/android-sdk" > local.properties`.
- The update script runs in a **non-interactive** shell that does not source `~/.bashrc`, so when running
  Gradle from such a context, pass `JAVA_HOME` explicitly (e.g. `JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64 ./gradlew ...`).
  Interactive shells already have `JAVA_HOME` set.

### Build / test / lint / run

Run from the repo root (interactive shell has `JAVA_HOME`/`ANDROID_HOME` set):

- Unit tests: `./gradlew test`
- Build debug APK: `./gradlew assembleDebug` → `app/build/outputs/apk/debug/app-debug.apk`
- Lint: `./gradlew lintDebug` (HTML report under `app/build/reports/`)

### Running the app (important limitation)

This is a **GUI Android app**, and the cloud VM has **no `/dev/kvm`**, so a hardware-accelerated emulator
cannot run here. There is no automated GUI run in this environment. To validate behavior headlessly, rely on
the **JVM unit tests** (which exercise the calculator logic) plus `assembleDebug`. To see the UI, install the
APK on a real device/emulator outside this VM (`adb install app/build/outputs/apk/debug/app-debug.apk`).
