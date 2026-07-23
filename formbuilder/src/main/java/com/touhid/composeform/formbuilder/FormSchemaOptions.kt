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

/**
 * Collects the [optionsUrl] fields among this schema's immediate [FormSchema.fields] only - it does
 * NOT descend into a [FormField.InputBox.pickerScreen]. Each schema level (the main page, or any
 * given pickerScreen) is its own lazy fetch scope, resolved right before that level renders; the
 * caller re-runs this on a pickerScreen's own schema once that picker is actually opened.
 */
fun FormSchema.fieldsWithOptionsUrl(): List<FieldWithOptionsUrl> = fields.mapNotNull { it.optionsUrlOrNull() }

private fun FormField.optionsUrlOrNull(): FieldWithOptionsUrl? = when (this) {
    is FormField.Radio -> optionsUrl?.let { FieldWithOptionsUrl(key, it) }
    is FormField.CheckboxGroup -> optionsUrl?.let { FieldWithOptionsUrl(key, it) }
    is FormField.Dropdown -> optionsUrl?.let { FieldWithOptionsUrl(key, it) }
    else -> null
}
