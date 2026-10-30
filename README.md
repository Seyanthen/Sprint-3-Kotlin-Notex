# Notex 1.0

A Kotlin Android note-taking app for **CSE 310 Sprint 3**, by **Joshua Adams**.
Create, view, and edit notes with Jetpack Compose, Material 3, and local Room
storage. The project began as a console app to practice variables, conditionals,
loops, functions, classes, and collections; its core flows now run in Android.

AI assisted with main menu planning, UI programming, README instructions, and the
Android UI and Room persistence phases.

## Instructions for Build and Use

### Android Studio setup

1. Open the repository root in Android Studio supporting **AGP 9.1.1** and sync
   using the checked-in Gradle wrapper. Initial downloads require internet access.
2. Set the Gradle JDK to a supported JDK 17+ in Settings > Build, Execution,
   Deployment > Build Tools > Gradle. Verified with **Temurin 25.0.4.1**.
3. In SDK Manager install **Platform 36**, **Build Tools 36.0.0**, and **Platform
   Tools**. Set `ANDROID_HOME` or use Studio's untracked `local.properties` with
   your own `sdk.dir`.
4. Start an API 26+ emulator or connect a device with USB debugging enabled, select
   the `app` configuration, and Run.

### Windows build, test, and install

Use Android Studio for Android source analysis. IntelliJ IDEA without Android
Support can exclude `app/src` from analysis and report unresolved Gradle DSL
references even when the command-line build passes. Open the root project in
Android Studio and sync Gradle before running IDE commit checks.

From PowerShell at the repository root, with a supported JDK on PATH:

```powershell
.\gradlew.bat :app:assembleDebug
.\gradlew.bat :app:testDebugUnitTest :app:lintDebug
.\gradlew.bat :app:connectedDebugAndroidTest
.\gradlew.bat :app:installDebug
```

Connected tests and installation require a running device. Use a disposable
emulator for tests: the test runner can uninstall the app and remove its data.
APK: **`app/build/outputs/apk/debug/app-debug.apk`**.

Alternatively, with Platform Tools on PATH:

```powershell
adb install -r .\app\build\outputs\apk\debug\app-debug.apk
adb shell am start -n com.example.notex/.ui.MainActivity
```

Upgrades require the same signing key. Uninstalling or clearing app data deletes
notes. On macOS/Linux, replace `.\gradlew.bat` with `./gradlew`.

### Using Notex

1. Tap **Add note**, enter a nonblank title and optional multiline content, then
   **Save** to open the saved note. A fresh installation starts empty.
2. Tap a note card to view its title, creation date, and full content. Tap **Edit**
   to change it; saving preserves its ID, creation date, and list order.
3. **Cancel** or **Back** prompts before discarding changed drafts. Choose **Keep
   editing** to continue. Back from detail returns to Notes.

Saved notes survive force-stop and relaunch. Rotation retains the current screen
and draft; unsaved drafts are not guaranteed to survive force-stop. While saving,
controls are disabled to prevent duplicate submissions. Failed saves retain the
draft for retry; loading failures offer **Retry**.

## Development Environment

| Component | Version |
| --- | --- |
| Android Gradle plugin / Gradle wrapper | 9.1.1 / 9.3.1 |
| Kotlin / Compose compiler | 2.2.10 (AGP built-in Kotlin) |
| KSP / Room | 2.3.11 / 2.8.4 |
| Compose BOM | 2025.08.01 |
| Activity Compose / Lifecycle Compose | 1.10.1 / 2.9.2 |
| Kotlin coroutines | 1.10.2 |
| compileSdk / targetSdk / minSdk | 36 / 36 / 26 |
| SDK Build Tools | 36.0.0 |
| Java / Kotlin bytecode target | 17 |
| Verified JDK | Temurin 25.0.4.1 |

AGP supplies Kotlin; do not add `org.jetbrains.kotlin.android`. Gradle memory
limits are configured in `gradle.properties` for Room compilation and lint.

The tooling update from AGP 9.0.1 / Gradle 9.1.0 uses the compatible
AGP 9.1.1 / Gradle 9.3.1 pair. The Gradle distribution checksum is pinned in the
wrapper properties. After pulling this update, choose **File > Sync Project
with Gradle Files** in Android Studio.

### Code and storage

- `model/`: immutable notes with UUIDs, titles, creation dates, and content.
- `data/`: repository contract, Room entity/DAO, and one application-scoped
  `notex.db`. Suspend writes and Flow queries run on database executors. UUIDs
  remain unique, dates convert explicitly to epoch days, and an insertion position
  keeps ordering deterministic. Updates change only title and content.
- `ui/`: ViewModel, saved editor state, lifecycle-aware observation, and Compose UI.
- `app/schemas/`: exported initial Room schema. Retain it in version control and
  provide tested migrations for future versions; no destructive fallback is used.
- `app/src/test` and `app/src/androidTest`: repository, ViewModel, Room persistence,
  and Compose flow tests. The in-memory repository is a test-only fake.

The original console source is preserved unchanged in
[`reference/console/Main.kt`](reference/console/Main.kt) from commit `4208c31`
for class presentations and comparison with the Android app. It demonstrates the
original menu, note creation, selection, display, and editing, with Joshua Adams's
authorship and AI-use attribution intact. It is outside the Android source set;
its sample note and console entry point do not run in the Android app.

Notes in the Android app are local only, with no account or cloud synchronization.

### Verification

After the AGP 9.1.1 / Gradle 9.3.1 update on October 30, 2026:

- Debug app and instrumentation-test APK builds passed.
- All **13 unit tests** passed; lint passed with **0 errors and 15 warnings**.
- The Room schema remained unchanged. Connected tests and manual device checks
  were not rerun because no emulator or device was running.

Before the tooling update, verified October 30, 2026, on the API 36 Medium_Phone emulator:

- Debug build passed; all **13 unit tests** and **2 connected tests** passed.
- Lint passed with **0 errors and 15 warnings**, including dependency-version
  notices and Android configuration warnings.
- Room tests verified file-backed close/reopen persistence and updates preserving
  ID, date, and order without duplicates. Compose covered create/view/edit,
  validation, Back/discard, and unsaved draft rotation/recreation.
- Manual force-stop/relaunch and unsaved-title rotation checks passed.

## Useful Websites to Learn More

- [W3Schools Kotlin Tutorial](https://www.w3schools.com/kotlin/index.php)
- [Jetpack Compose documentation](https://developer.android.com/develop/ui/compose)
- [Room documentation and releases](https://developer.android.com/jetpack/androidx/releases/room)
- [ViewModel saved-state guidance](https://developer.android.com/topic/libraries/architecture/viewmodel/viewmodel-savedstate)
- [AGP 9.1 compatibility](https://developer.android.com/build/releases/agp-9-1-0-release-notes)
- [KSP 2.3.11 release](https://github.com/google/ksp/releases/tag/2.3.11)
- [Gradle Java compatibility](https://docs.gradle.org/current/userguide/compatibility.html)

## Future Work

- [ ] Add note deletion with confirmation or undo.
- [ ] Add search and sorting to find notes more easily.
- [ ] Add tags or folders to organize notes.
- [ ] Add export/import for backing up notes outside the app.
- [ ] Expand accessibility checks and testing across Android versions and devices.
