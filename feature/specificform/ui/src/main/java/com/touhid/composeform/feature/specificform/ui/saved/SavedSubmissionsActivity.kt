package com.touhid.composeform.feature.specificform.ui.saved

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.touhid.composeform.designsystem.theme.ComposeFormAppTheme
import dagger.hilt.android.AndroidEntryPoint

// Hosts the saved list, opened from the form's toolbar through OpenSavedSubmissions. Tapping a
// saved submission finishes with its id, so the form can reopen it for editing.
//
// The long @AndroidEntryPoint(ComponentActivity::class) form, extending the generated
// Hilt_SavedSubmissionsActivity directly: only :app applies the Hilt Gradle plugin, and that
// plugin's bytecode rewrite is what lets the short form (`: ComponentActivity()`) work.
@AndroidEntryPoint(ComponentActivity::class)
class SavedSubmissionsActivity : Hilt_SavedSubmissionsActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ComposeFormAppTheme {
                SavedSubmissionsScreen(
                    onBack = ::finish,
                    onOpen = { id ->
                        setResult(Activity.RESULT_OK, Intent().putExtra(OpenSavedSubmissions.ExtraSavedId, id))
                        finish()
                    },
                )
            }
        }
    }
}
