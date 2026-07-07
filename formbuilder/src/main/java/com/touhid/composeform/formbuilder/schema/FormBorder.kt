package com.touhid.composeform.formbuilder.schema

import kotlinx.serialization.Serializable

@Serializable
data class FormBorder(
    val color: String = "#000000",
    val width: Int = 1,
    val radius: Int = 0,
)
