package com.touhid.composeform.formbuilder.schema

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class FormRadioAppearance {
    @SerialName("dot") Dot,
    @SerialName("check") Check,
    @SerialName("toggle") Toggle,
}
