# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Commands

- Build everything: `./gradlew build`
- Build/check a single module: `./gradlew :app:assembleDebug`, `./gradlew :designsystem:build`, `./gradlew :formbuilder:build`
- Run unit tests: `./gradlew test` (single module: `./gradlew :formbuilder:testDebugUnitTest`, single test: `./gradlew :formbuilder:testDebugUnitTest --tests "com.touhid.composeform.formbuilder.FormValidatorTest"`)
- Run instrumented tests (needs a device/emulator): `./gradlew :app:connectedDebugAndroidTest`
- Lint: `./gradlew lint` (per-module: `./gradlew :app:lintDebug`)
- Install and launch on a running emulator/device:
  ```
  adb install -r app/build/outputs/apk/debug/app-debug.apk
  adb shell am start -n com.touhid.composeform/.MainActivity
  ```

## Architecture

Three-module Gradle project, no `build-logic`/convention-plugin infrastructure — each module's `build.gradle.kts` is configured directly (this is intentional; see below).

- **`:app`** — the application shell. Contains only `MainActivity.kt`. It hosts Compose content and is **not permitted to depend on Material3 or Foundation directly** — see "Design system boundary" below.
- **`:designsystem`** — an Android library module (namespace `com.touhid.composeform.designsystem`) owning all Material3-based UI. `:app` depends on it via `implementation(project(":designsystem"))`.
- **`:formbuilder`** — an Android library module (namespace `com.touhid.composeform.formbuilder`) that parses a JSON form schema (`kotlinx.serialization`) and renders it using `:designsystem`'s components. Also subject to the Material3-free boundary — it depends on `:designsystem` only, never Material3 directly.

### Design system boundary (important, easy to violate accidentally)

`:designsystem` depends on `androidx.compose.material3` and `androidx.compose.foundation` as `implementation` (not `api`). Neither `:app` nor `:formbuilder` declare either dependency themselves. This means Material3/Foundation classes are not on their compile classpaths at all — any `import androidx.compose.material3.*` added there will fail to compile. This is deliberate, compiler-enforced encapsulation: all UI must go through `:designsystem`'s wrapped components (`AppText`, `AppButton`, `AppScaffold`, etc.), never the raw Material3 APIs. (Foundation layout primitives — `Column`/`Row`/`Box`/`Modifier.padding` — are fine for any module to use directly; only Material3 is restricted.)

When a module needs a new Material3 primitive it doesn't have a wrapper for yet, add the wrapper to `:designsystem` rather than adding a direct dependency elsewhere.

### `:designsystem` internal structure

```
designsystem/src/main/java/com/touhid/composeform/designsystem/
├── theme/                  # Color.kt, Theme.kt (ComposeFormTheme), Type.kt, Spacing.kt (AppSpacing)
└── components/
    ├── text/               # AppText + AppTextStyle enum + AppTextOverride (size/weight/color override)
    ├── button/             # AppButton, AppOutlinedButton
    ├── layout/             # AppScaffold
    └── input/              # AppTextField (+ AppTextFieldType: Text/Number/Email/Password), AppCheckbox,
                             # AppRadioButton, AppSwitch, AppDropdown (+ AppDropdownOption)
```

Components are organized by category (not a flat package) — when adding a new component, put it under the matching category subpackage, creating a new one if it doesn't fit `text`/`button`/`layout`/`input`.

Conventions established by existing components:
- Each component wraps a Material3 equivalent with a narrowed, opinionated API (e.g. `AppTextStyle` enum instead of raw `TextStyle` passthrough) — don't leak Material3/Foundation types (like `PaddingValues`) through a component's public signature if avoidable (see `AppScaffold`: it absorbs `innerPadding` internally via a `Box` rather than exposing it).
- Every component that renders text accepts an optional `AppTextOverride` (`fontSize`/`fontWeight`/`color`, all no-op by default) — `AppText`'s `override`, `AppButton`/`AppOutlinedButton`'s `textOverride`, `AppCheckbox`/`AppRadioButton`/`AppSwitch`/`AppDropdown`'s `labelOverride`. This is how callers (like `:formbuilder`) apply per-instance styling without the design system losing its opinionated defaults.
- Spacing between elements inside a component uses `AppSpacing` (`theme/Spacing.kt`) tokens, not hardcoded `dp` values.
- Each category subpackage has its own `*Previews.kt` file (not one global previews file) with a private composable carrying stacked `@Preview(name = "Light", ...)` / `@Preview(name = "Dark", uiMode = Configuration.UI_MODE_NIGHT_YES, ...)` annotations, wrapped in `ComposeFormTheme`.

**Not yet built** (planned next phase): `components/surface/` — `AppCard`, `AppDialog`, `AppChip`, `AppDivider`, `AppTopBar`. Follow the same wrapping conventions above when implementing these.

### `:formbuilder` — JSON-driven dynamic forms

```
formbuilder/src/main/java/com/touhid/composeform/formbuilder/
├── schema/                  # FormSchema, FormField (sealed interface + 8 types), FormOption, FormInsets,
│                            # FormTextStyle, FormSize, FormOrientation, FormValue — the JSON-facing data model
├── FormSchemaParser.kt      # parseFormSchema(jsonString): FormSchema
├── FormValidator.kt         # internal validate(schema, values): Map<String, String> (field key -> error)
├── FormFieldMappers.kt      # FormTextStyle -> AppTextOverride, inputType string -> AppTextFieldType,
│                            # FormSize -> Modifier (fillMaxWidth/Height, fixed dp, or no-op for wrap_content)
├── FormState.kt             # internal FormState (values + touched), rememberSaveable via a kotlinx.serialization Saver
├── FormRenderer.kt          # public FormRenderer(schema, modifier, onSubmit) entry point
└── FormRendererPreviews.kt
```

Schema model: `FormSchema` is just `{ fields: [...] }` — **everything is a field**, including the form's title (`type: "text"`, non-interactive display) and the submit button (`type: "submit"`), positioned wherever they appear in the list. There's no separate top-level "title"/"submit" property. The 8 `type` discriminator values are `text`, `inputBox`, `checkbox`, `checkboxGroup`, `radio`, `switch`, `dropdown`, `submit`, dispatched by a `JsonContentPolymorphicSerializer` in `FormField.kt` (decode-only; `FormValue`, the submitted-data type, is separately `@Serializable` for `FormState`'s Saver round-trip, not for parsing).

Key conventions if extending the schema:
- Every field has both `margin` (space outside its bounds, separating it from neighbors) and `padding` (space inside its bounds, around its content) — distinct concepts, both `FormInsets` (`top`/`bottom`/`left`/`right` dp), both present on every field type for uniformity.
- Every field has a `size: FormSize` (`width`/`height`, each `"match_parent"` | `"wrap_content"` | a numeric dp string), defaulting to `match_parent` width / `wrap_content` height. Applied directly to the field's own rendered component (not the margin/padding wrapper `Box`s) via `FormSize.toModifier()`, since components like `AppButton`/`AppCheckbox` don't stretch on their own the way `AppTextField` does.
- Every field (and every `FormOption`) has the same `style: FormTextStyle?` (`size`/`color`/`weight`) — one property name/shape everywhere text appears, never a context-specific key like `labelStyle`/`titleStyle`.
- `radio`/`checkboxGroup`/`dropdown` share one `FormOption` model (`id`, `value`, `default`, `style`, `margin`, `padding`) — selection is tracked/returned by `id`, displayed by `value`. Each option's `margin` (default `4dp` all sides) is what actually creates the gap between adjacent options in a group — `OptionsContainer` itself applies no arrangement spacing, so the per-option `margin` is the only knob and works symmetrically in both `orientation`s.
- `label` is optional on every field (defaults to `""`) — a field can be present purely for its side effects (e.g. a `radio`/`checkboxGroup` whose heading is supplied by a separate preceding `text` field instead of the group's own label).
- The submit button is gated: `FormRenderer` computes `validate(schema, values)` live on every recomposition and disables the button until it's empty. Per-field error text only renders once that field has been touched (tracked in `FormState`), so the button being disabled from the first frame doesn't create a dead end.
- `FormValue.Options.selected` is `List<FormValue.Option>` (just `id`/`value`), **not** `List<FormOption>` — schema metadata (`margin`/`padding`/`style`/`default`) must never leak into the submitted data. If you add a new option-bearing field type, map down to `FormValue.Option`/`FormValue.Options` the same way `checkboxGroup` does in `FormRenderer.kt` and `FormState.kt`, rather than passing the schema `FormOption` straight through.
