package com.touhid.composeform.feature.specificform.domain

// A submission kept on the device to upload later.
data class SavedSubmission(
    val id: String,
    // The form's title when it was saved, shown in the saved list.
    val title: String,
    val submission: FormSubmission,
    // The form's values as the ui saved them, used to reopen the form for editing. Opaque here:
    // only the ui (through :formbuilder) reads or writes its shape.
    val formStateJson: String,
    // Epoch millis of the last save.
    val savedAt: Long,
)
