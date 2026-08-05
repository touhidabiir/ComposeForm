package com.touhid.composeform

import androidx.compose.runtime.Composable
import com.touhid.composeform.designsystem.theme.ComposeFormTheme

// The app's current theme policy, in one place: light only, regardless of system dark mode or
// dynamic color, for now. Every Activity's setContent should call this instead of
// ComposeFormTheme directly, so a future change to the policy (e.g. supporting dark mode) only
// needs to happen here rather than at every Activity.
//
// Deliberately not the default on ComposeFormTheme itself - :designsystem's own Light/Dark
// @Preview pairs across every component rely on ComposeFormTheme following
// isSystemInDarkTheme() (which resolves per-preview via each @Preview's uiMode); forcing light
// there would silently break every "Dark" preview in the module. This is an :app-level product
// decision, not a :designsystem capability - the design system stays dark-mode-capable.
@Composable
fun ComposeFormAppTheme(content: @Composable () -> Unit) {
    ComposeFormTheme(darkTheme = false, dynamicColor = false, content = content)
}
