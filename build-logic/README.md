# `build-logic`: the project's shared Gradle setup

`build-logic` holds the Gradle configuration that most modules would otherwise copy into their own `build.gradle.kts`. It isn't app code and doesn't end up in the APK; it runs only while Gradle builds the project.

If you've never worked with one of these before, read this file top to bottom once. After that, the "How do I…" section is the part you'll come back to.

## Why it exists

Without it, every module's `build.gradle.kts` repeats the same block: `compileSdk`, `minSdk`, Java version, the Compose compiler, the Compose BOM, Hilt + KSP, and so on. With 20+ modules, a change like bumping `minSdk` means editing 20 files and hoping none is missed.

Instead, that shared setup is written once here, as **convention plugins**. A module applies one plugin and states only what's specific to it:

```kotlin
// feature/acquisition/ui/build.gradle.kts - the whole file
plugins {
    id("composeform.android.feature")
}

android {
    namespace = "com.touhid.composeform.feature.acquisition.ui"
}

dependencies {
    implementation(project(":feature:acquisition:domain"))
    implementation(project(":feature:acquisition:navigation"))
    implementation(libs.androidx.navigation.compose)
    implementation(libs.coil.compose)
}
```

That one `id("composeform.android.feature")` line gives the module all of the following:
- the Android library setup;
- the SDK levels and Java version;
- Compose;
- Hilt;
- the design system and the shared helpers;
- lifecycle and ViewModel support;
- the dependency rules.

## Key ideas, in plain terms

- **A Gradle plugin** is a class that configures a module when the module applies it. `com.android.library` (from Google) is one; it's what makes a module an Android library.
- **A convention plugin** is a plugin we write ourselves, usually by applying other plugins and setting values on them. Ours apply `com.android.library`, the Compose compiler plugin, KSP etc., then fill in our standard settings.
- **An included build** is a separate small Gradle build that the main build compiles first and can use. `build-logic` is one: the root `settings.gradle.kts` says `pluginManagement { includeBuild("build-logic") }`. That line is what lets every module use the plugin ids below.
- **A plugin id** (e.g. `composeform.android.feature`) is just a name. It's mapped to a class in `convention/build.gradle.kts`:
  ```kotlin
  register("androidFeature") {                         // internal name, only used here
      id = "composeform.android.feature"               // what modules write in plugins { }
      implementationClass = "AndroidFeatureConventionPlugin"  // the class that runs
  }
  ```
  The `java-gradle-plugin` support built into `kotlin-dsl` generates the files Gradle needs to find a plugin by its id. If you add a plugin class but forget to `register` it, `id("…")` won't be found.
- **Each plugin class implements `Plugin<Project>`**: Gradle calls its `apply(target: Project)` with the module being configured. Inside, `pluginManager.apply("…")` applies another plugin, `extensions.configure<LibraryExtension> { … }` is the code version of the `android { … }` block, and `dependencies.add("implementation", …)` is the code version of `implementation(…)`.

## Folder layout

```
build-logic/
├── settings.gradle.kts            makes this its own build; reuses ../gradle/libs.versions.toml as `libs`
└── convention/
    ├── build.gradle.kts           compiles the plugins; registers each plugin id -> class
    └── src/main/kotlin/
        ├── ProjectExtensions.kt                     shared helpers + ProjectConfig (SDK levels, Java version)
        ├── AndroidApplicationConventionPlugin.kt    composeform.android.application
        ├── AndroidLibraryConventionPlugin.kt        composeform.android.library
        ├── AndroidLibraryComposeConventionPlugin.kt composeform.android.library.compose
        ├── AndroidFeatureConventionPlugin.kt        composeform.android.feature
        ├── JvmLibraryConventionPlugin.kt            composeform.jvm.library
        ├── HiltConventionPlugin.kt                  composeform.hilt
        ├── RoomConventionPlugin.kt                  composeform.android.room
        └── DependencyRulesPlugin.kt                 composeform.dependency.rules
```

The plugin classes sit in the default package (no `package` line). That's deliberate, because the `implementationClass` values in `convention/build.gradle.kts` then need no package prefix.

## What each class does

| Class (plugin id) | What it sets up | Applied by |
| --- | --- | --- |
| `AndroidApplicationConventionPlugin` (`composeform.android.application`) | `com.android.application`, the Compose compiler, SDK levels, Java version, `buildFeatures.compose`, the Compose dependencies, the dependency rules | `:app` |
| `AndroidLibraryConventionPlugin` (`composeform.android.library`) | `com.android.library`, `compileSdk`/`minSdk`, Java version, the dependency rules. No Compose. | `:core:network`, `:core:navigation`, `:core:database`, `:core:session:data`, every `data` and `navigation` module |
| `AndroidLibraryComposeConventionPlugin` (`composeform.android.library.compose`) | Everything `composeform.android.library` does, plus the Compose compiler, `buildFeatures.compose` and the Compose dependencies | `:core:designsystem`, `:core:common`, `:formbuilder` |
| `AndroidFeatureConventionPlugin` (`composeform.android.feature`) | Everything `composeform.android.library.compose` + `composeform.hilt` do, plus what every screen module uses: `:core:designsystem`, `:core:common`, `material-icons-extended`, lifecycle ViewModel/runtime Compose, `hilt-navigation-compose` | every feature `ui` module |
| `JvmLibraryConventionPlugin` (`composeform.jvm.library`) | `org.jetbrains.kotlin.jvm` (a plain Kotlin module, no Android), Java and Kotlin JVM target, the dependency rules | `:core:model`, every `domain` module, `:core:lead:data` |
| `HiltConventionPlugin` (`composeform.hilt`) | KSP, `hilt-android`, `hilt-compiler` (through KSP). Not the Hilt Gradle plugin - see "Things that will bite you" | `:app`, `:core:network`, every `data` module, every `ui` module (via the feature plugin) |
| `RoomConventionPlugin` (`composeform.android.room`) | KSP, `room-runtime`, `room-ktx`, `room-compiler` (through KSP), schema export to the module's own `schemas/` folder, and `:core:database` (`buildRoomDatabase`, `safeDbCall`) | a `data` module that keeps its own Room database, on top of `composeform.android.library` + `composeform.hilt` (none yet) |
| `DependencyRulesPlugin` (`composeform.dependency.rules`) | Fails the build when a module depends on a module the architecture forbids (e.g. a `ui` module on a `data` module). The rules are listed in "Dependency rules" below. | Never applied directly: the application, Android library and JVM library plugins apply it, so every module gets it |

`ProjectExtensions.kt` isn't a plugin. It holds the helpers the plugins share:

- `ProjectConfig`: `COMPILE_SDK`, `COMPILE_SDK_MINOR`, `MIN_SDK`, `TARGET_SDK` and `JAVA_VERSION` for app code. **This is the one place to change any of them.**
- `Project.libs`: the version catalog (`gradle/libs.versions.toml`), readable from plugin code.
- `Project.library("alias")`: looks up one `[libraries]` entry by its alias. It's the code version of `libs.some.library` in a build file.
- `Project.addComposeDependencies()`: the Compose BOM plus `ui`, `foundation`, `ui-tooling-preview`, and `ui-tooling` for debug builds. Material3 is deliberately not here: only `:core:designsystem` may use it.

### How the plugins stack

Plugins apply each other, so each setting is written in exactly one place:

```
composeform.android.feature
├── composeform.android.library.compose
│   └── composeform.android.library
│       ├── com.android.library
│       └── composeform.dependency.rules
└── composeform.hilt
    └── com.google.devtools.ksp

composeform.android.room ───────── com.google.devtools.ksp (+ Room, :core:database)
composeform.android.application ── com.android.application, Compose compiler, composeform.dependency.rules
composeform.jvm.library ────────── org.jetbrains.kotlin.jvm, composeform.dependency.rules
```

Kotlin itself isn't in the tree for Android modules: AGP 9 compiles Kotlin on its own ("built-in Kotlin"), so there is no `org.jetbrains.kotlin.android` plugin to apply.

## Dependency rules

`DependencyRulesPlugin` checks every `implementation(project(...))`/`api(project(...))` a module declares, the moment Gradle reads the build file. A forbidden one fails the build with a message like:

```
Forbidden dependency :feature:lead:ui -> :feature:lead:data: a ui module never depends on a data module.
```

### How it knows what a module is

The plugin reads only the module's **Gradle path**, so the rules work only if modules follow this naming:

| Path | Meaning |
| --- | --- |
| `:feature:<name>:<layer>` | A layer of a feature, e.g. `:feature:lead:ui` |
| `:core:<topic>:<layer>` | A layer of a core slice: data more than one feature shares, e.g. `:core:lead:domain` |
| `:core:<name>` | Core infrastructure, e.g. `:core:network`, `:core:model` |
| `:app` | The application module |

`<layer>` is one of `domain`, `data`, `ui`, `navigation`. Anything else (e.g. `:formbuilder`) is treated as a plain library: only the general rules apply to it.

### The rules

1. **Nothing depends on `:app`.** `:app` assembles every other module, so a dependency on it would run backwards. A module's dependency on itself (some plugins add one) is ignored.
2. **Only `:app` and feature modules may depend on a feature module.** A core or library module can't reach up into a feature.
3. **A feature depends on another feature only through its `navigation` module.** `:feature:acquisition:ui -> :feature:lead:navigation` is fine (opening a lead screen); `-> :feature:lead:domain` or `-> :feature:lead:data` is not. If two features need the same data, move it into a core slice (`:core:<topic>:domain` + `:core:<topic>:data`) and have both depend on that.
4. **Only `data` modules (and `:app`) depend on `:core:network`.** Screens never touch the HTTP client; they go through a repository.
5. **Only `data` modules depend on `:core:database`, `:app` included.** Same reason as rule 4; `:app` has nothing to configure in a database, so unlike the network it gets no exception.
6. **A `domain` module depends only on `:core:model` and core-slice `domain` modules.** It stays pure Kotlin business logic.
7. **A `navigation` module depends only on `:core:navigation`.** It holds destinations, nothing else, because other features depend on it.
8. **A `ui` module never depends on a `data` module** (its own or a core slice's). It sees only its `domain` (models and repository interfaces); Hilt supplies the implementation.
9. **A `data` module never depends on a `ui` or `navigation` module.**

`:app` is exempt from every rule except 1 and 5: it may depend on any other module, because it wires everything together (Hilt needs every `data` module, manifest merging needs every `ui` module).

### Using it in another project

Copy `DependencyRulesPlugin.kt` and register it like the others. If your module paths differ (for example `:features:...` or `:shared:...`), adjust the `Module` class at the top of the file: it's the only place that turns a path into "feature / layer". Each rule is one `if` in `dependencyRuleViolation`, so removing or adding one is a small, local change.

## Which plugin should my new module use?

| The module is… | Apply |
| --- | --- |
| A feature's screens (`feature/<name>/ui`) | `composeform.android.feature` |
| A feature's or core slice's `domain` | `composeform.jvm.library` |
| A feature's or core slice's `data` | `composeform.android.library` + `composeform.hilt` |
| …that also stores data in Room | additionally `composeform.android.room` |
| A feature's `navigation` | `composeform.android.library` |
| A shared Compose library (like `:core:common`) | `composeform.android.library.compose` |
| A plain Kotlin library with no Android at all | `composeform.jvm.library` |

Then add only that module's own dependencies.

## How do I…

**…bump `compileSdk`, `minSdk`, `targetSdk` or the Java version?** Edit `ProjectConfig` in `ProjectExtensions.kt`. Never set them in a module's build file.

**…add a library every feature screen should get?** Add it to `gradle/libs.versions.toml` under `[libraries]`, then add a `dependencies.add("implementation", library("<alias>"))` line to `AndroidFeatureConventionPlugin`. The alias is the key on the left of `=` in the toml; `-`, `_` and `.` count as the same character.

**…add a library only one module needs?** Put it in that module's own `build.gradle.kts`, not here. A plugin is for what *every* module of a kind needs.

**…add a new kind of module that needs different setup?**
1. Write a new class in `convention/src/main/kotlin/` implementing `Plugin<Project>`. Start from the closest existing plugin, and apply the existing plugins you build on rather than copying their code.
2. Register it in `convention/build.gradle.kts` under `gradlePlugin { plugins { … } }`, with a `composeform.…` id.
3. If it applies a third-party plugin by id that isn't already listed in the root `build.gradle.kts`, add it there with `apply false` (see below).
4. Make sure it applies `composeform.dependency.rules` directly or through one of the base plugins.
5. Sync. If the new id isn't found, check the `register` block.

**…see which rule a "Forbidden dependency" build failure is about?** The message names the two modules (`:feature:lead:ui -> :feature:lead:data`) and the rule. "Dependency rules" below lists every rule and how to restructure instead. Don't relax the plugin to make a build pass; restructure the dependency.

## Things that will bite you

- **The `apply false` lines in the root `build.gradle.kts` are load-bearing.** `convention/build.gradle.kts` depends on AGP and the Kotlin Gradle plugin only as `compileOnly`: enough to compile our plugin classes, not to run them. At runtime the plugins apply AGP, KSP, the Compose compiler and Kotlin JVM *by id*, and those ids are found because the root build file lists them with `apply false`. Remove one of those lines and the matching convention plugin fails with "plugin not found", even though nothing else changed.
- **`composeform.android.room` needs KSP's Gradle plugin as `compileOnly` too** (`libs.ksp.gradlePlugin` in `convention/build.gradle.kts`), because it configures `KspExtension` to pass `room.schemaLocation`. Keep its version on the catalog's `ksp`, the same one the root `apply false` uses.
- **The version catalog alias must match exactly.** `library("google-dagger-hilt-android")` throws `NoSuchElementException: No value present` if `libs.versions.toml` has no `[libraries]` entry with that name. A `[plugins]` entry with the same name doesn't count.
- **Only `:app` applies the Hilt Gradle plugin** (`com.google.dagger.hilt.android`), per Hilt's multi-module setup; `composeform.hilt` gives library modules only Hilt + its compiler. That's enough for `@Module`, `@Inject` and `@HiltViewModel`. An `@AndroidEntryPoint` Activity or Fragment in a library module has to use Hilt's long form, `@AndroidEntryPoint(ComponentActivity::class) class LeadActivity : Hilt_LeadActivity()`. The short form (`: ComponentActivity()`) relies on the plugin's bytecode rewrite, which library modules don't get.
- **`build-logic`'s own `clean` does nothing, on purpose.** Android Studio's "Clean and Assemble Project with Tests" and "Rebuild" also clean this included build in the same run, after its plugin jar was already loaded to configure the main build. Gradle then stops with "Unable to make progress running work". Changes to these sources are still picked up automatically on the next build. If you ever need to force it, delete `build-logic/convention/build` by hand.
- **Two different Java versions, both intentional.** `build-logic` itself compiles for Java 17, because it runs inside Gradle and AGP needs 17. The app's modules compile for `ProjectConfig.JAVA_VERSION` (Java 11), which is what ends up on devices.
- **A module that doesn't apply one of these plugins** (for example a plain `id("com.android.library")`) still builds. But it misses the shared setup and isn't checked by the dependency rules, so always start from a convention plugin.

## Related reading

- `CLAUDE.md` (in this repo): the full module architecture.
- `docs/architecture.docx`: the shareable write-up, including a Build logic section.
- Gradle's guide to convention plugins: https://docs.gradle.org/current/samples/sample_convention_plugins.html
