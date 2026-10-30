package com.touhid.composeform.feature.specificform.domain

data class FormSubmission(
    // SpecificForm.key of the form these answers belong to.
    val formKey: String,
    // Question key -> answer. For a choice question the answer is the chosen option's key, not its
    // display text, so the backend gets stable ids whatever language the form was shown in.
    val answers: Map<String, String>,
)
