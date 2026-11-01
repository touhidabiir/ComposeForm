package com.touhid.composeform.feature.specificform.data

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import com.touhid.composeform.feature.specificform.data.local.SavedSubmissionEntity
import com.touhid.composeform.feature.specificform.domain.FormSubmission
import com.touhid.composeform.feature.specificform.domain.SavedSubmission
import com.touhid.composeform.feature.specificform.domain.SpecificForm

internal fun SpecificFormPayloadDto.toDomain() = SpecificForm(
    key = key,
    title = title,
    schemaJson = JsonObject().apply { add("questions", questions) }.toString(),
)

internal fun FormSubmission.toRequestDto() = SpecificFormSubmitRequestDto(formKey = formKey, answers = answers)

private val gson = Gson()
private val answersType = object : TypeToken<Map<String, String>>() {}.type

internal fun SavedSubmissionEntity.toDomain() = SavedSubmission(
    id = id,
    title = title,
    submission = FormSubmission(formKey = formKey, answers = gson.fromJson(answersJson, answersType)),
    formStateJson = formStateJson,
    savedAt = savedAt,
)

internal fun FormSubmission.toEntity(id: String, title: String, formStateJson: String, savedAt: Long) =
    SavedSubmissionEntity(
        id = id,
        formKey = formKey,
        title = title,
        answersJson = gson.toJson(answers),
        formStateJson = formStateJson,
        savedAt = savedAt,
    )
