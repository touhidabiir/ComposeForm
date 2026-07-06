package com.touhid.composeform.formbuilder.schema

import kotlinx.serialization.Serializable

@Serializable
data class FormTextStyle(
    val size: Int? = null,
    val color: String? = null,
    val weight: String? = null,
)
