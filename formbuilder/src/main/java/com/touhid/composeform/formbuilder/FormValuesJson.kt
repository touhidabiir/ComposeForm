package com.touhid.composeform.formbuilder

import com.touhid.composeform.formbuilder.schema.FormValue
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

// A form's current values as an opaque string, for callers that store an unfinished form (e.g. a
// local "save for later" copy) and later reopen it through FormRenderer's initialValues. Keeps
// option display text as well as ids, so the reopened form shows exactly what was entered.
// Callers treat the string as opaque: only these two functions read or write its shape.

private val formValuesJson = Json { ignoreUnknownKeys = true }

fun encodeFormValues(values: Map<String, FormValue>): String = formValuesJson.encodeToString(values)

fun decodeFormValues(raw: String): Map<String, FormValue> = formValuesJson.decodeFromString(raw)
