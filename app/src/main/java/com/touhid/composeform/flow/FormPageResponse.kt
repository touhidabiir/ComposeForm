package com.touhid.composeform.flow

import com.touhid.composeform.formbuilder.schema.FormSchema
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class FormPageResponse(
    val schema: FormSchema,
    val nextFormUrl: String? = null,
    val submitUrl: String? = null,
)

private val json = Json { ignoreUnknownKeys = true }

fun parseFormPageResponse(jsonString: String): FormPageResponse = json.decodeFromString(jsonString)
