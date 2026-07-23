package com.touhid.composeform

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.touhid.composeform.capture.ImageCaptureScreen
import com.touhid.composeform.designsystem.components.button.AppButton
import com.touhid.composeform.designsystem.components.layout.AppScaffold
import com.touhid.composeform.designsystem.components.surface.AppTopBar
import com.touhid.composeform.designsystem.components.text.AppText
import com.touhid.composeform.designsystem.theme.ComposeFormTheme
import com.touhid.composeform.flow.DemoFormApi
import com.touhid.composeform.flow.FormFlowState
import com.touhid.composeform.flow.FormFlowViewModel
import com.touhid.composeform.formbuilder.FormFieldResult
import com.touhid.composeform.formbuilder.FormImagePickerResult
import com.touhid.composeform.formbuilder.FormRenderer
import com.touhid.composeform.formbuilder.fieldsWithOptionsUrl
import com.touhid.composeform.formbuilder.schema.FormField
import com.touhid.composeform.formbuilder.schema.FormSchema
import com.touhid.composeform.formbuilder.singleAnswerValue
import com.touhid.composeform.formbuilder.withOptions

// Mirrors FormFlowState but scoped to the picker destination, since picker schemas aren't
// ViewModel-backed today. Only :app interprets a field's optionsUrl - FormRenderer never does.
private sealed interface PickerLoadState {
    data object Loading : PickerLoadState
    data class Error(val message: String) : PickerLoadState
    data class Ready(val schema: FormSchema) : PickerLoadState
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ComposeFormTheme {
                val navController = rememberNavController()
                var activePickerKey by rememberSaveable { mutableStateOf<String?>(null) }
                var activePickerSchema by remember { mutableStateOf<FormSchema?>(null) }
                var pendingResult by remember { mutableStateOf<FormFieldResult?>(null) }
                var activeImagePickerKey by rememberSaveable { mutableStateOf<String?>(null) }
                var activeImagePickerField by remember { mutableStateOf<FormField.ImagePicker?>(null) }
                var pendingImagePickerResult by remember { mutableStateOf<FormImagePickerResult?>(null) }

                NavHost(navController = navController, startDestination = "form") {
                    composable("form") {
                        val viewModel: FormFlowViewModel = viewModel()
                        val flowState = viewModel.state

                        LaunchedEffect(pendingResult) {
                            if (pendingResult != null) {
                                activePickerKey = null
                                pendingResult = null
                            }
                        }

                        LaunchedEffect(pendingImagePickerResult) {
                            if (pendingImagePickerResult != null) {
                                activeImagePickerKey = null
                                activeImagePickerField = null
                                pendingImagePickerResult = null
                            }
                        }

                        BackHandler(enabled = viewModel.canGoBack) { viewModel.goBack() }

                        AppScaffold(topBar = { scrollBehavior ->
                            AppTopBar(
                                title = (flowState as? FormFlowState.Page)?.schema?.screenTitle ?: "ComposeForm Demo",
                                scrollBehavior = scrollBehavior,
                                navigationIcon = Icons.AutoMirrored.Filled.ArrowBack,
                                onNavigationClick = { if (!viewModel.goBack()) finish() },
                            )
                        }) {
                            when (flowState) {
                                is FormFlowState.Loading -> {
                                    AppText(text = "Loading…", modifier = Modifier.padding(16.dp))
                                }

                                is FormFlowState.Error -> {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        AppText(text = flowState.message)
                                        AppButton(text = "Retry", onClick = viewModel::retry)
                                    }
                                }

                                is FormFlowState.Page -> {
                                    FormRenderer(
                                        schema = flowState.schema,
                                        modifier = Modifier.padding(16.dp),
                                        initialValues = flowState.initialValues,
                                        pendingResult = pendingResult,
                                        pendingImagePickerResult = pendingImagePickerResult,
                                        onPickerFieldClick = { key, pickerSchema ->
                                            activePickerKey = key
                                            activePickerSchema = pickerSchema
                                            navController.navigate("picker")
                                        },
                                        onImagePickerFieldClick = { key, field ->
                                            activeImagePickerKey = key
                                            activeImagePickerField = field
                                            navController.navigate("imageCapture")
                                        },
                                        onSubmit = viewModel::onPageSubmit,
                                    )
                                }

                                is FormFlowState.Completed -> {
                                    AppText(text = "All done", modifier = Modifier.padding(16.dp))
                                }
                            }
                        }
                    }

                    composable("picker") {
                        val pickerSchema = activePickerSchema
                        if (pickerSchema == null) {
                            LaunchedEffect(Unit) { navController.popBackStack() }
                        } else {
                            var loadState by remember { mutableStateOf<PickerLoadState>(PickerLoadState.Loading) }
                            var retryTrigger by remember { mutableStateOf(0) }

                            LaunchedEffect(retryTrigger) {
                                val fieldsToFetch = pickerSchema.fieldsWithOptionsUrl()
                                if (fieldsToFetch.isEmpty()) {
                                    loadState = PickerLoadState.Ready(pickerSchema)
                                } else {
                                    loadState = PickerLoadState.Loading
                                    runCatching {
                                        fieldsToFetch.fold(pickerSchema) { schema, field ->
                                            schema.withOptions(field.key, DemoFormApi.fetchOptions(field.optionsUrl))
                                        }
                                    }.onSuccess { loadState = PickerLoadState.Ready(it) }
                                        .onFailure { loadState = PickerLoadState.Error(it.message ?: "Failed to load options") }
                                }
                            }

                            AppScaffold(topBar = { scrollBehavior ->
                                AppTopBar(
                                    title = pickerSchema.screenTitle ?: "Select a value",
                                    scrollBehavior = scrollBehavior,
                                    navigationIcon = Icons.AutoMirrored.Filled.ArrowBack,
                                    onNavigationClick = { navController.popBackStack() },
                                )
                            }) {
                                when (val state = loadState) {
                                    is PickerLoadState.Loading -> {
                                        AppText(text = "Loading options…", modifier = Modifier.padding(16.dp))
                                    }

                                    is PickerLoadState.Error -> {
                                        Column(modifier = Modifier.padding(16.dp)) {
                                            AppText(text = state.message)
                                            AppButton(text = "Retry", onClick = { retryTrigger++ })
                                        }
                                    }

                                    is PickerLoadState.Ready -> {
                                        FormRenderer(
                                            schema = state.schema,
                                            modifier = Modifier.padding(16.dp),
                                            onSubmit = { values ->
                                                val result = state.schema.singleAnswerValue(values)
                                                activePickerKey?.let { key -> pendingResult = FormFieldResult(key, result) }
                                                navController.popBackStack()
                                            },
                                        )
                                    }
                                }
                            }
                        }
                    }

                    composable("imageCapture") {
                        val imageField = activeImagePickerField
                        if (imageField == null) {
                            LaunchedEffect(Unit) { navController.popBackStack() }
                        } else {
                            ImageCaptureScreen(
                                field = imageField,
                                onResult = { image ->
                                    activeImagePickerKey?.let { key ->
                                        pendingImagePickerResult = FormImagePickerResult(key, image)
                                    }
                                    navController.popBackStack()
                                },
                                onCancel = { navController.popBackStack() },
                            )
                        }
                    }
                }
            }
        }
    }
}
