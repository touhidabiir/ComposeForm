package com.touhid.composeform.formbuilder.schema

import kotlinx.serialization.Serializable

@Serializable
sealed interface FormValue {
    @Serializable
    data class Text(val value: String) : FormValue

    @Serializable
    data class Option(val id: String, val value: String) : FormValue

    @Serializable
    data class Options(val selected: List<Option>) : FormValue

    @Serializable
    data class Image(val url: String, val localPath: String? = null) : FormValue
}
