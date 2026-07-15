package com.touhid.composeform.formbuilder

import com.touhid.composeform.formbuilder.schema.FormField
import com.touhid.composeform.formbuilder.schema.FormOption
import com.touhid.composeform.formbuilder.schema.FormSchema

/**
 * Appends [options] to the field matching [key] (recursing into nested [FormField.InputBox.pickerScreen]s).
 * Pure data transformation - no networking. The app module fetches options and calls this to merge them in.
 */
fun FormSchema.withOptions(key: String, options: List<FormOption>): FormSchema =
    copy(fields = fields.map { it.withOptions(key, options) })

private fun FormField.withOptions(key: String, options: List<FormOption>): FormField = when (this) {
    is FormField.InputBox -> copy(pickerScreen = pickerScreen?.withOptions(key, options))
    is FormField.Radio -> if (this.key == key) copy(options = this.options + options) else this
    is FormField.CheckboxGroup -> if (this.key == key) copy(options = this.options + options) else this
    is FormField.Dropdown -> if (this.key == key) copy(options = this.options + options) else this
    else -> this
}

data class FieldWithOptionsUrl(val key: String, val optionsUrl: String)

/** Recursively collects every field (including inside nested pickerScreens) that declares an [optionsUrl]. */
fun FormSchema.fieldsWithOptionsUrl(): List<FieldWithOptionsUrl> = fields.flatMap { it.fieldsWithOptionsUrl() }

private fun FormField.fieldsWithOptionsUrl(): List<FieldWithOptionsUrl> {
    val self = when (this) {
        is FormField.Radio -> optionsUrl?.let { listOf(FieldWithOptionsUrl(key, it)) }
        is FormField.CheckboxGroup -> optionsUrl?.let { listOf(FieldWithOptionsUrl(key, it)) }
        is FormField.Dropdown -> optionsUrl?.let { listOf(FieldWithOptionsUrl(key, it)) }
        else -> null
    }.orEmpty()
    val nested = (this as? FormField.InputBox)?.pickerScreen?.fieldsWithOptionsUrl().orEmpty()
    return self + nested
}
