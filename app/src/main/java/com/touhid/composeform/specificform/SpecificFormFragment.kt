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

// Standalone - not hosted by any Activity/NavHost yet. Fetches its form schema from
// AppRepository.getSpecificForm() (backed by the v1/forms/specific mock while no real backend
// exists) and renders it with FormRenderer; the submit button logs the schema's single answer
// value rather than calling a submit endpoint.
@AndroidEntryPoint
class SpecificFormFragment : Fragment() {

    private val viewModel: SpecificFormViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return ComposeView(requireContext()).apply {
            setContent {
                ComposeFormAppTheme {
                    LaunchedEffect(Unit) { viewModel.onScreenStart() }

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
}
