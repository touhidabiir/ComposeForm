package com.touhid.composeform.feature.lead.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.touhid.composeform.designsystem.theme.ComposeFormAppTheme
import dagger.hilt.android.AndroidEntryPoint

// The lead feature's host Activity: the entry point LeadDashboardHandler opens. Lead has a single
// screen today, so no NavHost yet; add one here once there's a second lead screen to move to.
//
// The long @AndroidEntryPoint(ComponentActivity::class) form, extending the generated
// Hilt_LeadActivity directly: only :app applies the Hilt Gradle plugin, and that plugin's bytecode
// rewrite is what lets the short form (`: ComponentActivity()`) work.
@AndroidEntryPoint(ComponentActivity::class)
class LeadActivity : Hilt_LeadActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ComposeFormAppTheme {
                LeadDashboardScreen(onBack = ::finish)
            }
        }
    }

    companion object {
        fun intent(context: Context): Intent = Intent(context, LeadActivity::class.java)
    }
}
