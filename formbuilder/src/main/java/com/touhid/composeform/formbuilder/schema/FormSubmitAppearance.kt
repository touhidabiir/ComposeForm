package com.touhid.composeform.formbuilder.schema

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class FormSubmitAppearance {
    @SerialName("plain") Plain,
    @SerialName("stepper") Stepper,
}
