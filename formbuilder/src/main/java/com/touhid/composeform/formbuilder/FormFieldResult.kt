package com.touhid.composeform.formbuilder

import com.touhid.composeform.formbuilder.schema.FormField
import com.touhid.composeform.formbuilder.schema.FormSchema
import com.touhid.composeform.formbuilder.schema.FormValue

data class FormFieldResult(val key: String, val value: String)

data class FormImagePickerResult(val key: String, val value: FormValue.Image)

fun FormSchema.singleAnswerValue(values: Map<String, FormValue>): String {
    val key = fields.firstOrNull { it !is FormField.Text && it !is FormField.Submit }?.key
    return key?.let { values[it] }?.toPlainString().orEmpty()
}

fun FormValue.toPlainString(): String = when (this) {
    is FormValue.Text -> value
    is FormValue.Option -> value
    is FormValue.Options -> selected.joinToString(", ") { it.value }
    is FormValue.Image -> url
}

fun Map<String, FormValue>.toPlainValues(): Map<String, String> = mapValues { it.value.toPlainString() }

/** Like [toPlainString], but for option-based fields uses the option's [FormValue.Option.id] instead of its display value. */
fun FormValue.toKeyString(): String = when (this) {
    is FormValue.Text -> value
    is FormValue.Option -> id
    is FormValue.Options -> selected.joinToString(",") { it.id }
    is FormValue.Image -> url
}

fun Map<String, FormValue>.toKeyValues(): Map<String, String> = mapValues { it.value.toKeyString() }
