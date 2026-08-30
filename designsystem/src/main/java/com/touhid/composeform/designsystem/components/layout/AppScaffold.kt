package com.touhid.composeform.designsystem.components.layout

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.Dp
import com.touhid.composeform.designsystem.components.surface.AppTopBarScrollBehavior
import com.touhid.composeform.designsystem.theme.AppSpacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppScaffold(
    modifier: Modifier = Modifier.fillMaxSize(),
    // Null (the default) hides the app bar/bottom bar entirely - callers that don't need one
    // shouldn't have to pass a no-op lambda to get that.
    topBar: (@Composable (AppTopBarScrollBehavior) -> Unit)? = null,
    bottomBar: (@Composable () -> Unit)? = null,
    snackbarHost: @Composable () -> Unit = {},
    content: @Composable BoxScope.() -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = { topBar?.invoke(AppTopBarScrollBehavior(scrollBehavior)) },
        bottomBar = { bottomBar?.invoke() },
        snackbarHost = snackbarHost,
        // A real AppTopBar/AppBottomActionBar consumes its own edge's system-bar inset itself
        // (see AppBottomActionBar's windowInsetsPadding(WindowInsets.navigationBars)). With no
        // topBar/bottomBar, nothing does - left as the default, that inset still lands in
        // innerPadding, leaving a status/navigation-bar-height band painted in Scaffold's
        // containerColor at that edge of the content (reads as a stray "empty bar"). Excluding it
        // here only for the edge that's actually missing its bar lets content run edge-to-edge
        // there instead, same as an actually absent bar should look.
        contentWindowInsets = ScaffoldDefaults.contentWindowInsets
            .let { if (topBar == null) it.exclude(WindowInsets.statusBars) else it }
            .let { if (bottomBar == null) it.exclude(WindowInsets.navigationBars) else it },
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            content()
        }
    }
}

// The complement to the navigationBars exclusion above: a screen with no bottomBar gets
// edge-to-edge content from AppScaffold, but a scrollable list inside it (LazyColumn/LazyRow's
// contentPadding) still needs to keep its last item from sitting under the system navigation bar.
// One shared place for that math, rather than every such screen re-deriving it inline.
@Composable
fun edgeToEdgeListContentPadding(horizontal: Dp = AppSpacing.Medium, vertical: Dp = AppSpacing.Small): PaddingValues {
    return PaddingValues(
        start = horizontal,
        end = horizontal,
        top = vertical,
        bottom = vertical + WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding(),
    )
}
