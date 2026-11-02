package com.touhid.composeform

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import com.touhid.composeform.designsystem.components.button.AppButton
import com.touhid.composeform.designsystem.components.layout.AppScaffold
import com.touhid.composeform.designsystem.components.surface.AppAlertDialog
import com.touhid.composeform.designsystem.components.surface.AppTopBar
import com.touhid.composeform.designsystem.components.text.AppText
import com.touhid.composeform.designsystem.theme.ComposeFormAppTheme
import com.touhid.composeform.feature.acquisition.navigation.AcquisitionApprovalListDestination
import com.touhid.composeform.feature.lead.navigation.LeadDashboardDestination
import com.touhid.composeform.feature.login.navigation.LoginIntentFactory
import com.touhid.composeform.feature.login.navigation.OpenLogin
import com.touhid.composeform.feature.specificform.navigation.SpecificFormDestination
import com.touhid.composeform.flow.DemoFormApi
import com.touhid.composeform.flow.FormFlowState
import com.touhid.composeform.flow.FormFlowViewModel
import com.touhid.composeform.formbuilder.FormFieldResult
import com.touhid.composeform.formbuilder.FormRenderer
import com.touhid.composeform.formbuilder.fieldsWithOptionsUrl
import com.touhid.composeform.formbuilder.schema.FormSchema
import com.touhid.composeform.formbuilder.singleAnswerValue
import com.touhid.composeform.formbuilder.withOptions
import com.touhid.composeform.home.AgentHomeScreen
import com.touhid.composeform.home.ApproverHomeScreen
import com.touhid.composeform.navigation.Navigator
import com.touhid.composeform.session.SessionEndReason
import com.touhid.composeform.session.SessionGuard
import com.touhid.composeform.session.domain.UserType
import com.touhid.composeform.splash.SplashScreen
import com.touhid.composeform.splash.SplashTarget
import com.touhid.composeform.splash.SplashViewModel
import javax.inject.Inject

// Mirrors FormFlowState but scoped to the picker destination, since picker schemas aren't
// ViewModel-backed today. Only :app interprets a field's optionsUrl - FormRenderer never does.
private sealed interface PickerLoadState {
    data object Loading : PickerLoadState
    data class Error(val message: String) : PickerLoadState
    data class Ready(val schema: FormSchema) : PickerLoadState
}

// One home per user type. Exhaustive on purpose: a new UserType doesn't compile until it has a home.
private fun homeRoute(userType: UserType): String = when (userType) {
    UserType.Approver -> "home/approver"
    UserType.Agent -> "home/agent"
}

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    // Opens other features' screens (each in its own host Activity) by Destination.
    @Inject
    lateinit var navigator: Navigator

    // Builds the sign-in Activity's Intent for OpenLogin; bound by :feature:login:ui.
    @Inject
    lateinit var loginIntentFactory: LoginIntentFactory

    // Ends the session (Log out) and restarts the app here.
    @Inject
    lateinit var sessionGuard: SessionGuard

    // Set when SessionGuard restarted the app because a session ended.
    private fun sessionEndReason(): SessionEndReason? =
        intent.getStringExtra(SessionGuard.EXTRA_SESSION_END)
            ?.let { extra -> SessionEndReason.entries.firstOrNull { it.name == extra } }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ComposeFormAppTheme {
                val navController = rememberNavController()
                var activePickerKey by rememberSaveable { mutableStateOf<String?>(null) }
                var activePickerSchema by remember { mutableStateOf<FormSchema?>(null) }
                var pendingResult by remember { mutableStateOf<FormFieldResult?>(null) }

                NavHost(navController = navController, startDestination = "splash") {
                    // Launch screen, and where every ended session comes back to (SessionGuard
                    // restarts the app here). After the splash: the home for the signed-in user's
                    // type, or sign-in. Sign-in runs as its own Activity on top of this one;
                    // signing in shows the home, backing out of it closes the app.
                    composable("splash") {
                        val viewModel: SplashViewModel = hiltViewModel()
                        val target by viewModel.target.collectAsStateWithLifecycle()
                        // Saved, so a recreated Activity waiting on sign-in's result doesn't
                        // open sign-in a second time.
                        var loginOpened by rememberSaveable { mutableStateOf(false) }
                        var expiryAcknowledged by rememberSaveable { mutableStateOf(false) }
                        val loginLauncher = rememberLauncherForActivityResult(
                            contract = remember { OpenLogin(loginIntentFactory) },
                        ) { signedIn ->
                            if (signedIn) viewModel.onSignedIn() else finish()
                        }

                        LaunchedEffect(Unit) { viewModel.start(sessionEndReason()) }
                        LaunchedEffect(target, expiryAcknowledged) {
                            when (val current = target) {
                                is SplashTarget.Home -> navController.navigate(homeRoute(current.userType)) {
                                    popUpTo("splash") { inclusive = true }
                                }
                                is SplashTarget.Login -> {
                                    val waitingOnNotice = current.sessionExpired && !expiryAcknowledged
                                    if (!loginOpened && !waitingOnNotice) {
                                        loginOpened = true
                                        loginLauncher.launch(Unit)
                                    }
                                }
                                null -> Unit
                            }
                        }
                        SplashScreen()

                        val current = target
                        if (current is SplashTarget.Login && current.sessionExpired && !expiryAcknowledged) {
                            AppAlertDialog(
                                title = stringResource(R.string.session_expired_title),
                                message = stringResource(R.string.session_expired_message),
                                confirmText = stringResource(R.string.session_expired_ok),
                                onConfirm = { expiryAcknowledged = true },
                                onDismissRequest = {},
                                dismissible = false,
                            )
                        }
                    }

                    composable(homeRoute(UserType.Approver)) {
                        ApproverHomeScreen(
                            onOpenAcquisitionApproval = { navigator.navigate(AcquisitionApprovalListDestination) },
                            onLogout = sessionGuard::signOut,
                        )
                    }

                    composable(homeRoute(UserType.Agent)) {
                        AgentHomeScreen(
                            onOpenFormDemo = { navController.navigate("form") },
                            onOpenLeadDashboard = { navigator.navigate(LeadDashboardDestination) },
                            onOpenSpecificForm = { navigator.navigate(SpecificFormDestination) },
                            onLogout = sessionGuard::signOut,
                        )
                    }

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
                                onNavigationClick = { if (!viewModel.goBack()) navController.popBackStack() },
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
                }
            }
        }
    }
}
