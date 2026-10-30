package com.touhid.composeform.feature.specificform.data

import com.google.gson.JsonElement
import com.google.gson.annotations.SerializedName

// questions is left as a raw JsonElement rather than modeled field-by-field, since :formbuilder's
// parseSpecificFormSchema already parses that exact "questions" shape - no need for a second,
// duplicate model of it here. toDomain() re-serializes it for the domain model.
internal data class SpecificFormResponseDto(
    val data: SpecificFormPayloadDto,
)

internal data class SpecificFormPayloadDto(
    val key: String,
    val title: String,
    val questions: JsonElement,
)

internal data class SpecificFormSubmitRequestDto(
    @SerializedName("form_key") val formKey: String,
    val answers: Map<String, String>,
)

internal data class SpecificFormSubmitResponseDto(
    @SerializedName("is_error") val isError: Boolean,
    val message: String,
)
