package com.touhid.composeform.designsystem.components.layout

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import com.touhid.composeform.designsystem.components.surface.AppTopBarScrollBehavior

class AppSnackbarHostState internal constructor(internal val delegate: SnackbarHostState) {
    suspend fun showMessage(message: String) {
        delegate.showSnackbar(message)
    }
}

@Composable
fun rememberAppSnackbarHostState(): AppSnackbarHostState {
    val delegate = remember { SnackbarHostState() }
    return remember(delegate) { AppSnackbarHostState(delegate) }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppScaffold(
    modifier: Modifier = Modifier.fillMaxSize(),
    topBar: @Composable (AppTopBarScrollBehavior) -> Unit = {},
    snackbarHostState: AppSnackbarHostState = rememberAppSnackbarHostState(),
    content: @Composable () -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = { topBar(AppTopBarScrollBehavior(scrollBehavior)) },
        snackbarHost = { SnackbarHost(snackbarHostState.delegate) },
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            content()
        }
    }
}
