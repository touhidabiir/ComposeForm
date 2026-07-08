package com.touhid.composeform.formbuilder.schema

import kotlinx.serialization.Serializable

@Serializable
data class FormOption(
    val id: String,
    val value: String,
    val default: Boolean = false,
    val style: FormTextStyle? = null,
    val margin: FormInsets = FormInsets(),
    val padding: FormInsets = FormInsets(),
    val border: FormBorder? = null,
)
