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

The app is organised as feature slices on top of a thin shared `core`. Each business feature owns its `domain` (models + repository interfaces), `data` (API, DTOs, repository implementations), `ui` (screens, ViewModels, host Activity) and, when other modules open its screens, `navigation` (destinations). Data two features share moves into a core slice. The build enforces the dependency rules between them.

| Module | Description | Docs |
|---|---|---|
| `:app` | The application shell: launcher, the `@HiltAndroidApp` entry point, the `Navigator` implementation, the demo server-driven form flow (`DemoFormApi`), and the Hilt config `:core:network` needs (base URL, token storage). | [`CLAUDE.md`](CLAUDE.md) |
| `:feature:lead:{domain,data,ui,navigation}` | The lead dashboard. | [`CLAUDE.md`](CLAUDE.md) |
| `:feature:acquisition:{domain,data,ui,navigation}` | The acquisition approval list and detail/decision flow. | [`CLAUDE.md`](CLAUDE.md) |
| `:feature:specificform:{domain,data,ui}` | The server-driven "specific form" Fragment, rendering a `:formbuilder` schema. | [`CLAUDE.md`](CLAUDE.md) |
| `:core:lead:{domain,data}` | Core slice: lead types both features show, and their wire DTOs + mappers. | [`CLAUDE.md`](CLAUDE.md) |
| `:core:session:{domain,data}` | Core slice: sign-in (`SessionRepository.login`, storing the tokens). | [`CLAUDE.md`](CLAUDE.md) |
| `:core:model` | `AppResult`/`AppError`, the result type every repository returns. | [`CLAUDE.md`](CLAUDE.md) |
| `:core:network` | The HTTP client: main `Retrofit`, auth and token refresh, interceptors, `safeAppCall`. The only module that builds a client; feature `data` modules declare their own endpoints on it. | [`core/network/README.md`](core/network/README.md) |
| `:core:navigation` | `Destination`/`Navigator`/`DestinationHandler`: how one module opens another feature's screens without depending on its `ui`. | [`CLAUDE.md`](CLAUDE.md) |
| `:core:designsystem` | All Material3-based UI, plus the app theme. The only module allowed to depend on Material3 — everything else goes through its wrapped components (`AppText`, `AppButton`, `AppScaffold`, etc.). | [`core/designsystem/README.md`](core/designsystem/README.md) |
| `:core:common` | Compose helpers and strings shared by more than one feature. | [`CLAUDE.md`](CLAUDE.md) |
| `:formbuilder` | Parses a JSON form schema (`kotlinx.serialization`) and renders it using `:core:designsystem`'s components. | [`formbuilder/README.md`](formbuilder/README.md) |

Shared build setup lives in convention plugins under `build-logic/` (see [`build-logic/README.md`](build-logic/README.md)). A shareable write-up of the architecture, with diagrams, is in [`docs/architecture.docx`](docs/architecture.docx).

See [`CLAUDE.md`](CLAUDE.md) for the full architecture, build/test commands, and the build-enforced module boundaries.
