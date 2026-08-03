# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Commands

- Build everything: `./gradlew build`
- Build/check a single module: `./gradlew :app:assembleDebug`, `./gradlew :designsystem:build`, `./gradlew :formbuilder:build`, `./gradlew :network:build`
- Run unit tests: `./gradlew test` (single module: `./gradlew :formbuilder:testDebugUnitTest`, single test: `./gradlew :formbuilder:testDebugUnitTest --tests "com.touhid.composeform.formbuilder.FormValidatorTest"`)
- Run instrumented tests (needs a device/emulator): `./gradlew :app:connectedDebugAndroidTest`
- Lint: `./gradlew lint` (per-module: `./gradlew :app:lintDebug`)
- Install and launch on a running emulator/device:
  ```
  adb install -r app/build/outputs/apk/debug/app-debug.apk
  adb shell am start -n com.touhid.composeform/.MainActivity
  ```

## Architecture

Four-module Gradle project, no `build-logic`/convention-plugin infrastructure — each module's `build.gradle.kts` is configured directly (this is intentional; see below).

- **`:app`** — the application shell. Contains `MainActivity.kt`, the `flow` package (the demo server-driven form flow, backed by `DemoFormApi`'s in-memory mock — not real network calls), `ComposeFormApplication.kt` (the `@HiltAndroidApp` entry point), and `di/` (the Hilt modules `:app` owns to configure `:network` — base URL, debug-logging flag, token storage — per the deferral pattern described below). It hosts Compose content and is **not permitted to depend on Material3 or Foundation directly** — see "Design system boundary" below — and is **not permitted to depend on Square's networking libraries directly** — see "Network boundary" below.
- **`:designsystem`** — an Android library module (namespace `com.touhid.composeform.designsystem`) owning all Material3-based UI. `:app` depends on it via `implementation(project(":designsystem"))`.
- **`:formbuilder`** — an Android library module (namespace `com.touhid.composeform.formbuilder`) that parses a JSON form schema (`kotlinx.serialization`) and renders it using `:designsystem`'s components. Also subject to the Material3-free boundary — it depends on `:designsystem` only, never Material3 directly.
- **`:network`** — an Android library module (namespace `com.touhid.composeform.network`) owning OkHttp/Retrofit and all API/network-related work. `:app` depends on it via `implementation(project(":network"))`.

### Design system boundary (important, easy to violate accidentally)

`:designsystem` depends on `androidx.compose.material3` and `androidx.compose.foundation` as `implementation` (not `api`). Neither `:app` nor `:formbuilder` declare either dependency themselves. This means Material3/Foundation classes are not on their compile classpaths at all — any `import androidx.compose.material3.*` added there will fail to compile. This is deliberate, compiler-enforced encapsulation: all UI must go through `:designsystem`'s wrapped components (`AppText`, `AppButton`, `AppScaffold`, etc.), never the raw Material3 APIs. (Foundation layout primitives — `Column`/`Row`/`Box`/`Modifier.padding` — are fine for any module to use directly; only Material3 is restricted. This includes `Image` — `androidx.compose.foundation.Image` (and Coil's `AsyncImage`, if/when added) is Foundation, not Material3, so `:app` can call it directly with no `AppImage` wrapper needed; `Icon` is the one that's Material3-gated and needs `AppIcon`.)

When a module needs a new Material3 primitive it doesn't have a wrapper for yet, add the wrapper to `:designsystem` rather than adding a direct dependency elsewhere.

### `:designsystem` internal structure

```
designsystem/src/main/java/com/touhid/composeform/designsystem/
├── theme/                  # Color.kt (incl. semantic Status*/Status*Container tones), Theme.kt (ComposeFormTheme),
│                            # Type.kt, Spacing.kt (AppSpacing)
└── components/
    ├── text/               # AppText + AppTextStyle enum + AppTextOverride (size/weight/color override),
    │                        # AppIconLabelValue (icon + value, or label caption stacked above value;
    │                        # AppIconPosition: Start/End/Top/Bottom decides which side the icon sits on)
    ├── button/             # AppButton, AppOutlinedButton (+ AppButtonTone: Primary/Success/Danger,
    │                        # both with an optional leadingIcon slot), AppStepperButton
    ├── layout/             # AppScaffold (topBar + optional bottomBar slot + content)
    ├── input/              # AppTextField (+ AppTextFieldType: Text/Number/Email/Password), AppSearchField
    │                        # (pill-shaped filled search bar - distinct from AppTextField; leadingIcon
    │                        # defaults to none, trailingIcon defaults to none, both nullable composable
    │                        # slots so either/both can be omitted entirely), AppCheckbox,
    │                        # AppRadioButton, AppSwitch, AppDropdown (+ AppDropdownOption),
    │                        # AppRadioToggleChip, AppRadioCheckCircle
    ├── icon/                # AppIcon (non-interactive), AppIconButton (clickable)
    └── surface/             # AppTopBar (+ AppTopBarAction, AppTopBarScrollBehavior), AppCard, AppDivider,
                             # AppStatusBadge (+ AppStatusTone: Success/Warning/Error/Info/Neutral - always
                             # shows a status dot), AppChip (selectable pill for filter tabs - no dot,
                             # communicates selection rather than state), AppBottomActionBar (flat elevated
                             # bar for a screen's pinned bottom actions)
```

Components are organized by category (not a flat package) — when adding a new component, put it under the matching category subpackage, creating a new one if it doesn't fit an existing one.

Conventions established by existing components:
- Each component wraps a Material3 equivalent with a narrowed, opinionated API (e.g. `AppTextStyle` enum instead of raw `TextStyle` passthrough) — don't leak Material3/Foundation types (like `PaddingValues`) through a component's public signature if avoidable (see `AppScaffold`: it absorbs `innerPadding` internally via a `Box` rather than exposing it). A container component's `content` slot should still be typed with that container's actual scope (`AppCard`'s is `ColumnScope`, `AppBottomActionBar`'s is `RowScope`, `AppScaffold`'s is `BoxScope`) — the scope isn't a Material3/Foundation type worth hiding, and exposing the wrong one (or none) either silently breaks `weight`/`align` modifiers at runtime or blocks them outright.
- Every component that renders text accepts an optional `AppTextOverride` (`fontSize`/`fontWeight`/`color`, all no-op by default) — `AppText`'s `override`, `AppButton`/`AppOutlinedButton`'s `textOverride`, `AppCheckbox`/`AppRadioButton`/`AppSwitch`/`AppDropdown`'s `labelOverride`. This is how callers (like `:formbuilder`) apply per-instance styling without the design system losing its opinionated defaults.
- Spacing between elements inside a component uses `AppSpacing` (`theme/Spacing.kt`) tokens, not hardcoded `dp` values.
- Each category subpackage has its own `*Previews.kt` file (not one global previews file) with a private composable carrying stacked `@Preview(name = "Light", ...)` / `@Preview(name = "Dark", uiMode = Configuration.UI_MODE_NIGHT_YES, ...)` annotations, wrapped in `ComposeFormTheme`.
- Modules outside `:designsystem` (e.g. `:app`) can reference `ImageVector` constants from `androidx.compose.material:material-icons-core` directly (it's a separate artifact with no Material3 dependency) and pass them into a designsystem component (`AppIcon`, `AppIconButton`, `AppTopBarAction`, `AppButton`'s `leadingIcon` slot, etc.) — but any `@Composable` slot lambda a caller *writes* (e.g. `AppIconLabelValue`'s `icon` param) still compiles as part of that caller's own module, so its body must only call designsystem-exposed composables (`AppIcon`, not raw Material3 `Icon`), never Material3 directly.
- Not everything a screen renders belongs in `:designsystem`, even when it's visually involved and looks reusable in principle. The boundary's purpose is specifically wrapping Material3 access `:app`/`:formbuilder` can't otherwise reach — a composition already buildable from exposed primitives (`AppText`, `AppIcon`, Foundation `Row`/`Column`/`Box`/`border`/`background`) has no Material3-wrapping reason to move, and "some other screen might want this shape someday" isn't a reason either - that's the same speculative-reuse trap the rest of this doc argues against for ordinary code. Ask two concrete questions instead of a hypothetical one: (1) does it have an *actual* second caller today, not an imagined future one? (2) does it use a Material3-derived default (`MaterialTheme.colorScheme...`) that its real caller(s) actually rely on, rather than always overriding? `AcquisitionApprovalDetailScreen`'s score-band step indicator and its score circle both answer no to both - one caller, always-explicit colors - so both live in `:app`, built from `AppText`/`AppIcon`/`AppTextOverride` plus plain Foundation shapes.

**Not yet built**: `components/surface/AppDialog`. Follow the same wrapping conventions above when implementing it.

### Network boundary (same pattern as the design system boundary)

`:network` depends on `okio`, `okhttp` (+ `logging-interceptor`), and `retrofit2` (+ `converter-scalars`, `converter-gson`, `adapter-rxjava2`) as `implementation` (not `api`). `:app` does not declare any of these itself, so `okhttp3.*`/`retrofit2.*`/`okio.*` are not on its compile classpath — importing them there fails to compile. All API/network work (Retrofit service interfaces, request/response handling) belongs inside `:network`; `:app` only consumes what `:network` exposes publicly:

- `NetworkClient` (`network/.../NetworkClient.kt`) — `create(service: Class<T>): T`, a thin wrapper around `Retrofit.create` that's the only way to obtain a Retrofit service instance from outside the module. Kept as a generic escape hatch; the concrete API below is the normal path.
- `@BaseUrl` (`network/.../BaseUrl.kt`) — a Hilt qualifier a consuming module's own Hilt module binds to a `String` (e.g. `@Provides @BaseUrl fun provideBaseUrl(): String = "..."`) so `:network` never hardcodes an environment's base URL. Retrofit requires a trailing slash (`https://api.example.com/`, not `.../com`) — `NetworkModule` normalizes a missing one, but supply it correctly regardless. Once product flavors exist, `:app` reads this from a per-flavor `BuildConfig.BASE_URL` — `:network` itself never needs to know about flavors. `:app` currently supplies a dummy URL via `di/AppNetworkModule.kt`.
- `AppApiService` (`network/.../api/AppApiService.kt`) — `internal`, the Retrofit service interface (`login`, `getManagerList`, `getAdminList`, `getAdminDetails`); bound as a Hilt-injectable singleton in `NetworkModule`. Compiler-enforced, not just convention: it cannot be injected from `:app` or any other module — go through `AppRepository` instead, which is what actually enforces the safe-call/error-mapping and auth behavior below.
- `network/.../model/` — the plain (Gson-reflected, no serialization annotations needed) request/response data classes: `LoginRequest`/`LoginResponse`, `ManagerSummary`, `AdminSummary`, `AdminDetails`.
- `NetworkResult`/`NetworkError` (`network/.../NetworkResult.kt`, `NetworkError.kt`) and `safeApiCall` (`network/.../SafeApiCall.kt`) — the safe-call layer. `safeApiCall { ... }` runs a suspending Retrofit call on `Dispatchers.IO` and maps `HttpException`/`SocketTimeoutException`/`IOException`/anything else into `NetworkResult.Success`/`NetworkResult.Failure(NetworkError.Http|Timeout|NoConnection|Unexpected)` — `retrofit2`/`okhttp3`/`java.io` exception types never cross the module boundary, same spirit as not leaking Material3 types from `:designsystem`. `CancellationException` is rethrown, not wrapped.
- `network/.../auth/` — `TokenProvider` (the contract; storage is deferred to the consuming module, same pattern as `@BaseUrl`) and `AuthInterceptor` (`internal`, attaches `Authorization: Bearer <token>` when a token is present). `:app` binds `TokenProvider` to `di/InMemoryTokenProvider.kt` (in-memory only — no persistence library exists yet) via `di/AppTokenModule.kt`.
- `network/.../repository/AppRepository.kt` — **the actual thing consumers should inject.** Wraps each `AppApiService` call in `safeApiCall`, and on a successful `login` stores the returned token via `TokenProvider` automatically, so callers never manage the token by hand — subsequent `getManagerList`/`getAdminList`/`getAdminDetails` calls are authenticated for free.
- `NetworkModule` (`network/.../NetworkModule.kt`) — `internal`, wires the `OkHttpClient` (30s connect/read/write timeouts, `AuthInterceptor` + debug-gated `HttpLoggingInterceptor` — `Level.BODY` only when `:network`'s own generated `BuildConfig.DEBUG` is true, `Level.NONE` otherwise so release builds never log request/response bodies), `Retrofit`, and `AppApiService` singletons; not visible outside the module. `:network` enables `buildFeatures.buildConfig` itself for this — deliberately not a Hilt qualifier like `@BaseUrl`, since Gradle's variant-aware dependency resolution already gives every module the `BuildConfig.DEBUG` matching whichever build type of `:app` is being built, with no value needing to cross the module boundary.

`:network` depends on `hilt-android`/`ksp(hilt-compiler)`/`kotlinx-coroutines-core` for its own `@Module`s and the safe-call layer, but does **not** apply the Hilt Gradle plugin (`com.google.dagger.hilt.android`) — per Hilt's multi-module guidance, only `:app` (which has `ComposeFormApplication`, the `@HiltAndroidApp` entry point) applies that plugin; library modules just contribute `@Module`s via the compiler dependency. Note no Activity/ViewModel is `@AndroidEntryPoint`-annotated or injects `AppRepository` yet, so while the DI graph now compiles under a real entry point, it isn't exercised by any screen at runtime until one is wired up.

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
- `radio`/`checkboxGroup`/`dropdown` share one `FormOption` model (`id`, `value`, `default`, `style`, `margin`, `padding`) — selection is tracked/returned by `id`, displayed by `value`. Each option's `margin` (zero by default, set via JSON) is what actually creates the gap between adjacent options in a group — `OptionsContainer` itself applies no arrangement spacing, so the per-option `margin` is the only knob and works symmetrically in both `orientation`s.
- `label` is optional on every field (defaults to `""`) — a field can be present purely for its side effects (e.g. a `radio`/`checkboxGroup` whose heading is supplied by a separate preceding `text` field instead of the group's own label).
- The submit button is gated: `FormRenderer` computes `validate(schema, values)` live on every recomposition and disables the button until it's empty. Per-field error text only renders once that field has been touched (tracked in `FormState`), so the button being disabled from the first frame doesn't create a dead end.
- `FormValue.Options.selected` is `List<FormValue.Option>` (just `id`/`value`), **not** `List<FormOption>` — schema metadata (`margin`/`padding`/`style`/`default`) must never leak into the submitted data. If you add a new option-bearing field type, map down to `FormValue.Option`/`FormValue.Options` the same way `checkboxGroup` does in `FormRenderer.kt` and `FormState.kt`, rather than passing the schema `FormOption` straight through.
- `visibleWhen: FormVisibilityCondition?` (`key`/`operator`/`values`) gates a field's visibility on another field's current value. `operator` is only `Equals`/`NotEquals` (no `In`) — both consult the *entire* `values` list the same way regardless of how many entries it has (`Equals`: satisfied on any overlap with the trigger's value(s); `NotEquals`: satisfied on no overlap), so there's one consistent shape rather than one that silently behaves differently depending on `values.size` (see `FormFieldVisibility.kt`). If a new operator is ever needed, give it its own full-list semantics too — never one that only inspects `values.firstOrNull()`, since a JSON author can't see that inconsistency from the schema shape alone.
