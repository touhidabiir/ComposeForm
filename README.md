# ComposeForm

ComposeForm renders a fully interactive form from a JSON schema — no per-screen Compose UI code needed. You write (or fetch from an API) a JSON document describing fields, options, styling, layout, and conditional visibility; `parseFormSchema` turns it into a `FormSchema`, and `FormRenderer` draws it.

The project is split into three modules — `:app` (shell), `:designsystem` (Material3-based UI components), `:formbuilder` (JSON schema + rendering, the subject of this document). See `CLAUDE.md` for the full module architecture and conventions; this document is specifically a reference for **what the JSON schema supports**.

## Quick start

```kotlin
val schema = parseFormSchema(jsonString)

FormRenderer(
    schema = schema,
    onSubmit = { values -> /* Map<String, FormValue> of submitted data */ },
)
```

## Top-level schema

Every JSON document is a single object with these properties:

| Property | Type | Default | Description |
|---|---|---|---|
| `fields` | array of fields (required) | — | The fields to render, in order. See [Field types](#field-types). |
| `screenTitle` | string, nullable | `null` | Used as the screen's top app bar title when `FormRenderer` is hosted inside `AppScaffold`/`AppTopBar`. If omitted, the hosting screen falls back to its own default title. |
| `numbered` | boolean | `false` | When `true`, prefixes each answerable field's label with its live serial number (`"1. What is your name?"`). Only counts `inputBox`, `checkbox`, `checkboxGroup`, `radio`, `switch`, and `dropdown` fields — `text` and `submit` are never numbered. The count only includes currently-visible fields, so a field hidden/shown via `visibleWhen` immediately renumbers everything after it. |
| `language` | string | `"en"` | One of `"en"` or `"bn"`. Controls only the numeral script used when `numbered` is `true` — `"bn"` renders question numbers as Bangla numerals (`১, ২, ৩...`) instead of Arabic digits. Does not translate any other text; field/option labels are still authored directly in whichever language the JSON already uses. |

## Properties common to every field

Every field, regardless of `type`, accepts:

| Property | Type | Default | Description |
|---|---|---|---|
| `type` | string (required) | — | Discriminator selecting the field's shape. One of `text`, `inputBox`, `checkbox`, `checkboxGroup`, `radio`, `switch`, `dropdown`, `submit`. |
| `key` | string (required) | — | Identifies the field in the submitted data map and in `visibleWhen` references. Must be unique within a schema. |
| `label` | string | `""` | Display text. A blank label is simply skipped (no empty space reserved) — useful when a preceding `text` field supplies the heading for a group instead. |
| `margin` | [`FormInsets`](#forminsets) | all zero | Space *outside* the field, separating it from neighbors. |
| `padding` | [`FormInsets`](#forminsets) | all zero | Space *inside* the field, around its content. |
| `style` | [`FormTextStyle`](#formtextstyle), nullable | `null` | Font size/color/weight for the field's text. |
| `size` | [`FormSize`](#formsize) | width `match_parent`, height `wrap_content` | The field's own width/height. |
| `border` | [`FormBorder`](#formborder), nullable | `null` | Optional border drawn around the field. |
| `visibleWhen` | [`FormVisibilityCondition`](#conditional-visibility-visiblewhen), nullable | `null` | Hides the field unless the condition is satisfied. See [Conditional visibility](#conditional-visibility-visiblewhen). |

### `FormInsets`

```json
{ "top": 8, "bottom": 8, "left": 0, "right": 0 }
```

All four are optional `Int` dp values, default `0`. Any subset may be given — omitted sides stay `0`.

### `FormTextStyle`

```json
{ "size": 16, "color": "#1F1F1F", "weight": "medium" }
```

| Property | Type | Description |
|---|---|---|
| `size` | Int, nullable | Font size in sp. |
| `color` | string, nullable | Hex color: `#RRGGBB` or `#AARRGGBB`. |
| `weight` | string, nullable | One of `light`, `normal`, `medium`, `semibold`, `bold`. Unrecognized values are ignored. |

### `FormSize`

```json
{ "width": "match_parent", "height": "wrap_content" }
```

Each of `width`/`height` is one of:
- `"match_parent"` — fill available space on that axis.
- `"wrap_content"` — size to content (the default for both, except `width` defaults to `match_parent`).
- a numeric string (e.g. `"200"`) — a fixed dp value.

### `FormBorder`

```json
{ "color": "#9E9E9E", "width": 1, "radius": 12 }
```

| Property | Type | Default | Description |
|---|---|---|---|
| `color` | string | `"#000000"` | Hex color. |
| `width` | Int | `1` | Border thickness in dp. |
| `radius` | Int | `0` | Corner radius in dp. |

## Field types

### `text`

A display-only heading/label. No properties beyond the common set above — its whole purpose is `label` + `style`.

### `inputBox`

A text input field.

| Property | Type | Default | Description |
|---|---|---|---|
| `required` | Boolean | `false` | Must be non-blank to pass validation. |
| `inputType` | string | `"text"` | One of `text`, `number`, `email`, `password` — controls keyboard type and (for `password`) masking. Unrecognized values fall back to `text`. |
| `defaultValue` | string | `""` | Pre-filled value. |
| `pattern` | string, nullable | `null` | Regex the value must match (only checked when non-empty). |
| `errorMessage` | string, nullable | `null` | Custom message shown instead of the default one when any validation rule fails. |
| `minLength` / `maxLength` | Int, nullable | `null` | Length bounds. |
| `editable` | Boolean | `true` | `false` blocks typing (keyboard never appears) but keeps the field visually normal — if it also has `pickerScreen`, tapping anywhere on the field (not just the icon) opens the picker. |
| `enabled` | Boolean | `true` | `false` fully disables the field — greyed out, no focus, no typing, and the picker icon (if any) also stops responding. |
| `pickerScreen` | [`FormSchema`](#top-level-schema), nullable | `null` | Opens a picker screen when its trailing icon (or, if `editable: false`, the whole field) is tapped. See [Nested picker screens](#nested-picker-screens-pickerscreen). |

### `checkbox`

A single checkbox.

| Property | Type | Default | Description |
|---|---|---|---|
| `required` | Boolean | `false` | Must be checked to pass validation. |
| `defaultValue` | Boolean | `false` | Initial checked state. |

### `switch`

A toggle switch — same shape as `checkbox`.

| Property | Type | Default | Description |
|---|---|---|---|
| `required` | Boolean | `false` | Must be on to pass validation. |
| `defaultValue` | Boolean | `false` | Initial state. |

### `radio`

A single-select group of options.

| Property | Type | Default | Description |
|---|---|---|---|
| `required` | Boolean | `false` | Must have a selection to pass validation. |
| `options` | array of [`FormOption`](#formoption) (required) | — | The choices. |
| `orientation` | `"vertical"` \| `"horizontal"` | `"vertical"` | Layout direction. Horizontal options are distributed with equal width. |
| `appearance` | `"dot"` \| `"check"` \| `"toggle"` | `"dot"` | `dot` = standard Material radio button; `check` = checkmark-in-circle list item; `toggle` = equal-width pill/chip. |

### `dropdown`

A single-select dropdown menu.

| Property | Type | Default | Description |
|---|---|---|---|
| `required` | Boolean | `false` | Must have a selection to pass validation. |
| `options` | array of [`FormOption`](#formoption) (required) | — | The choices. |

### `checkboxGroup`

A multi-select group of checkboxes.

| Property | Type | Default | Description |
|---|---|---|---|
| `required` | Boolean | `false` | Must have at least one selection to pass validation. |
| `options` | array of [`FormOption`](#formoption) (required) | — | The choices. |
| `orientation` | `"vertical"` \| `"horizontal"` | `"vertical"` | Layout direction. |

### `submit`

The submit button. Disabled automatically while any field in the same schema fails validation; calls the `onSubmit` callback with the current submitted values when tapped.

| Property | Type | Default | Description |
|---|---|---|---|
| `sticky` | Boolean | `false` | `false` (default): the button scrolls with the rest of the form content, exactly like any other field. `true`: the button is pinned to the bottom of the screen, and the remaining fields scroll in the space above it. |
| `appearance` | `"plain"` \| `"stepper"` | `"plain"` | `plain` = today's single-zone button. `stepper` = a pill-shaped button split into a progress zone (`progressText`) and a label zone with a fixed trailing chevron — for survey/wizard-style "next step" buttons. |
| `progressText` | string, nullable | `null` | Only used when `appearance` is `"stepper"` — literal text shown in the left zone (e.g. `"১/১০"`). Not computed automatically; author it directly in the JSON. If `null`, the left zone and divider are omitted. |

## `FormOption`

Used by `radio`, `dropdown`, and `checkboxGroup`:

```json
{ "id": "male", "value": "Male", "default": false, "style": { "weight": "bold" }, "margin": { "top": 4, "bottom": 4 } }
```

| Property | Type | Default | Description |
|---|---|---|---|
| `id` | string (required) | — | Returned in submitted data; not shown to the user. |
| `value` | string (required) | — | The displayed text. |
| `default` | Boolean | `false` | Pre-selects this option (first matching option wins for single-select fields). |
| `style` | [`FormTextStyle`](#formtextstyle), nullable | `null` | Per-option text styling. |
| `margin` / `padding` | [`FormInsets`](#forminsets) | all zero | Per-option spacing — this is what actually creates the gap between adjacent options in a group (the group itself applies no arrangement spacing). |
| `border` | [`FormBorder`](#formborder), nullable | `null` | Per-option border. |

## Nested picker screens (`pickerScreen`)

An `inputBox` field can carry a *complete second `FormSchema`* under `pickerScreen`. Tapping the field's trailing search icon (or, when `editable: false`, anywhere on the field) navigates to a screen that renders that nested schema. Whichever field in the picker schema isn't `text` or `submit` is treated as "the answer" — its submitted value is written back into the field that opened the picker, and the picker screen is popped.

Because the picker's schema is nested directly in the same JSON document (rather than referenced by an ID the app has to know about), adding a new picker-enabled field is a pure JSON/backend change — no app code changes required.

```json
{
  "type": "inputBox", "key": "location", "label": "Location Type", "required": true,
  "pickerScreen": {
    "screenTitle": "Select Location Type",
    "fields": [
      {
        "type": "radio", "key": "location_type", "required": true, "orientation": "vertical", "appearance": "check",
        "options": [
          { "id": "inside_market", "value": "Inside the market" },
          { "id": "beside_road", "value": "Beside the road" }
        ]
      },
      { "type": "submit", "key": "submit", "label": "Confirm" }
    ]
  }
}
```

## Conditional visibility (`visibleWhen`)

Any field can be shown only when another field's current value matches a rule:

```json
{
  "key": "gender",
  "operator": "notEquals",
  "values": ["male"]
}
```

| Property | Type | Description |
|---|---|---|
| `key` | string (required) | The other field's `key` to read the current value from. |
| `operator` | `"equals"` \| `"notEquals"` \| `"in"` | `equals`/`notEquals` compare against `values[0]` only (extra entries ignored); `in` checks membership across the whole list. |
| `values` | array of strings (required) | Comparison value(s). For option-based fields (`radio`/`dropdown`/`checkboxGroup`), compare against the option `id`, not `value`. |

If the referenced field has no current value at all (never touched, no default, or itself currently hidden), the condition is **not satisfied** for every operator — including `notEquals`. A field that becomes hidden has its value cleared from the submitted data immediately.

Example (mirrors the shipped demo): a newsletter opt-in only appears once a gender selection has been made and isn't `male`:

```json
{
  "type": "radio", "key": "newsletter", "label": "Subscribe to newsletter?",
  "visibleWhen": { "key": "gender", "operator": "notEquals", "values": ["male"] },
  "options": [
    { "id": "yes", "value": "Yes" },
    { "id": "no", "value": "No" }
  ]
}
```

## Validation rules

| Field type | `required` means |
|---|---|
| `inputBox` | Non-blank. Also independently checks `pattern` (if non-empty), `minLength`, `maxLength` regardless of `required`. |
| `checkbox` / `switch` | Must be checked/on. |
| `radio` / `dropdown` | Must have a selection. |
| `checkboxGroup` | Must have at least one selection. |
| `text` / `submit` | Not applicable — never produce a validation error. |

The `submit` button is disabled whenever any field in the schema currently has a validation error. Per-field error text only appears once that field has been touched, so the button being disabled from the very first frame doesn't create a dead end.

## Full worked example

```json
{
  "screenTitle": "Sign Up",
  "fields": [
    { "type": "text", "key": "heading", "label": "Sign Up", "style": { "size": 24, "weight": "bold" } },
    { "type": "inputBox", "key": "name", "label": "Name", "required": true },
    {
      "type": "inputBox", "key": "email", "label": "Email", "required": true, "inputType": "email",
      "pattern": "^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$",
      "errorMessage": "Enter a valid email address"
    },
    {
      "type": "radio", "key": "gender", "label": "Gender", "required": true, "orientation": "horizontal",
      "options": [
        { "id": "male", "value": "Male" },
        { "id": "female", "value": "Female" }
      ]
    },
    {
      "type": "radio", "key": "newsletter", "label": "Subscribe to newsletter?",
      "visibleWhen": { "key": "gender", "operator": "notEquals", "values": ["male"] },
      "options": [
        { "id": "yes", "value": "Yes", "default": true },
        { "id": "no", "value": "No" }
      ]
    },
    {
      "type": "dropdown", "key": "country", "label": "Country", "required": true,
      "options": [
        { "id": "bangladesh", "value": "Bangladesh" },
        { "id": "india", "value": "India" }
      ]
    },
    { "type": "checkbox", "key": "acceptTerms", "label": "I agree to the terms and conditions", "required": true },
    { "type": "submit", "key": "submit", "label": "Submit" }
  ]
}
```

## Where to modify this

| To do this | Edit these files |
|---|---|
| Add a property to an existing field type | `formbuilder/.../schema/FormField.kt` (add the property to the relevant data class), then wire its effect in `formbuilder/FormRenderer.kt`'s `RenderField`. |
| Change how a JSON string/enum maps to a Compose value | `formbuilder/FormFieldMappers.kt`. |
| Change validation rules | `formbuilder/FormValidator.kt`. |
| Change conditional-visibility semantics | `formbuilder/FormFieldVisibility.kt`. |
| Add a brand new field `type` | Add a variant to the `FormField` sealed interface (`FormField.kt`), register its `"type"` discriminator string in `FormFieldSerializer`, add a rendering branch in `FormRenderer.kt`'s `RenderField`. |

For the broader module architecture (why `:designsystem`/`:formbuilder`/`:app` are split the way they are, and the Material3-encapsulation boundary), see `CLAUDE.md`.
