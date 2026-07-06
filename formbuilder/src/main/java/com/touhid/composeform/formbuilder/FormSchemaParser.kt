package com.touhid.composeform.formbuilder

import com.touhid.composeform.formbuilder.schema.FormSchema
import kotlinx.serialization.json.Json

private val json = Json { ignoreUnknownKeys = true }

fun parseFormSchema(jsonString: String): FormSchema = json.decodeFromString(jsonString)
