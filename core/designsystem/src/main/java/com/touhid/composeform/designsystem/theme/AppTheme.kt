package com.touhid.composeform.designsystem.theme

import androidx.compose.runtime.Composable

// The app's current theme policy, in one place: light only, regardless of system dark mode or
// dynamic color, for now. Every Activity's (and Fragment's) setContent should call this instead
// of ComposeFormTheme directly, so a future change to the policy (e.g. supporting dark mode)
// only needs to happen here rather than at every Activity. Lives here rather than in :app
// because Activities now live in feature modules, which can't depend on :app.
//
// Deliberately not the default on ComposeFormTheme itself - the Light/Dark @Preview pairs across
// every module rely on ComposeFormTheme following isSystemInDarkTheme() (which resolves
// per-preview via each @Preview's uiMode); forcing light there would silently break every "Dark"
// preview. The design system stays dark-mode-capable; this is only the product's current choice.
@Composable
fun ComposeFormAppTheme(content: @Composable () -> Unit) {
    ComposeFormTheme(darkTheme = false, dynamicColor = false, content = content)
}
