package com.touhid.composeform.formbuilder.schema

import kotlinx.serialization.Serializable

@Serializable
data class FormBorder(
    val enabled: Boolean = false,
    val color: String = "#000000",
    val width: Int = 1,
)
