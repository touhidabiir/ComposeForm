package com.touhid.composeform.designsystem.components.layout

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import com.touhid.composeform.designsystem.components.surface.AppTopBarScrollBehavior

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppScaffold(
    modifier: Modifier = Modifier.fillMaxSize(),
    // Null (the default) hides the app bar entirely - callers that don't need one shouldn't have
    // to pass a no-op lambda to get that.
    topBar: (@Composable (AppTopBarScrollBehavior) -> Unit)? = null,
    bottomBar: @Composable () -> Unit = {},
    snackbarHost: @Composable () -> Unit = {},
    content: @Composable BoxScope.() -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = { topBar?.invoke(AppTopBarScrollBehavior(scrollBehavior)) },
        bottomBar = bottomBar,
        snackbarHost = snackbarHost,
        // A real AppTopBar consumes the status-bar inset itself. With no topBar, nothing does -
        // left as the default, that inset still lands in innerPadding, leaving a status-bar-height
        // band painted in Scaffold's containerColor above the content (reads as a stray "empty app
        // bar"). Excluding it here only when there's no topBar lets content run edge-to-edge under
        // the status bar instead, same as an actually absent app bar should look.
        contentWindowInsets = if (topBar != null) {
            ScaffoldDefaults.contentWindowInsets
        } else {
            ScaffoldDefaults.contentWindowInsets.exclude(WindowInsets.statusBars)
        },
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            content()
        }
    }
}
