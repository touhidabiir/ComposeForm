package com.touhid.composeform.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.touhid.composeform.R
import com.touhid.composeform.designsystem.components.button.AppButtonStyle
import com.touhid.composeform.designsystem.components.layout.AppScaffold
import com.touhid.composeform.designsystem.components.surface.AppAlertDialog
import com.touhid.composeform.designsystem.components.surface.AppTopBar
import com.touhid.composeform.designsystem.components.surface.AppTopBarAction
import com.touhid.composeform.designsystem.theme.AppSpacing

// What both homes share: the top bar with Log out at the top right (confirmed first), and a
// column for the home's own buttons.
@Composable
fun HomeScaffold(
    onLogout: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    var confirmingLogout by rememberSaveable { mutableStateOf(false) }

    AppScaffold(
        modifier = modifier.fillMaxSize(),
        topBar = { scrollBehavior ->
            AppTopBar(
                title = stringResource(R.string.home_title),
                scrollBehavior = scrollBehavior,
                actions = listOf(
                    AppTopBarAction(
                        icon = Icons.AutoMirrored.Filled.Logout,
                        contentDescription = stringResource(R.string.home_logout),
                        onClick = { confirmingLogout = true },
                    ),
                ),
            )
        },
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(AppSpacing.Medium),
            verticalArrangement = Arrangement.spacedBy(AppSpacing.Medium),
            content = content,
        )
    }

    if (confirmingLogout) {
        AppAlertDialog(
            title = stringResource(R.string.home_logout_confirm_title),
            message = stringResource(R.string.home_logout_confirm_message),
            confirmText = stringResource(R.string.home_logout),
            onConfirm = {
                confirmingLogout = false
                onLogout()
            },
            onDismissRequest = { confirmingLogout = false },
            dismissText = stringResource(R.string.home_logout_confirm_cancel),
            confirmButtonType = AppButtonStyle.Danger,
        )
    }
}
