package com.touhid.composeform.feature.specificform.ui.saved

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract

// Opens the saved list for one form (input: its form key). The result is the id of the saved
// submission the user tapped to reopen, or null when they just went back.
internal class OpenSavedSubmissions : ActivityResultContract<String, String?>() {

    override fun createIntent(context: Context, input: String): Intent =
        Intent(context, SavedSubmissionsActivity::class.java).putExtra(ExtraFormKey, input)

    override fun parseResult(resultCode: Int, intent: Intent?): String? =
        if (resultCode == Activity.RESULT_OK) intent?.getStringExtra(ExtraSavedId) else null

    companion object {
        // SavedSubmissionsViewModel reads the form key from its SavedStateHandle by this name.
        const val ExtraFormKey = "formKey"
        const val ExtraSavedId = "savedId"
    }
}
