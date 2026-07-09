package com.touhid.composeform.formbuilder.schema

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class FormVisibilityOperator {
    @SerialName("equals") Equals,
    @SerialName("notEquals") NotEquals,
    @SerialName("in") In,
}
