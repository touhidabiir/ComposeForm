package com.touhid.composeform.formbuilder.schema

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonContentPolymorphicSerializer
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

@Serializable(with = FormFieldSerializer::class)
sealed interface FormField {
    val key: String
    val label: String
    val margin: FormInsets
    val padding: FormInsets
    val style: FormTextStyle?
    val size: FormSize
    val border: FormBorder?
    val visibleWhen: FormVisibilityCondition?

    @Serializable
    data class Text(
        override val key: String,
        override val label: String = "",
        override val margin: FormInsets = FormInsets(),
        override val padding: FormInsets = FormInsets(),
        override val style: FormTextStyle? = null,
        override val size: FormSize = FormSize(),
        override val border: FormBorder? = null,
        override val visibleWhen: FormVisibilityCondition? = null,
    ) : FormField

    @Serializable
    data class InputBox(
        override val key: String,
        override val label: String = "",
        override val margin: FormInsets = FormInsets(),
        override val padding: FormInsets = FormInsets(),
        override val style: FormTextStyle? = null,
        override val size: FormSize = FormSize(),
        override val border: FormBorder? = null,
        override val visibleWhen: FormVisibilityCondition? = null,
        val required: Boolean = false,
        val inputType: String = "text",
        val defaultValue: String = "",
        val pattern: String? = null,
        val errorMessage: String? = null,
        val minLength: Int? = null,
        val maxLength: Int? = null,
    ) : FormField

    @Serializable
    data class Checkbox(
        override val key: String,
        override val label: String = "",
        override val margin: FormInsets = FormInsets(),
        override val padding: FormInsets = FormInsets(),
        override val style: FormTextStyle? = null,
        override val size: FormSize = FormSize(),
        override val border: FormBorder? = null,
        override val visibleWhen: FormVisibilityCondition? = null,
        val required: Boolean = false,
        val defaultValue: Boolean = false,
    ) : FormField

    @Serializable
    data class CheckboxGroup(
        override val key: String,
        override val label: String = "",
        override val margin: FormInsets = FormInsets(),
        override val padding: FormInsets = FormInsets(),
        override val style: FormTextStyle? = null,
        override val size: FormSize = FormSize(),
        override val border: FormBorder? = null,
        override val visibleWhen: FormVisibilityCondition? = null,
        val required: Boolean = false,
        val options: List<FormOption>,
        val orientation: FormOrientation = FormOrientation.Vertical,
    ) : FormField

    @Serializable
    data class Radio(
        override val key: String,
        override val label: String = "",
        override val margin: FormInsets = FormInsets(),
        override val padding: FormInsets = FormInsets(),
        override val style: FormTextStyle? = null,
        override val size: FormSize = FormSize(),
        override val border: FormBorder? = null,
        override val visibleWhen: FormVisibilityCondition? = null,
        val required: Boolean = false,
        val options: List<FormOption>,
        val orientation: FormOrientation = FormOrientation.Vertical,
        val appearance: FormRadioAppearance = FormRadioAppearance.Dot,
    ) : FormField

    @Serializable
    data class Switch(
        override val key: String,
        override val label: String = "",
        override val margin: FormInsets = FormInsets(),
        override val padding: FormInsets = FormInsets(),
        override val style: FormTextStyle? = null,
        override val size: FormSize = FormSize(),
        override val border: FormBorder? = null,
        override val visibleWhen: FormVisibilityCondition? = null,
        val required: Boolean = false,
        val defaultValue: Boolean = false,
    ) : FormField

    @Serializable
    data class Dropdown(
        override val key: String,
        override val label: String = "",
        override val margin: FormInsets = FormInsets(),
        override val padding: FormInsets = FormInsets(),
        override val style: FormTextStyle? = null,
        override val size: FormSize = FormSize(),
        override val border: FormBorder? = null,
        override val visibleWhen: FormVisibilityCondition? = null,
        val required: Boolean = false,
        val options: List<FormOption>,
    ) : FormField

    @Serializable
    data class Submit(
        override val key: String,
        override val label: String = "",
        override val style: FormTextStyle? = null,
        override val margin: FormInsets = FormInsets(),
        override val padding: FormInsets = FormInsets(),
        override val size: FormSize = FormSize(),
        override val border: FormBorder? = null,
        override val visibleWhen: FormVisibilityCondition? = null,
    ) : FormField
}

object FormFieldSerializer : JsonContentPolymorphicSerializer<FormField>(FormField::class) {
    override fun selectDeserializer(element: JsonElement): DeserializationStrategy<FormField> {
        val type = element.jsonObject["type"]?.jsonPrimitive?.content
        return when (type) {
            "text" -> FormField.Text.serializer()
            "inputBox" -> FormField.InputBox.serializer()
            "checkbox" -> FormField.Checkbox.serializer()
            "checkboxGroup" -> FormField.CheckboxGroup.serializer()
            "radio" -> FormField.Radio.serializer()
            "switch" -> FormField.Switch.serializer()
            "dropdown" -> FormField.Dropdown.serializer()
            "submit" -> FormField.Submit.serializer()
            else -> error("Unknown form field type: $type")
        }
    }
}
