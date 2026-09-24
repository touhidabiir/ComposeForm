# `:designsystem`

An Android library module (namespace `com.touhid.composeform.designsystem`) owning all Material3-based UI. `:app` depends on it via `implementation(project(":designsystem"))`.

## Material3 boundary (important, easy to violate accidentally)

`:designsystem` depends on `androidx.compose.material3` and `androidx.compose.foundation` as `implementation` (not `api`). Neither `:app` nor `:formbuilder` declare either dependency themselves. This means Material3/Foundation classes are not on their compile classpaths at all — any `import androidx.compose.material3.*` added there will fail to compile. This is deliberate, compiler-enforced encapsulation: all UI must go through `:designsystem`'s wrapped components (`AppText`, `AppButton`, `AppScaffold`, etc.), never the raw Material3 APIs. (Foundation layout primitives — `Column`/`Row`/`Box`/`Modifier.padding` — are fine for any module to use directly; only Material3 is restricted. This includes `Image` — `androidx.compose.foundation.Image` (and Coil's `AsyncImage`, if/when added) is Foundation, not Material3, so `:app` can call it directly with no `AppImage` wrapper needed; `Icon` is the one that's Material3-gated and needs `AppIcon`.)

When a module needs a new Material3 primitive it doesn't have a wrapper for yet, add the wrapper to `:designsystem` rather than adding a direct dependency elsewhere.

## Internal structure

```
designsystem/src/main/java/com/touhid/composeform/designsystem/
├── theme/                  # Color.kt (incl. semantic Status*/Status*Container tones), Theme.kt (ComposeFormTheme),
│                            # Type.kt, Spacing.kt (AppSpacing)
└── components/
    ├── text/               # AppText + AppTextStyle enum + AppTextOverride (size/weight/color override),
    │                        # AppIconLabelValue (icon + value, or label caption stacked above value;
    │                        # AppIconPosition: Start/End/Top/Bottom decides which side the icon sits on;
    │                        # optional subValue renders a second, smaller/muted caption below the value;
    │                        # optional trailingIcon sits next to the value itself, e.g. a copy glyph)
    ├── button/             # AppButton, AppOutlinedButton (+ AppButtonStyle: Primary/Success/Danger,
    │                        # both with an optional leadingIcon slot), AppStepperButton
    ├── layout/             # AppScaffold (topBar + optional bottomBar slot + content), AppPullToRefreshBox
    │                        # (wraps Material3's pull-to-refresh; isRefreshing/onRefresh + a BoxScope content slot)
    ├── input/              # AppTextField (+ AppTextFieldType: Text/Number/Email/Password), AppSearchField
    │                        # (pill-shaped filled search bar - distinct from AppTextField; leadingIcon
    │                        # defaults to none, trailingIcon defaults to none, both nullable composable
    │                        # slots so either/both can be omitted entirely), AppCheckbox,
    │                        # AppRadioButton, AppSwitch, AppDropdown (+ AppDropdownOption),
    │                        # AppRadioToggleChip, AppRadioCheckCircle
    ├── icon/                # AppIcon (non-interactive), AppIconButton (clickable)
    └── surface/             # AppTopBar (+ AppTopBarAction, AppTopBarScrollBehavior), AppCard (optional
                             # topContent slot renders before the padded content column, flush with the
                             # card's own bounds/corners - for a full-bleed banner), AppHorizontalDivider,
                             # AppStatusBadge (+ AppStatusTone: Success/Warning/Error/Info/Neutral - always
                             # shows a status dot), AppChip (selectable pill for filter tabs - no dot,
                             # communicates selection rather than state), AppBottomActionBar (flat elevated
                             # bar for a screen's pinned bottom actions), AppBottomSheet (wraps
                             # ModalBottomSheet, scrollable ColumnScope content slot), AppSnackbar (wraps
                             # SnackbarHostState; showMessage returns AppSnackbarResult - Dismissed/
                             # ActionPerformed - so callers react to e.g. a "Retry" action without seeing
                             # the raw Material3 SnackbarResult), AppProgressDialog (blocking,
                             # non-dismissable spinner overlay - for a reload with existing content behind
                             # it worth protecting from interaction) and AppProgressIndicator (a plain
                             # non-blocking inline spinner - for a screen's very first load, before there's
                             # any content yet)
```

Components are organized by category (not a flat package) — when adding a new component, put it under the matching category subpackage, creating a new one if it doesn't fit an existing one.

## Conventions established by existing components

- Each component wraps a Material3 equivalent with a narrowed, opinionated API (e.g. `AppTextStyle` enum instead of raw `TextStyle` passthrough) — don't leak Material3/Foundation types (like `PaddingValues`) through a component's public signature if avoidable (see `AppScaffold`: it absorbs `innerPadding` internally via a `Box` rather than exposing it). A container component's `content` slot should still be typed with that container's actual scope (`AppCard`'s is `ColumnScope`, `AppBottomActionBar`'s is `RowScope`, `AppScaffold`'s is `BoxScope`) — the scope isn't a Material3/Foundation type worth hiding, and exposing the wrong one (or none) either silently breaks `weight`/`align` modifiers at runtime or blocks them outright.
- Every component that renders text accepts an optional `AppTextOverride` (`fontSize`/`fontWeight`/`color`, all no-op by default) — `AppText`'s `override`, `AppButton`/`AppOutlinedButton`'s `textOverride`, `AppCheckbox`/`AppRadioButton`/`AppSwitch`/`AppDropdown`'s `labelOverride`. This is how callers (like `:formbuilder`) apply per-instance styling without the design system losing its opinionated defaults.
- Spacing between elements inside a component uses `AppSpacing` (`theme/Spacing.kt`) tokens, not hardcoded `dp` values.
- Each category subpackage has its own `*Previews.kt` file (not one global previews file) with a private composable carrying stacked `@Preview(name = "Light", ...)` / `@Preview(name = "Dark", uiMode = Configuration.UI_MODE_NIGHT_YES, ...)` annotations, wrapped in `ComposeFormTheme`.
- Modules outside `:designsystem` (e.g. `:app`) can reference `ImageVector` constants from `androidx.compose.material:material-icons-core` (or its superset, `material-icons-extended` — `:app` uses the extended artifact since it needs glyphs like `ContentCopy` outside the curated core set; both are separate artifacts with no Material3 dependency) directly and pass them into a designsystem component (`AppIcon`, `AppIconButton`, `AppTopBarAction`, `AppButton`'s `leadingIcon` slot, etc.) — but any `@Composable` slot lambda a caller *writes* (e.g. `AppIconLabelValue`'s `icon` param) still compiles as part of that caller's own module, so its body must only call designsystem-exposed composables (`AppIcon`, not raw Material3 `Icon`), never Material3 directly.
- Not everything a screen renders belongs in `:designsystem`, even when it's visually involved and looks reusable in principle. The boundary's purpose is specifically wrapping Material3 access `:app`/`:formbuilder` can't otherwise reach — a composition already buildable from exposed primitives (`AppText`, `AppIcon`, Foundation `Row`/`Column`/`Box`/`border`/`background`) has no Material3-wrapping reason to move, and "some other screen might want this shape someday" isn't a reason either - that's the same speculative-reuse trap the rest of this doc argues against for ordinary code. Ask two concrete questions instead of a hypothetical one: (1) does it have an *actual* second caller today, not an imagined future one? (2) does it use a Material3-derived default (`MaterialTheme.colorScheme...`) that its real caller(s) actually rely on, rather than always overriding? `AcquisitionApprovalDetailScreen`'s score-band step indicator and its score circle both answer no to both - one caller, always-explicit colors - so both live in `:app`, built from `AppText`/`AppIcon`/`AppTextOverride` plus plain Foundation shapes.

**Not yet built**: a generic Material3 `AlertDialog` wrapper (title/message/confirm-cancel buttons) — `AppProgressDialog` (spinner-only) and `AppBottomSheet` (modal bottom sheet) exist and cover their own specific cases, but nothing wraps a plain confirm/alert dialog yet. Follow the same wrapping conventions above when implementing it.

For the broader module architecture and the other module boundaries, see [`CLAUDE.md`](../CLAUDE.md).
