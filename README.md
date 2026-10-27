# Notex

A small Kotlin Android note-taking project for CSE 310 Sprint 3, by Joshua Adams.
This phase replaces the console application with one `:app` module using Jetpack
Compose and Material 3. Launching Notex shows a welcome screen and the original
example note with its title, creation date, and content. The editor and persistent
storage are future work.

## Build and run

1. Open this directory in Android Studio with support for AGP 9.0, or build from
   a terminal using the checked-in Gradle wrapper.
2. Use JDK 17 or newer supported by Gradle. This conversion was prepared with the
   installed Temurin JDK 25.0.4.1; Gradle 9.1.0 explicitly supports JDK 25. Java
   and Kotlin compilation target Java 17 bytecode, not Java 25 bytecode.
3. Install Android SDK Platform 36, SDK Build Tools 36.0.0, and Platform Tools
   using Android Studio's SDK Manager. Configure `ANDROID_HOME` to your SDK
   directory, or set `sdk.dir` in an untracked `local.properties`. Keep SDK paths
   local to your machine.
4. Run `.\gradlew.bat :app:assembleDebug`. Internet access is required for the
   first Gradle and dependency downloads. The debug APK is written to
   `app/build/outputs/apk/debug/app-debug.apk`.
5. Start an Android device/emulator running API 26 or newer, then run
   `.\gradlew.bat :app:installDebug` and open **Notex** from its launcher.

Run repository unit tests with `.\gradlew.bat :app:testDebugUnitTest`.
On macOS/Linux, substitute `./gradlew` for `.\gradlew.bat`.
Notes live in memory, survive activity recreation through a ViewModel, and reset
when the app's process is restarted. There are no create/edit controls in this phase.

## Pinned versions

| Component | Version |
| --- | --- |
| Android Gradle plugin | 9.0.1 |
| Gradle wrapper | 9.1.0, with official distribution SHA-256 |
| Kotlin | 2.2.10, supplied by AGP's built-in Kotlin support |
| Compose compiler plugin | 2.2.10, matching Kotlin |
| Compose BOM | 2025.08.01 (stable Compose UI 1.9.0 / Material 3 1.3.2) |
| Activity Compose | 1.10.1 |
| Lifecycle Compose | 2.9.2 |
| Kotlin coroutines | 1.10.2 |
| compileSdk / targetSdk | 36 / 36 |
| minSdk | 26 (native `java.time.LocalDate`; no core library desugaring needed) |
| Android SDK Build Tools | 36.0.0 |
| Java / Kotlin bytecode target | 17 |

AGP 9 enables Kotlin by default, so this project does **not** apply the standalone
`org.jetbrains.kotlin.android` plugin. Google Maven and Maven Central supply app
dependencies; plugin resolution also includes the Gradle Plugin Portal.
The application ID and namespace are both `com.example.notex`.

Version selection references:

- [AGP 9.0.1 compatibility and built-in Kotlin](https://developer.android.com/build/releases/agp-9-0-0-release-notes)
- [Gradle Java compatibility](https://docs.gradle.org/current/userguide/compatibility.html)
- [Official Gradle checksums](https://gradle.org/release-checksums/)
- [Compose compiler plugin setup](https://developer.android.com/develop/ui/compose/setup-compose-dependencies-and-compiler)
- [Compose BOM version mapping](https://developer.android.com/develop/ui/compose/bom/bom-mapping)
- [Pinned BOM in Google Maven](https://dl.google.com/dl/android/maven2/androidx/compose/compose-bom/2025.08.01/compose-bom-2025.08.01.pom)
- [Activity releases](https://developer.android.com/jetpack/androidx/releases/activity#1.10.1)
- [Lifecycle releases](https://developer.android.com/jetpack/androidx/releases/lifecycle#2.9.2)

## Structure and behavior reference

- `app/src/main/kotlin/com/example/notex/model`: immutable `Note`, with a UUID,
  title, non-null creation date, and content.
- `app/src/main/kotlin/com/example/notex/data`: `NoteRepository` exposes a read-only
  `StateFlow` of note snapshots, creates notes, and updates title/content by ID.
  Updates preserve identity, creation date, and list position; missing IDs return
  `null`. Empty text and duplicate titles remain valid, as in the console version.
  `InMemoryNoteRepository` has no terminal I/O or persistence.
- `app/src/main/kotlin/com/example/notex/ui`: launcher activity, ViewModel,
  lifecycle-aware note observation, Compose screen, and Material 3 light/dark theme.
- `app/src/test`: repository checks for unique IDs, dates, ID-based updates,
  snapshot preservation, missing IDs, and independently editable fields.
- `reference/console/Main.kt`: the original console source preserved unchanged,
  outside the Android source set. It documents the create/select/display/edit
  flow for the next UI phase; it is not another Gradle module.

Original behavior to retain when implementing the editor:

1. Seed an example note at startup.
2. Create a note from a title and content, setting its creation date automatically.
3. List notes in creation order and select one; handle an empty list or invalid
   selection. The Android implementation should select by stable ID.
4. Display the selected note's title, creation date, and content.
5. Edit title and content independently, retaining any field the user does not
   change and preserving the original creation date.
6. Keep changes for the current session only. Android handles exiting the app;
   the console's Exit menu does not need an Android equivalent.

The original Kotlin learning goals were variables, conditionals, loops,
functions, classes, and creating/modifying collections. The console source
retains those examples and its original AI-use attribution.

## Verification on the conversion machine

- Installed JDK: Temurin 25.0.4.1. No configured Android SDK or `adb`/`sdkmanager`
  was found in the checked standard locations or on PATH.
- Gradle 9.1.0 downloaded successfully after retrying outside the network sandbox.
  The regenerated wrapper JAR and distribution match the official checksums.
- Gradle configuration and `:app:dependencies --configuration debugRuntimeClasspath`
  succeeded with no unresolved dependencies.
- All four repository tests passed in a temporary, ignored JVM Gradle harness
  under `.gradle/repository-check`, compiling the actual app model/data and test
  sources with Kotlin 2.2.10, coroutines 1.10.2, and Java 17 bytecode. The harness
  was used because the Android test task also requires an SDK; it is not an app
  module or a substitute for an Android build.
- `.\gradlew.bat :app:assembleDebug` was attempted and stopped with
  **SDK location not found** while determining dependencies for
  `:app:compileDebugJavaWithJavac`. Android compilation, packaging, the Android
  unit-test task, and device/emulator launch remain unverified. No APK was produced.
- Manifest/resource XML parsing, ignore rules, and `git diff --check` passed.
  The console reference matches the original Git blob. The pre-existing time log
  edits and `roadmap/` ignore entry were preserved.

## Next phase

- Implement note creation, selection, and editing in Compose.
- Add persistent storage in a later phase.
