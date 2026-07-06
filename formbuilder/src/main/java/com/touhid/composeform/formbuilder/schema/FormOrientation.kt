package com.touhid.composeform.formbuilder.schema

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class FormOrientation {
    @SerialName("vertical") Vertical,
    @SerialName("horizontal") Horizontal,
}
