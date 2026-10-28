package com.touhid.composeform.network.model

import com.google.gson.JsonElement

// questions is left as a raw JsonElement (re-serialized by the caller) rather than modeled
// field-by-field here, since :formbuilder's parseSpecificFormSchema already parses that exact
// "questions" shape from a JSON string - no need for a second, duplicate model of it in :network.
data class SpecificFormResponse(
    val data: SpecificFormPayload,
)

data class SpecificFormPayload(
    val key: String,
    val title: String,
    val questions: JsonElement,
)
