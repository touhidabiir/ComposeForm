package com.touhid.composeform

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.hilt.navigation.compose.hiltViewModel
import com.touhid.composeform.designsystem.components.surface.AppAlertDialog
import com.touhid.composeform.designsystem.theme.ComposeFormAppTheme
import com.touhid.composeform.feature.login.navigation.LoginIntentFactory
import com.touhid.composeform.feature.login.navigation.OpenLogin
import com.touhid.composeform.home.AgentHomeActivity
import com.touhid.composeform.home.ApproverHomeActivity
import com.touhid.composeform.session.SessionEndReason
import com.touhid.composeform.session.SessionGuard
import com.touhid.composeform.session.domain.UserType
import com.touhid.composeform.splash.SplashScreen
import com.touhid.composeform.splash.SplashTarget
import com.touhid.composeform.splash.SplashViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

// The launcher: the splash, and where every ended session comes back to (SessionGuard restarts
// the app here). It decides where the app goes and then gets out of the way:
// - signed in: starts that user type's home Activity and finishes, so Back on the home leaves the app;
// - not signed in: opens sign-in (its own Activity, on top of this one) and waits for the answer.
//   Signed in: same as above. Backed out: finishes, closing the app.
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    // Builds the sign-in Activity's Intent for OpenLogin; bound by :feature:login:ui.
    @Inject
    lateinit var loginIntentFactory: LoginIntentFactory

    // Set when SessionGuard restarted the app because a session ended.
    private fun sessionEndReason(): SessionEndReason? =
        intent.getStringExtra(SessionGuard.EXTRA_SESSION_END)
            ?.let { extra -> SessionEndReason.entries.firstOrNull { it.name == extra } }

    // One home Activity per user type. Exhaustive on purpose: a new UserType doesn't compile
    // until it has a home.
    private fun homeIntent(userType: UserType): Intent = when (userType) {
        UserType.Approver -> ApproverHomeActivity.intent(this)
        UserType.Agent -> AgentHomeActivity.intent(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ComposeFormAppTheme {
                val viewModel: SplashViewModel = hiltViewModel()
                val target by viewModel.target.collectAsStateWithLifecycle()
                // Saved, so a recreated Activity waiting on sign-in's result doesn't open
                // sign-in a second time.
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
                        is SplashTarget.Home -> {
                            startActivity(homeIntent(current.userType))
                            finish()
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
        }
    }
}
