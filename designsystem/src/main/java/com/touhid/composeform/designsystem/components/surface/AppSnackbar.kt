package com.touhid.composeform.designsystem.components.surface

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier

enum class AppSnackbarResult { Dismissed, ActionPerformed }

// Wraps SnackbarHostState so callers never see the raw Material3 type - showMessage's own
// AppSnackbarResult return value lets a caller react to the action (e.g. a "Retry" tap) without
// importing SnackbarResult itself.
@Stable
class AppSnackbarHostState internal constructor(internal val hostState: SnackbarHostState) {
    suspend fun showMessage(message: String, actionLabel: String? = null): AppSnackbarResult {
        val result = hostState.showSnackbar(
            message = message,
            actionLabel = actionLabel,
            duration = SnackbarDuration.Short,
        )
        return if (result == SnackbarResult.ActionPerformed) AppSnackbarResult.ActionPerformed else AppSnackbarResult.Dismissed
    }
}

@Composable
fun rememberAppSnackbarHostState(): AppSnackbarHostState {
    val hostState = remember { SnackbarHostState() }
    return remember(hostState) { AppSnackbarHostState(hostState) }
}

@Composable
fun AppSnackbarHost(state: AppSnackbarHostState, modifier: Modifier = Modifier) {
    SnackbarHost(hostState = state.hostState, modifier = modifier)
}
