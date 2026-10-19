package com.touhid.composeform.feature.specificform.domain

data class SpecificForm(
    val key: String,
    val title: String,
    // The form's questions as a {"questions": [...]} JSON string - exactly the shape
    // :formbuilder's parseSpecificFormSchema reads. Kept as a string so this domain module (and
    // the ui above it) never sees a JSON library type.
    val schemaJson: String,
)
