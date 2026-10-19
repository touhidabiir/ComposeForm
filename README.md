# ComposeForm

ComposeForm renders a fully interactive form from a JSON schema — no per-screen Compose UI code needed. You write (or fetch from an API) a JSON document describing fields, options, styling, layout, and conditional visibility; `parseFormSchema` turns it into a `FormSchema`, and `FormRenderer` draws it.

## Quick start

```kotlin
val schema = parseFormSchema(jsonString)

FormRenderer(
    schema = schema,
    onSubmit = { values -> /* Map<String, FormValue> of submitted data */ },
)
```

`FormValue` is `Text(value)` / `Option(id, value)` / `Options(selected: List<Option>)` — to get the plain answered value per field instead of pattern-matching yourself, call `values.toPlainValues()` for a flattened `Map<String, String>` (or `formValue.toPlainString()` for a single one). See [`formbuilder/README.md`](formbuilder/README.md) for the full JSON schema reference.

## Modules

The main Gradle modules:

| Module | Description | Docs |
|---|---|---|
| `:app` | The application shell — hosts Compose screens, the `@HiltAndroidApp` entry point, and the demo server-driven form flow (`DemoFormApi`) that exercises `:formbuilder`/`:core:network`. Not permitted to depend on Material3/Foundation or Square's networking libraries directly. | [`CLAUDE.md`](CLAUDE.md) |
| `:core:designsystem` | Owns all Material3-based UI. The only module allowed to depend on Material3/Foundation directly — everything else goes through its wrapped components (`AppText`, `AppButton`, `AppScaffold`, etc.). | [`core/designsystem/README.md`](core/designsystem/README.md) |
| `:formbuilder` | Parses a JSON form schema (`kotlinx.serialization`) and renders it using `:core:designsystem`'s components. | [`formbuilder/README.md`](formbuilder/README.md) |
| `:core:network` | Owns OkHttp/Retrofit and all raw API/network work — Retrofit service interfaces, DTOs, interceptors, the safe-call layer. The only module allowed to depend on Square's networking libraries directly. | [`core/network/README.md`](core/network/README.md) |
| `:core:model` | Plain Kotlin module with `AppResult`/`AppError`, the result type feature repositories return. | [`CLAUDE.md`](CLAUDE.md) |
| `:feature:lead:{domain,data,ui,navigation}` | The lead dashboard as a feature slice: domain models + `LeadRepository` interface, its own API/DTOs/repository impl, the screens with their host Activity, and the destinations other modules open it by. Shared lead types live in `:core:lead`. | [`CLAUDE.md`](CLAUDE.md) |
| `:feature:acquisition:{domain,data,ui,navigation}` | The acquisition approval list and detail/decision flow as a feature slice, same shape as lead. | [`CLAUDE.md`](CLAUDE.md) |
| `:feature:specificform:{domain,data,ui}` | The server-driven "specific form" screen (a Fragment rendering a `:formbuilder` schema) as a feature slice. | [`CLAUDE.md`](CLAUDE.md) |
| `:core:session:{domain,data}` | Sign-in: `SessionRepository.login` plus storing the tokens. | [`CLAUDE.md`](CLAUDE.md) |
| `:core:navigation` | `Destination`/`Navigator`/`DestinationHandler`: how one feature opens another's screens without depending on its `ui`. | [`CLAUDE.md`](CLAUDE.md) |
| `:core:lead:{domain,data}` | Lead types both features use: the value types (`domain`) and their wire DTOs + mappers (`data`). | [`CLAUDE.md`](CLAUDE.md) |

See [`CLAUDE.md`](CLAUDE.md) for the full architecture, build/test commands, and the compiler-enforced module boundaries.
