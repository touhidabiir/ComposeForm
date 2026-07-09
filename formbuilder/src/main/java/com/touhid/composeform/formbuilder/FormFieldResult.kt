package com.touhid.composeform.formbuilder

import com.touhid.composeform.formbuilder.schema.FormField
import com.touhid.composeform.formbuilder.schema.FormSchema
import com.touhid.composeform.formbuilder.schema.FormValue

data class FormFieldResult(val key: String, val value: String)

fun FormSchema.singleAnswerValue(values: Map<String, FormValue>): String {
    val key = fields.firstOrNull { it !is FormField.Text && it !is FormField.Submit }?.key
    return key?.let { values[it] }.toDisplayString()
}

private fun FormValue?.toDisplayString(): String = when (this) {
    is FormValue.Text -> value
    is FormValue.Option -> value
    is FormValue.Options -> selected.joinToString(", ") { it.value }
    null -> ""
}
