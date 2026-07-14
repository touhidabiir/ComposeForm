package com.touhid.composeform.formbuilder.schema

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class FormLanguage {
    @SerialName("en") En,
    @SerialName("bn") Bn,
}
