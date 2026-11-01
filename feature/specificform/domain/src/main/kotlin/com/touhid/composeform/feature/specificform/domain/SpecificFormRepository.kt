package com.touhid.composeform.feature.specificform.domain

import com.touhid.composeform.model.AppResult
import kotlinx.coroutines.flow.Flow

interface SpecificFormRepository {

    suspend fun getSpecificForm(): AppResult<SpecificForm>

    suspend fun submit(submission: FormSubmission): AppResult<Unit>

    // Saves on the device, replacing the saved submission with this id when one is given, and
    // returns the id it was saved under.
    suspend fun saveForLater(
        id: String?,
        title: String,
        submission: FormSubmission,
        formStateJson: String,
    ): AppResult<String>

    // Newest first. Emits again whenever a submission for this form is saved, uploaded or deleted.
    fun observeSavedSubmissions(formKey: String): Flow<List<SavedSubmission>>

    // Null when no submission has this id (e.g. it was uploaded or deleted meanwhile).
    suspend fun getSavedSubmission(id: String): AppResult<SavedSubmission?>

    // Uploads a saved submission as it was saved, and removes it from the device once the server
    // has accepted it. A failed upload keeps it.
    suspend fun uploadSaved(id: String): AppResult<Unit>

    suspend fun deleteSaved(id: String): AppResult<Unit>
}
