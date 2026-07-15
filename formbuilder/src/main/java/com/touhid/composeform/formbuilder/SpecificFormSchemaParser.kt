package com.touhid.composeform.formbuilder

import com.touhid.composeform.formbuilder.schema.FormField
import com.touhid.composeform.formbuilder.schema.FormInsets
import com.touhid.composeform.formbuilder.schema.FormLanguage
import com.touhid.composeform.formbuilder.schema.FormOption
import com.touhid.composeform.formbuilder.schema.FormOrientation
import com.touhid.composeform.formbuilder.schema.FormSchema
import com.touhid.composeform.formbuilder.schema.FormTextStyle
import com.touhid.composeform.formbuilder.schema.FormVisibilityCondition
import com.touhid.composeform.formbuilder.schema.FormVisibilityOperator
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

private val json = Json { ignoreUnknownKeys = true }

private val questionMargin = FormInsets(top = 24, bottom = 8)
private val optionMargin = FormInsets(top = 4, bottom = 4, left = 4, right = 4)
private val defaultTextStyle = FormTextStyle(color = "#262626")

@Serializable
private data class SpecificForm(val questions: List<SpecificQuestion>)

@Serializable
private data class SpecificQuestion(
    val key: String,
    val question: String,
    val type: String,
    val orientation: FormOrientation = FormOrientation.Horizontal,
    @SerialName("depends_on") val dependsOn: SpecificDependsOn? = null,
    val answers: List<SpecificAnswer>,
)

@Serializable
private data class SpecificDependsOn(val question: String, val answer: String)

@Serializable
private data class SpecificAnswer(val key: String, val value: String)

fun parseSpecificFormSchema(jsonString: String): FormSchema {
    val raw = json.decodeFromString(SpecificForm.serializer(), jsonString)
    return FormSchema(
        fields = raw.questions.map { it.toFormField() },
        numbered = true,
        language = FormLanguage.Bn,
    )
}

private fun SpecificQuestion.toFormField(): FormField {
    val options = answers.map { FormOption(id = it.key, value = it.value, style = defaultTextStyle, margin = optionMargin) }
    val visibleWhen = dependsOn?.let {
        FormVisibilityCondition(key = it.question, operator = FormVisibilityOperator.Equals, values = listOf(it.answer))
    }
    return if (type == "multiple_choice") {
        FormField.CheckboxGroup(
            key = key,
            label = question,
            options = options,
            orientation = orientation,
            style = defaultTextStyle,
            margin = questionMargin,
            visibleWhen = visibleWhen,
        )
    } else {
        FormField.Radio(
            key = key,
            label = question,
            options = options,
            orientation = orientation,
            style = defaultTextStyle,
            margin = questionMargin,
            visibleWhen = visibleWhen,
        )
    }
}
