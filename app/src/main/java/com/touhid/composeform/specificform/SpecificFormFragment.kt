package com.touhid.composeform.specificform

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.touhid.composeform.ComposeFormAppTheme
import com.touhid.composeform.designsystem.components.button.AppButton
import com.touhid.composeform.designsystem.components.layout.AppScaffold
import com.touhid.composeform.designsystem.components.surface.AppTopBar
import com.touhid.composeform.designsystem.components.text.AppText
import com.touhid.composeform.formbuilder.FormRenderer
import dagger.hilt.android.AndroidEntryPoint

// Not wired into any Activity/NavHost by this module - intended to be one of several fragments a
// host Activity swaps through (a form wizard), created via newInstance() with this fragment's
// position among its siblings. Fetches its form schema from AppRepository.getSpecificForm()
// (backed by the v1/forms/specific mock while no real backend exists) and renders it with
// FormRenderer; the submit button logs the schema's single answer value rather than calling a
// submit endpoint.
@AndroidEntryPoint
class SpecificFormFragment : Fragment() {

    private val viewModel: SpecificFormViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val position = arguments?.getInt(ArgPosition, 1) ?: 1
        val totalCount = arguments?.getInt(ArgTotalCount, 1) ?: 1
        return ComposeView(requireContext()).apply {
            setContent {
                ComposeFormAppTheme {
                    LaunchedEffect(Unit) { viewModel.onScreenStart(position, totalCount) }

                    val state by viewModel.state.collectAsStateWithLifecycle()
                    val title = (state as? SpecificFormUiState.Ready)?.title ?: "Specific Form"

                    AppScaffold(topBar = { scrollBehavior ->
                        AppTopBar(title = title, scrollBehavior = scrollBehavior)
                    }) {
                        Box(modifier = Modifier.fillMaxSize().background(Color.White)) {
                            when (val current = state) {
                                is SpecificFormUiState.Loading -> {
                                    AppText(text = "Loading…", modifier = Modifier.padding(16.dp))
                                }

                                is SpecificFormUiState.Error -> {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        AppText(text = current.message)
                                        AppButton(text = "Retry", onClick = viewModel::retry)
                                    }
                                }

                                is SpecificFormUiState.Ready -> {
                                    FormRenderer(
                                        schema = current.schema,
                                        modifier = Modifier.padding(16.dp),
                                        onSubmit = viewModel::onSubmit,
                                    )
                                }
                            }
                        }
                    }
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
