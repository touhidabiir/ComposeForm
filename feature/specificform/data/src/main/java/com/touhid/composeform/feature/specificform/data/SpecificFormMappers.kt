package com.touhid.composeform.feature.specificform.data

import com.google.gson.JsonObject
import com.touhid.composeform.feature.specificform.domain.FormSubmission
import com.touhid.composeform.feature.specificform.domain.SpecificForm

internal fun SpecificFormPayloadDto.toDomain() = SpecificForm(
    key = key,
    title = title,
    schemaJson = JsonObject().apply { add("questions", questions) }.toString(),
)

internal fun FormSubmission.toRequestDto() = SpecificFormSubmitRequestDto(formKey = formKey, answers = answers)
