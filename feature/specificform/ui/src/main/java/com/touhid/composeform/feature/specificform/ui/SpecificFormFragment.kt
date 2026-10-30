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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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
import com.touhid.composeform.designsystem.components.surface.AppProgressDialog
import com.touhid.composeform.designsystem.components.surface.AppProgressIndicator
import com.touhid.composeform.designsystem.components.surface.AppSnackbarHost
import com.touhid.composeform.designsystem.components.surface.AppSnackbarResult
import com.touhid.composeform.designsystem.components.surface.AppTopBar
import com.touhid.composeform.designsystem.components.surface.rememberAppSnackbarHostState
import com.touhid.composeform.designsystem.components.text.AppText
import com.touhid.composeform.designsystem.theme.ComposeFormAppTheme
import com.touhid.composeform.formbuilder.FormRenderer
import com.touhid.composeform.formbuilder.schema.FormValue
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

// Not wired into any Activity/NavHost by this module - intended to be one of several fragments a
// host Activity swaps through (a form wizard), created via newInstance() with this fragment's
// position among its siblings. Fetches its form schema from SpecificFormRepository.getSpecificForm()
// (backed by the v1/forms/specific mock while no real backend exists), renders it with
// FormRenderer, and uploads the answers on submit (v1/forms/specific/submit, also mocked).
//
// The long @AndroidEntryPoint(Fragment::class) form, extending the generated
// Hilt_SpecificFormFragment directly: only :app applies the Hilt Gradle plugin, and that plugin's
// bytecode rewrite is what lets the short form (`: Fragment()`) work.
@AndroidEntryPoint(Fragment::class)
class SpecificFormFragment : Hilt_SpecificFormFragment() {

    private val viewModel: SpecificFormViewModel by viewModels()

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
                        onSubmit = viewModel::onSubmit,
                        onRetrySubmit = viewModel::retrySubmit,
                        onSubmitResultShown = viewModel::onSubmitResultShown,
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
    onSubmit: (Map<String, FormValue>) -> Unit,
    onRetrySubmit: () -> Unit,
    onSubmitResultShown: () -> Unit,
) {
    val snackbarHostState = rememberAppSnackbarHostState()
    val coroutineScope = rememberCoroutineScope()
    val uploadedMessage = stringResource(R.string.specificform_uploaded)
    val retryAction = stringResource(CommonR.string.common_retry_action)
    val failedMessage = state.submitError?.let { stringResource(R.string.specificform_upload_failed, it) }

    // The result is consumed right away and the snackbar shown from a separate coroutine:
    // clearing it inside a LaunchedEffect keyed on it would cancel that effect, and with it the
    // snackbar, the moment it appeared.
    LaunchedEffect(state.submitted, failedMessage) {
        when {
            state.submitted -> {
                onSubmitResultShown()
                coroutineScope.launch { snackbarHostState.showMessage(message = uploadedMessage) }
            }
            failedMessage != null -> {
                onSubmitResultShown()
                coroutineScope.launch {
                    val result = snackbarHostState.showMessage(message = failedMessage, actionLabel = retryAction)
                    if (result == AppSnackbarResult.ActionPerformed) onRetrySubmit()
                }
            }
        }
    }

    if (state.submitting) {
        AppProgressDialog()
    }

    AppScaffold(
        topBar = { scrollBehavior ->
            AppTopBar(
                title = state.form?.title ?: stringResource(R.string.specificform_default_title),
                scrollBehavior = scrollBehavior,
            )
        },
        snackbarHost = { AppSnackbarHost(snackbarHostState) },
    ) {
        Box(modifier = Modifier.fillMaxSize().background(Color.White)) {
            when {
                state.form != null -> FormRenderer(
                    schema = state.form.schema,
                    modifier = Modifier.padding(16.dp),
                    onSubmit = onSubmit,
                )
                state.isLoading -> AppProgressIndicator(modifier = Modifier.align(Alignment.Center))
                state.error != null -> Column(modifier = Modifier.padding(16.dp)) {
                    AppText(text = state.error)
                    AppButton(text = retryAction, onClick = onRetryLoad)
                }
            }
        }
    }
}
