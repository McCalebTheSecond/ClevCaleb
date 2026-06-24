# AGENTS.md

## Cursor Cloud specific instructions

### Current repository state (read this first)

As of this writing, the repository contains **only `README.md`** — there is **no application code, no build system, and no dependency manifests** (no `build.gradle`/`gradlew`, `package.json`, `pyproject.toml`, etc.).

Per `README.md`, the *intended* product is **ClevCaleb**: an ad-free clone of the Android app **ClevCalc** (a multi-function calculator), themed "Hermes Nous Blue Dark". This has not been scaffolded yet.

Implications for cloud agents:

- There is **nothing to install, lint, build, or run** until a project is scaffolded. Do not expect dependency installation, dev servers, or tests to exist.
- Do **not** invent or scaffold an entire app unless explicitly asked — that is product work, not environment setup.

### Pre-installed VM tooling (for reference)

The base VM already provides: **Node 22**, **Java/OpenJDK 21**, **Python 3.12**. Not present: **Gradle**, **Android SDK** (`ANDROID_HOME` unset), **Flutter**.

If the project is scaffolded as a native **Android** app, note that Android SDK + Gradle are *system-level* dependencies that are not pre-installed, and a running Android emulator is generally not available in this headless cloud VM. Plan testing accordingly (e.g. unit tests via Gradle, or Robolectric, rather than an on-device/emulator GUI run).

### When app code is added

Once a build system exists, update the cloud update script (via the environment setup flow) to install dependencies for the chosen stack, and add the standard lint/test/build/run commands here, referencing the project's own scripts rather than duplicating them.
