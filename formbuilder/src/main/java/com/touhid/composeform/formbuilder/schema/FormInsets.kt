package com.touhid.composeform.formbuilder.schema

import kotlinx.serialization.Serializable

@Serializable
data class FormInsets(
    val top: Int = 0,
    val bottom: Int = 0,
    val left: Int = 0,
    val right: Int = 0,
)
