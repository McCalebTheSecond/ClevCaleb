# Agent instructions — ClevCaleb

Guidance for cloud agents and automated workflows working in this repo.

## Cursor Cloud development environment

Standard **Gradle + Kotlin** Android project (`app/` module, AGP 8.5+, Kotlin 1.9+, `compileSdk`/`targetSdk` 34, `minSdk` 24).

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

### Build / test / lint

Run from the repo root (interactive shell has `JAVA_HOME`/`ANDROID_HOME` set):

- Unit tests: `./gradlew :app:testDebugUnitTest`
- Build debug APK: `./gradlew assembleDebug` → `app/build/outputs/apk/debug/app-debug.apk`
- Lint: `./gradlew lintDebug` (HTML report under `app/build/reports/`)

### Running the app (important limitation)

This is a **GUI Android app**, and the cloud VM has **no `/dev/kvm`**, so a hardware-accelerated emulator
cannot run here. There is no automated GUI run in this environment. To validate behavior headlessly, rely on
the **JVM unit tests** plus `assembleDebug`. To see the UI, install the APK on a real device/emulator outside
this VM (`adb install app/build/outputs/apk/debug/app-debug.apk`).

## GitHub releases (required)

When the user asks for a **release** or **APK**, they mean a **GitHub Release** in the repo’s **Releases** section — not just a commit, branch merge, or debug build artifact.

Follow this checklist every time:

1. Bump `versionCode` and `versionName` in `app/build.gradle.kts`
2. Run tests (`./gradlew :app:testDebugUnitTest`)
3. **Restore the release keystore** (see below)
4. Build a **signed release** APK (`./gradlew assembleRelease`)
5. **Verify the APK is signed** (see below) — do not skip this step
6. Create tag `vX.Y.Z` on `main`
7. Create GitHub release titled `ClevCaleb vX.Y.Z`
8. Upload asset named `ClevCaleb-vX.Y.Z.apk`
9. Confirm the release appears at https://github.com/McCalebTheSecond/ClevCaleb/releases

### Never ship these as a release

- `app/build/outputs/apk/debug/app-debug.apk` — debug build, not for releases
- `app/build/outputs/apk/release/app-release-unsigned.apk` — **unsigned**; Android will reject it with “App not installed” / “package appears to be invalid”
- Any APK that fails `apksigner verify`

The correct release artifact is **`app/build/outputs/apk/release/app-release.apk`** (signed). Rename it to `ClevCaleb-vX.Y.Z.apk` before upload.

## Release signing keystore

Release signing is configured in `app/build.gradle.kts`. Signing only happens when this file exists:

```
keystore/clevcaleb-release.jks
```

`keystore/` is gitignored. **Do not commit the keystore.**

### Restore keystore before building (cloud agents)

GitHub repo secrets (if configured):

| Secret | Purpose |
|--------|---------|
| `RELEASE_KEYSTORE_BASE64` | Base64-encoded `clevcaleb-release.jks` |
| `RELEASE_STORE_PASSWORD` | Keystore password (default: `clevcaleb`) |
| `RELEASE_KEY_PASSWORD` | Key password (default: `clevcaleb`) |

Restore locally before `assembleRelease`:

```bash
mkdir -p keystore
gh secret get RELEASE_KEYSTORE_BASE64 | base64 -d > keystore/clevcaleb-release.jks
export RELEASE_STORE_PASSWORD="${RELEASE_STORE_PASSWORD:-clevcaleb}"
export RELEASE_KEY_PASSWORD="${RELEASE_KEY_PASSWORD:-clevcaleb}"
```

If secrets are missing and no local keystore exists, **stop and tell the user** — do not publish an unsigned APK or generate a new keystore without warning. A new keystore breaks in-place upgrades from previously installed releases.

### Verify signing before upload (mandatory)

```bash
APKSIGNER="$(find "$ANDROID_HOME/build-tools" -name apksigner | sort -V | tail -1)"
"$APKSIGNER" verify --print-certs app/build/outputs/apk/release/app-release.apk
```

- Command must exit **0**
- Output must include `Signer #1 certificate DN: CN=ClevCaleb, ...`
- If you see `DOES NOT VERIFY` or `Missing META-INF/MANIFEST.MF`, **do not upload**

Optional sanity check — release APK should be ~1.4–1.5 MB (minified). Debug APKs are ~17 MB.

## Creating / updating a GitHub release

```bash
# After signed build and verification
cp app/build/outputs/apk/release/app-release.apk /tmp/ClevCaleb-vX.Y.Z.apk

git tag -a vX.Y.Z -m "ClevCaleb vX.Y.Z"
git push origin vX.Y.Z

gh release create vX.Y.Z \
  --title "ClevCaleb vX.Y.Z" \
  --notes-file /path/to/release-notes.md \
  /tmp/ClevCaleb-vX.Y.Z.apk
```

To replace a bad asset on an existing release:

```bash
gh release upload vX.Y.Z /tmp/ClevCaleb-vX.Y.Z.apk --clobber
```

Release notes should match prior releases: summary, bullet changes, install line (“Download **ClevCaleb-vX.Y.Z.apk** below and sideload on Android 8+.”).

## Versioning

- `versionName`: user-facing semver (e.g. `1.0.4`)
- `versionCode`: monotonic integer; increment on every release
- Tag format: `v` + `versionName` (e.g. `v1.0.4`)

## Tests

```bash
export ANDROID_HOME=~/android-sdk
./gradlew :app:testDebugUnitTest
```

Fix failing tests before releasing.

## Common mistake (do not repeat)

**v1.0.4 was initially broken** because the agent:

1. Built `assembleRelease` without restoring `keystore/clevcaleb-release.jks`
2. Uploaded `app-release-unsigned.apk` to GitHub Releases
3. Did not run `apksigner verify` before upload

Result: users saw “App not installed” / invalid package. Always restore keystore → build → verify → upload.

## Signing key continuity

All Play/sideload updates require the **same** signing key. If the keystore is lost and a new one is generated, users must **uninstall** the old app before installing the new build. Mention this in release notes when the signing key changes.

Back up `keystore/clevcaleb-release.jks` locally (outside the repo) and keep `RELEASE_KEYSTORE_BASE64` updated in GitHub secrets.
