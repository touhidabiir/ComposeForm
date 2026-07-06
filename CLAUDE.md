# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Commands

- Build everything: `./gradlew build`
- Build/check a single module: `./gradlew :app:assembleDebug`, `./gradlew :designsystem:build`
- Run unit tests: `./gradlew test` (single test: `./gradlew :app:testDebugUnitTest --tests "com.example.composeform.ExampleUnitTest"`)
- Run instrumented tests (needs a device/emulator): `./gradlew :app:connectedDebugAndroidTest`
- Lint: `./gradlew lint` (per-module: `./gradlew :app:lintDebug`)
- Install and launch on a running emulator/device:
  ```
  adb install -r app/build/outputs/apk/debug/app-debug.apk
  adb shell am start -n com.example.composeform/.MainActivity
  ```

## Architecture

Two-module Gradle project, no `build-logic`/convention-plugin infrastructure — each module's `build.gradle.kts` is configured directly (this is intentional; see below).

- **`:app`** — the application shell. Contains only `MainActivity.kt`. It hosts Compose content and is **not permitted to depend on Material3 or Foundation directly** — see "Design system boundary" below.
- **`:designsystem`** — an Android library module (namespace `com.example.composeform.designsystem`) owning all Material3-based UI. `:app` depends on it via `implementation(project(":designsystem"))`.

### Design system boundary (important, easy to violate accidentally)

`:designsystem` depends on `androidx.compose.material3` and `androidx.compose.foundation` as `implementation` (not `api`). `:app` does **not** declare either dependency itself. This means Material3/Foundation classes are not on `:app`'s compile classpath at all — any `import androidx.compose.material3.*` or `androidx.compose.foundation.*` added to `app/src` will fail to compile. This is deliberate, compiler-enforced encapsulation: all UI in `:app` must go through `:designsystem`'s wrapped components (`AppText`, `AppButton`, `AppScaffold`, etc.), never the raw Material3/Foundation APIs.

When `:app` needs a new Material3 primitive it doesn't have a wrapper for yet, add the wrapper to `:designsystem` rather than adding a direct dependency to `:app`.

### `:designsystem` internal structure

```
designsystem/src/main/java/com/example/composeform/designsystem/
├── theme/                  # Color.kt, Theme.kt (ComposeFormTheme), Type.kt, Spacing.kt (AppSpacing)
└── components/
    ├── text/               # AppText + AppTextStyle enum
    ├── button/             # AppButton, AppOutlinedButton
    ├── layout/              # AppScaffold
    └── input/              # AppTextField, AppCheckbox, AppRadioButton, AppSwitch, AppDropdown
```

Components are organized by category (not a flat package) — when adding a new component, put it under the matching category subpackage, creating a new one if it doesn't fit `text`/`button`/`layout`/`input`.

Conventions established by existing components:
- Each component wraps a Material3 equivalent with a narrowed, opinionated API (e.g. `AppTextStyle` enum instead of raw `TextStyle` passthrough) — don't leak Material3/Foundation types (like `PaddingValues`) through a component's public signature if avoidable (see `AppScaffold` for the pattern: it absorbs `innerPadding` internally via a `Box` rather than exposing it).
- Spacing between elements inside a component uses `AppSpacing` (`theme/Spacing.kt`) tokens, not hardcoded `dp` values.
- Each category subpackage has its own `*Previews.kt` file (not one global previews file) with a private composable carrying stacked `@Preview(name = "Light", ...)` / `@Preview(name = "Dark", uiMode = Configuration.UI_MODE_NIGHT_YES, ...)` annotations, wrapped in `ComposeFormTheme`.

**Not yet built** (planned next phase): `components/surface/` — `AppCard`, `AppDialog`, `AppChip`, `AppDivider`, `AppTopBar`. Follow the same wrapping conventions above when implementing these.
