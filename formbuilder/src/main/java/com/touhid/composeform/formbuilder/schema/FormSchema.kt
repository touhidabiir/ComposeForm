package com.touhid.composeform.formbuilder.schema

import kotlinx.serialization.Serializable

@Serializable
data class FormSchema(
    val fields: List<FormField>,
    val screenTitle: String? = null,
    val numbered: Boolean = false,
    val language: FormLanguage = FormLanguage.En,
)
