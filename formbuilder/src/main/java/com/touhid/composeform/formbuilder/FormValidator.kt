package com.touhid.composeform.formbuilder

import com.touhid.composeform.formbuilder.schema.FormField
import com.touhid.composeform.formbuilder.schema.FormSchema
import com.touhid.composeform.formbuilder.schema.FormValue

internal fun validate(schema: FormSchema, values: Map<String, FormValue>): Map<String, String> {
    val errors = mutableMapOf<String, String>()
    for (field in schema.fields) {
        if (!field.isVisible(values)) continue
        val message = validateField(field, values[field.key])
        if (message != null) errors[field.key] = message
    }
    return errors
}

private fun validateField(field: FormField, value: FormValue?): String? = when (field) {
    is FormField.Text -> null
    is FormField.Submit -> null

    is FormField.InputBox -> {
        val text = (value as? FormValue.Text)?.value.orEmpty()
        when {
            field.required && text.isBlank() -> field.errorMessage ?: "This field is required"
            field.minLength != null && text.length < field.minLength -> field.errorMessage ?: "Must be at least ${field.minLength} characters"
            field.maxLength != null && text.length > field.maxLength -> field.errorMessage ?: "Must be at most ${field.maxLength} characters"
            field.pattern != null && text.isNotEmpty() && !Regex(field.pattern).matches(text) -> field.errorMessage ?: "Invalid format"
            else -> null
        }
    }

    is FormField.Checkbox -> {
        val checked = (value as? FormValue.Text)?.value.toBoolean()
        if (field.required && !checked) "This field is required" else null
    }

    is FormField.Switch -> {
        val checked = (value as? FormValue.Text)?.value.toBoolean()
        if (field.required && !checked) "This field is required" else null
    }

    is FormField.Radio -> {
        val selected = value as? FormValue.Option
        if (field.required && selected == null) "Please select an option" else null
    }

    is FormField.Dropdown -> {
        val selected = value as? FormValue.Option
        if (field.required && selected == null) "Please select an option" else null
    }

    is FormField.CheckboxGroup -> {
        val selected = (value as? FormValue.Options)?.selected.orEmpty()
        if (field.required && selected.isEmpty()) "Please select at least one option" else null
    }
}
