package com.touhid.composeform.home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.touhid.composeform.designsystem.theme.ComposeFormAppTheme
import com.touhid.composeform.feature.acquisition.navigation.AcquisitionApprovalListDestination
import com.touhid.composeform.navigation.Navigator
import com.touhid.composeform.session.SessionGuard
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

// Home for UserType.Approver. Started by the splash (MainActivity), which then finishes, so Back
// here leaves the app. Log out goes through SessionGuard, which restarts the app at the splash.
@AndroidEntryPoint
class ApproverHomeActivity : ComponentActivity() {

    @Inject
    lateinit var navigator: Navigator

    @Inject
    lateinit var sessionGuard: SessionGuard

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ComposeFormAppTheme {
                ApproverHomeScreen(
                    onOpenAcquisitionApproval = { navigator.navigate(AcquisitionApprovalListDestination) },
                    onLogout = sessionGuard::signOut,
                )
            }
        }
    }

    companion object {
        fun intent(context: Context): Intent = Intent(context, ApproverHomeActivity::class.java)
    }
}
