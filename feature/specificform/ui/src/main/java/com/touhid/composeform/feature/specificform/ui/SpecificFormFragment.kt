package com.touhid.composeform.feature.specificform.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.touhid.composeform.common.R as CommonR
import com.touhid.composeform.designsystem.components.button.AppButton
import com.touhid.composeform.designsystem.components.layout.AppScaffold
import com.touhid.composeform.designsystem.components.surface.AppAlertDialog
import com.touhid.composeform.designsystem.components.surface.AppProgressDialog
import com.touhid.composeform.designsystem.components.surface.AppProgressIndicator
import com.touhid.composeform.designsystem.components.surface.AppSnackbarHost
import com.touhid.composeform.designsystem.components.surface.AppSnackbarResult
import com.touhid.composeform.designsystem.components.surface.AppTopBar
import com.touhid.composeform.designsystem.components.surface.AppTopBarAction
import com.touhid.composeform.designsystem.components.surface.rememberAppSnackbarHostState
import com.touhid.composeform.designsystem.components.text.AppText
import com.touhid.composeform.designsystem.theme.ComposeFormAppTheme
import com.touhid.composeform.feature.specificform.ui.saved.OpenSavedSubmissions
import com.touhid.composeform.formbuilder.FormRenderer
import com.touhid.composeform.formbuilder.schema.FormValue
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

// Intended to be one of several fragments a host Activity swaps through (a form wizard), created
// via newInstance() with this fragment's position among its siblings; for now SpecificFormActivity
// hosts it alone, as step 1 of 1. Fetches its form schema from SpecificFormRepository.getSpecificForm()
// (backed by the v1/forms/specific mock while no real backend exists) and renders it with
// FormRenderer. Submit asks whether to upload now (v1/forms/specific/submit, also mocked) or save
// on the device; the toolbar's saved-forms action opens SavedSubmissionsActivity, which hands back
// a saved submission to reopen here for editing.
//
// The long @AndroidEntryPoint(Fragment::class) form, extending the generated
// Hilt_SpecificFormFragment directly: only :app applies the Hilt Gradle plugin, and that plugin's
// bytecode rewrite is what lets the short form (`: Fragment()`) work.
@AndroidEntryPoint(Fragment::class)
class SpecificFormFragment : Hilt_SpecificFormFragment() {

    private val viewModel: SpecificFormViewModel by viewModels()

    private val openSavedSubmissions = registerForActivityResult(OpenSavedSubmissions()) { savedId ->
        savedId?.let(viewModel::onReopenSaved)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val position = arguments?.getInt(ArgPosition, 1) ?: 1
        val totalCount = arguments?.getInt(ArgTotalCount, 1) ?: 1
        return ComposeView(requireContext()).apply {
            setContent {
                ComposeFormAppTheme {
                    LaunchedEffect(Unit) { viewModel.onScreenStart(position, totalCount) }

                    val state by viewModel.state.collectAsStateWithLifecycle()
                    SpecificFormScreen(
                        state = state,
                        onRetryLoad = viewModel::retry,
                        onOpenSaved = { formKey -> openSavedSubmissions.launch(formKey) },
                        onSubmit = viewModel::onSubmit,
                        onUploadNow = viewModel::onUploadNow,
                        onSaveForLater = viewModel::onSaveForLater,
                        onChoiceCancelled = viewModel::onChoiceCancelled,
                        onSaveAfterFailedUpload = viewModel::onSaveAfterFailedUpload,
                        onResultShown = viewModel::onResultShown,
                    )
                }
            }
        }
    }

    companion object {
        private const val ArgPosition = "position"
        private const val ArgTotalCount = "totalCount"

        // The host Activity is the one that knows how many fragments make up its wizard and
        // where this one falls in it - both are 1-based (e.g. position 2 of totalCount 5).
        fun newInstance(position: Int, totalCount: Int): SpecificFormFragment =
            SpecificFormFragment().apply {
                arguments = bundleOf(ArgPosition to position, ArgTotalCount to totalCount)
            }
    }
}

@Composable
private fun SpecificFormScreen(
    state: SpecificFormState,
    onRetryLoad: () -> Unit,
    onOpenSaved: (formKey: String) -> Unit,
    onSubmit: (Map<String, FormValue>) -> Unit,
    onUploadNow: () -> Unit,
    onSaveForLater: () -> Unit,
    onChoiceCancelled: () -> Unit,
    onSaveAfterFailedUpload: () -> Unit,
    onResultShown: () -> Unit,
) {
    val snackbarHostState = rememberAppSnackbarHostState()
    val coroutineScope = rememberCoroutineScope()
    val result = state.result
    val message = when (result) {
        SpecificFormResult.Uploaded -> stringResource(R.string.specificform_uploaded)
        SpecificFormResult.SavedForLater -> stringResource(R.string.specificform_saved_for_later)
        is SpecificFormResult.UploadFailed -> stringResource(R.string.specificform_upload_failed, result.message)
        is SpecificFormResult.SaveFailed -> stringResource(R.string.specificform_save_failed, result.message)
        SpecificFormResult.ReopenFailed -> stringResource(R.string.specificform_reopen_failed)
        null -> null
    }
    val saveForLaterAction = stringResource(R.string.specificform_save_for_later)

    // The result is consumed right away and the snackbar shown from a separate coroutine:
    // clearing it inside a LaunchedEffect keyed on it would cancel that effect, and with it the
    // snackbar, the moment it appeared.
    LaunchedEffect(result) {
        if (result == null || message == null) return@LaunchedEffect
        onResultShown()
        coroutineScope.launch {
            if (result is SpecificFormResult.UploadFailed) {
                val action = snackbarHostState.showMessage(message = message, actionLabel = saveForLaterAction)
                if (action == AppSnackbarResult.ActionPerformed) onSaveAfterFailedUpload()
            } else {
                snackbarHostState.showMessage(message = message)
            }
        }
    }

    if (state.pendingValues != null) {
        // "Save for later" is a real second choice here, not a cancel, so it has its own callback;
        // back press or a tap outside cancels and returns to the form.
        AppAlertDialog(
            title = stringResource(R.string.specificform_submit_choice_title),
            message = stringResource(R.string.specificform_submit_choice_message),
            confirmText = stringResource(R.string.specificform_upload_now),
            onConfirm = onUploadNow,
            onDismissRequest = onChoiceCancelled,
            dismissText = saveForLaterAction,
            onDismissClick = onSaveForLater,
        )
    }

    if (state.submitting) {
        AppProgressDialog()
    }

    val form = state.form
    AppScaffold(
        topBar = { scrollBehavior ->
            AppTopBar(
                title = form?.title ?: stringResource(R.string.specificform_default_title),
                actions = if (form == null) {
                    emptyList()
                } else {
                    listOf(
                        AppTopBarAction(
                            icon = Icons.Outlined.Inventory2,
                            contentDescription = stringResource(R.string.specificform_saved_forms),
                            onClick = { onOpenSaved(form.key) },
                        ),
                    )
                },
                scrollBehavior = scrollBehavior,
            )
        },
        snackbarHost = { AppSnackbarHost(snackbarHostState) },
    ) {
        Box(modifier = Modifier.fillMaxSize().background(Color.White)) {
            when {
                // FormRenderer reads initialValues only when it creates its state, so reopening a
                // saved submission recreates it under a new key.
                form != null -> key(state.restoreVersion) {
                    FormRenderer(
                        schema = form.schema,
                        modifier = Modifier.padding(16.dp),
                        initialValues = state.restoredValues,
                        onSubmit = onSubmit,
                    )
                }
                state.isLoading -> AppProgressIndicator(modifier = Modifier.align(Alignment.Center))
                state.error != null -> Column(modifier = Modifier.padding(16.dp)) {
                    AppText(text = state.error)
                    AppButton(text = stringResource(CommonR.string.common_retry_action), onClick = onRetryLoad)
                }
            }
        }
    }
}
