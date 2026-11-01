package com.touhid.composeform.feature.specificform.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "saved_submission")
internal data class SavedSubmissionEntity(
    // A UUID, generated when the submission is first saved.
    @PrimaryKey val id: String,
    @ColumnInfo(name = "form_key") val formKey: String,
    val title: String,
    // FormSubmission.answers as a JSON object: exactly what gets uploaded.
    @ColumnInfo(name = "answers_json") val answersJson: String,
    // The ui's opaque form state, used to reopen the form for editing.
    @ColumnInfo(name = "form_state_json") val formStateJson: String,
    @ColumnInfo(name = "saved_at") val savedAt: Long,
)
