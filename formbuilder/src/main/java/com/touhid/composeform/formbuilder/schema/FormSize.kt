package com.touhid.composeform.formbuilder.schema

import kotlinx.serialization.Serializable

@Serializable
data class FormSize(
    val width: String = "match_parent",
    val height: String = "wrap_content",
)
