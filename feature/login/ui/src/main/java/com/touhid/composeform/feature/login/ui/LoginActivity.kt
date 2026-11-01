package com.touhid.composeform.feature.login.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.touhid.composeform.designsystem.theme.ComposeFormAppTheme
import com.touhid.composeform.feature.login.ui.otp.OtpScreen
import com.touhid.composeform.feature.login.ui.otp.OtpViewModel
import com.touhid.composeform.feature.login.ui.phone.PhoneLoginScreen
import dagger.hilt.android.AndroidEntryPoint

private const val PhoneRoute = "phone"
private const val OtpRoute = "otp/{${OtpViewModel.ArgPhoneNumber}}"

// The sign-in flow's host Activity, opened through :feature:login:navigation's OpenLogin. Phone
// number, then OTP; once the OTP is verified (and the session stored) it finishes with
// RESULT_OK. Backing out of the phone step finishes with RESULT_CANCELED.
//
// The long @AndroidEntryPoint form: only :app applies the Hilt Gradle plugin (see LeadActivity).
@AndroidEntryPoint(ComponentActivity::class)
class LoginActivity : Hilt_LoginActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ComposeFormAppTheme {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = PhoneRoute) {
                    composable(PhoneRoute) {
                        PhoneLoginScreen(
                            onOtpSent = { phoneNumber -> navController.navigate("otp/$phoneNumber") },
                        )
                    }
                    composable(
                        route = OtpRoute,
                        arguments = listOf(navArgument(OtpViewModel.ArgPhoneNumber) { type = NavType.StringType }),
                    ) {
                        OtpScreen(
                            onSignedIn = {
                                setResult(Activity.RESULT_OK)
                                finish()
                            },
                        )
                    }
                }
            }
        }
    }

    companion object {
        fun intent(context: Context): Intent = Intent(context, LoginActivity::class.java)
    }
}
