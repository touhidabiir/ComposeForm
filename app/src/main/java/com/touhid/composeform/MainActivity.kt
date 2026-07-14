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
import com.touhid.composeform.designsystem.components.button.AppButton
import com.touhid.composeform.designsystem.components.layout.AppScaffold
import com.touhid.composeform.designsystem.components.surface.AppTopBar
import com.touhid.composeform.designsystem.components.text.AppText
import com.touhid.composeform.designsystem.theme.ComposeFormTheme
import com.touhid.composeform.flow.FormFlowState
import com.touhid.composeform.flow.FormFlowViewModel
import com.touhid.composeform.formbuilder.FormFieldResult
import com.touhid.composeform.formbuilder.FormRenderer
import com.touhid.composeform.formbuilder.schema.FormSchema
import com.touhid.composeform.formbuilder.singleAnswerValue

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
                                        onPickerFieldClick = { key, pickerSchema ->
                                            activePickerKey = key
                                            activePickerSchema = pickerSchema
                                            navController.navigate("picker")
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
                            AppScaffold(topBar = { scrollBehavior ->
                                AppTopBar(
                                    title = pickerSchema.screenTitle ?: "Select a value",
                                    scrollBehavior = scrollBehavior,
                                    navigationIcon = Icons.AutoMirrored.Filled.ArrowBack,
                                    onNavigationClick = { navController.popBackStack() },
                                )
                            }) {
                                FormRenderer(
                                    schema = pickerSchema,
                                    modifier = Modifier.padding(16.dp),
                                    onSubmit = { values ->
                                        val result = pickerSchema.singleAnswerValue(values)
                                        activePickerKey?.let { key -> pendingResult = FormFieldResult(key, result) }
                                        navController.popBackStack()
                                    },
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
