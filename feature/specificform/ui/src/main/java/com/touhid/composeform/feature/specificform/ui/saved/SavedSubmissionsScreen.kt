package com.touhid.composeform.feature.specificform.ui.saved

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.touhid.composeform.common.ListEmptyState
import com.touhid.composeform.designsystem.components.button.AppButtonStyle
import com.touhid.composeform.designsystem.components.button.AppOutlinedButton
import com.touhid.composeform.designsystem.components.icon.AppIconButton
import com.touhid.composeform.designsystem.components.layout.AppScaffold
import com.touhid.composeform.designsystem.components.layout.edgeToEdgeContentPadding
import com.touhid.composeform.designsystem.components.surface.AppAlertDialog
import com.touhid.composeform.designsystem.components.surface.AppCard
import com.touhid.composeform.designsystem.components.surface.AppProgressIndicator
import com.touhid.composeform.designsystem.components.surface.AppSnackbarHost
import com.touhid.composeform.designsystem.components.surface.AppTopBar
import com.touhid.composeform.designsystem.components.surface.rememberAppSnackbarHostState
import com.touhid.composeform.designsystem.components.text.AppText
import com.touhid.composeform.designsystem.components.text.AppTextStyle
import com.touhid.composeform.designsystem.theme.AppSpacing
import com.touhid.composeform.designsystem.theme.StatusError
import com.touhid.composeform.designsystem.theme.StatusNeutral
import com.touhid.composeform.feature.specificform.domain.SavedSubmission
import com.touhid.composeform.feature.specificform.ui.R
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
internal fun SavedSubmissionsScreen(
    onBack: () -> Unit,
    onOpen: (String) -> Unit,
    viewModel: SavedSubmissionsViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    SavedSubmissionsContent(
        state = state,
        onBack = onBack,
        onOpen = onOpen,
        onScreenStart = viewModel::onScreenStart,
        onUpload = viewModel::onUpload,
        onDeleteTapped = viewModel::onDeleteTapped,
        onDeleteCancelled = viewModel::onDeleteCancelled,
        onDeleteConfirmed = viewModel::onDeleteConfirmed,
        onResultShown = viewModel::onResultShown,
    )
}

@Composable
private fun SavedSubmissionsContent(
    state: SavedSubmissionsState,
    onBack: () -> Unit,
    onOpen: (String) -> Unit,
    onScreenStart: () -> Unit,
    onUpload: (String) -> Unit,
    onDeleteTapped: (String) -> Unit,
    onDeleteCancelled: () -> Unit,
    onDeleteConfirmed: () -> Unit,
    onResultShown: () -> Unit,
) {
    LaunchedEffect(Unit) { onScreenStart() }

    val snackbarHostState = rememberAppSnackbarHostState()
    val coroutineScope = rememberCoroutineScope()
    val message = when (val result = state.result) {
        SavedSubmissionsResult.Uploaded -> stringResource(R.string.specificform_uploaded)
        SavedSubmissionsResult.Deleted -> stringResource(R.string.specificform_deleted)
        is SavedSubmissionsResult.Failed -> result.message
        null -> null
    }
    // Consumed right away and shown from a separate coroutine: clearing it inside an effect keyed
    // on it would cancel that effect, and the snackbar with it.
    LaunchedEffect(message) {
        if (message == null) return@LaunchedEffect
        onResultShown()
        coroutineScope.launch { snackbarHostState.showMessage(message = message) }
    }

    if (state.deleteConfirmId != null) {
        AppAlertDialog(
            title = stringResource(R.string.specificform_delete_title),
            message = stringResource(R.string.specificform_delete_message),
            confirmText = stringResource(R.string.specificform_delete),
            onConfirm = onDeleteConfirmed,
            onDismissRequest = onDeleteCancelled,
            dismissText = stringResource(R.string.specificform_cancel),
            confirmButtonType = AppButtonStyle.Danger,
        )
    }

    AppScaffold(
        topBar = { scrollBehavior ->
            AppTopBar(
                title = stringResource(R.string.specificform_saved_forms),
                navigationIcon = Icons.AutoMirrored.Filled.ArrowBack,
                onNavigationClick = onBack,
                scrollBehavior = scrollBehavior,
            )
        },
        snackbarHost = { AppSnackbarHost(snackbarHostState) },
    ) {
        when {
            state.isLoading -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                AppProgressIndicator()
            }
            state.items.isEmpty() -> ListEmptyState(message = stringResource(R.string.specificform_no_saved_forms))
            else -> LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = edgeToEdgeContentPadding(),
                verticalArrangement = Arrangement.spacedBy(AppSpacing.Medium),
            ) {
                items(state.items, key = { it.id }) { item ->
                    SavedSubmissionRow(
                        item = item,
                        isUploading = state.uploadingId == item.id,
                        uploadEnabled = state.uploadingId == null,
                        onOpen = { onOpen(item.id) },
                        onUpload = { onUpload(item.id) },
                        onDelete = { onDeleteTapped(item.id) },
                    )
                }
            }
        }
    }
}

@Composable
private fun SavedSubmissionRow(
    item: SavedSubmission,
    isUploading: Boolean,
    uploadEnabled: Boolean,
    onOpen: () -> Unit,
    onUpload: () -> Unit,
    onDelete: () -> Unit,
) {
    AppCard(modifier = Modifier.fillMaxWidth().clickable(onClick = onOpen)) {
        Column(verticalArrangement = Arrangement.spacedBy(AppSpacing.Small)) {
            AppText(text = item.title, style = AppTextStyle.TitleMedium)
            AppText(
                text = stringResource(R.string.specificform_saved_on, formatSavedAt(item.savedAt)),
                style = AppTextStyle.BodyMedium,
                color = StatusNeutral,
            )
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(AppSpacing.Small)) {
                AppOutlinedButton(
                    text = stringResource(if (isUploading) R.string.specificform_uploading else R.string.specificform_upload),
                    onClick = onUpload,
                    enabled = uploadEnabled,
                )
                AppIconButton(
                    icon = Icons.Outlined.Delete,
                    contentDescription = stringResource(R.string.specificform_delete),
                    onClick = onDelete,
                    enabled = !isUploading,
                    tint = StatusError,
                )
            }
        }
    }
}

private fun formatSavedAt(millis: Long): String =
    SimpleDateFormat("d MMM yyyy, h:mm a", Locale.getDefault()).format(Date(millis))
