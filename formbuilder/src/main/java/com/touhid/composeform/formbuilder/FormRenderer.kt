package com.touhid.composeform.formbuilder

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.touhid.composeform.designsystem.components.button.AppButton
import com.touhid.composeform.designsystem.components.button.AppStepperButton
import com.touhid.composeform.designsystem.components.input.AppCheckbox
import com.touhid.composeform.designsystem.components.input.AppDropdown
import com.touhid.composeform.designsystem.components.input.AppDropdownOption
import com.touhid.composeform.designsystem.components.input.AppRadioButton
import com.touhid.composeform.designsystem.components.input.AppRadioCheckCircle
import com.touhid.composeform.designsystem.components.input.AppRadioToggleChip
import com.touhid.composeform.designsystem.components.input.AppSwitch
import com.touhid.composeform.designsystem.components.input.AppTextField
import com.touhid.composeform.designsystem.components.text.AppText
import com.touhid.composeform.formbuilder.schema.FormField
import com.touhid.composeform.formbuilder.schema.FormInsets
import com.touhid.composeform.formbuilder.schema.FormOption
import com.touhid.composeform.formbuilder.schema.FormOrientation
import com.touhid.composeform.formbuilder.schema.FormRadioAppearance
import com.touhid.composeform.formbuilder.schema.FormSchema
import com.touhid.composeform.formbuilder.schema.FormSubmitAppearance
import com.touhid.composeform.formbuilder.schema.FormValue

@Composable
fun FormRenderer(
    schema: FormSchema,
    modifier: Modifier = Modifier,
    pendingResult: FormFieldResult? = null,
    onPickerFieldClick: (key: String, pickerSchema: FormSchema) -> Unit = { _, _ -> },
    onSubmit: (Map<String, FormValue>) -> Unit,
) {
    val state = rememberFormState(schema)
    val errors = validate(schema, state.values)

    LaunchedEffect(pendingResult) {
        pendingResult?.let { state.update(it.key, FormValue.Text(it.value)) }
    }

    val (stickyFields, scrollableFields) = schema.fields.partition { it is FormField.Submit && it.sticky }

    Column(modifier = modifier.imePadding()) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
        ) {
            scrollableFields.forEach { field ->
                RenderFieldWithInsets(
                    field = field,
                    state = state,
                    errors = errors,
                    onPickerFieldClick = onPickerFieldClick,
                    onSubmit = onSubmit,
                )
            }
        }
        stickyFields.forEach { field ->
            RenderFieldWithInsets(
                field = field,
                state = state,
                errors = errors,
                onPickerFieldClick = onPickerFieldClick,
                onSubmit = onSubmit,
            )
        }
    }
}

@Composable
private fun RenderFieldWithInsets(
    field: FormField,
    state: FormState,
    errors: Map<String, String>,
    onPickerFieldClick: (key: String, pickerSchema: FormSchema) -> Unit,
    onSubmit: (Map<String, FormValue>) -> Unit,
) {
    if (!field.isVisible(state.values)) return
    Box(modifier = field.margin.toInsetModifier()) {
        Box(modifier = field.border.toModifier()) {
            Box(modifier = field.padding.toInsetModifier()) {
                RenderField(
                    field = field,
                    state = state,
                    errors = errors,
                    onPickerFieldClick = onPickerFieldClick,
                    onSubmit = onSubmit,
                )
            }
        }
    }
}

@Composable
private fun RenderField(
    field: FormField,
    state: FormState,
    errors: Map<String, String>,
    onPickerFieldClick: (key: String, pickerSchema: FormSchema) -> Unit,
    onSubmit: (Map<String, FormValue>) -> Unit,
) {
    val sizeModifier = field.size.toModifier()
    when (field) {
        is FormField.Text -> {
            AppText(text = field.label, override = field.style.toOverride(), modifier = sizeModifier)
        }

        is FormField.InputBox -> {
            val text = (state.values[field.key] as? FormValue.Text)?.value.orEmpty()
            val showError = field.key in state.touched && errors[field.key] != null
            AppTextField(
                value = text,
                onValueChange = { state.update(field.key, FormValue.Text(it)) },
                label = field.label,
                isError = showError,
                supportingText = if (showError) errors[field.key] else null,
                type = field.inputType.toAppTextFieldType(),
                modifier = sizeModifier,
                enabled = field.enabled,
                readOnly = !field.editable,
                onTrailingActionClick = field.pickerScreen?.let { pickerSchema ->
                    { onPickerFieldClick(field.key, pickerSchema) }
                },
            )
        }

        is FormField.Checkbox -> {
            val checked = (state.values[field.key] as? FormValue.Text)?.value.toBoolean()
            AppCheckbox(
                checked = checked,
                onCheckedChange = { state.update(field.key, FormValue.Text(it.toString())) },
                label = field.label,
                labelOverride = field.style.toOverride(),
                modifier = sizeModifier,
            )
        }

        is FormField.Switch -> {
            val checked = (state.values[field.key] as? FormValue.Text)?.value.toBoolean()
            AppSwitch(
                checked = checked,
                onCheckedChange = { state.update(field.key, FormValue.Text(it.toString())) },
                label = field.label,
                labelOverride = field.style.toOverride(),
                modifier = sizeModifier,
            )
        }

        is FormField.Radio -> {
            val selectedId = (state.values[field.key] as? FormValue.Option)?.id
            Column(modifier = sizeModifier) {
                if (field.label.isNotBlank()) {
                    AppText(text = field.label, override = field.style.toOverride())
                }
                OptionsContainer(field.orientation) { optionModifier ->
                    field.options.forEach { option ->
                        RenderOption(option, optionModifier) {
                            val selected = option.id == selectedId
                            val onClick = { state.update(field.key, FormValue.Option(option.id, option.value)) }
                            val labelOverride = option.style.toOverride()
                            when (field.appearance) {
                                FormRadioAppearance.Dot -> AppRadioButton(
                                    selected = selected,
                                    onClick = onClick,
                                    label = option.value,
                                    labelOverride = labelOverride,
                                )

                                FormRadioAppearance.Check -> AppRadioCheckCircle(
                                    selected = selected,
                                    onClick = onClick,
                                    label = option.value,
                                    labelOverride = labelOverride,
                                    modifier = Modifier.fillMaxWidth(),
                                )

                                FormRadioAppearance.Toggle -> AppRadioToggleChip(
                                    selected = selected,
                                    onClick = onClick,
                                    label = option.value,
                                    labelOverride = labelOverride,
                                    modifier = Modifier.fillMaxWidth(),
                                )
                            }
                        }
                    }
                }
            }
        }

        is FormField.Dropdown -> {
            val dropdownOptions = field.options.map { AppDropdownOption(it.value, it.style.toOverride()) }
            val selectedId = (state.values[field.key] as? FormValue.Option)?.id
            val selectedIndex = field.options.indexOfFirst { it.id == selectedId }
            AppDropdown(
                options = dropdownOptions,
                selectedOption = selectedIndex.takeIf { it >= 0 }?.let { dropdownOptions[it] },
                onOptionSelected = { chosen ->
                    val index = dropdownOptions.indexOfFirst { it === chosen }
                    val option = field.options[index]
                    state.update(field.key, FormValue.Option(option.id, option.value))
                },
                label = field.label,
                labelOverride = field.style.toOverride(),
                modifier = sizeModifier,
            )
        }

        is FormField.CheckboxGroup -> {
            val selected = (state.values[field.key] as? FormValue.Options)?.selected.orEmpty()
            Column(modifier = sizeModifier) {
                if (field.label.isNotBlank()) {
                    AppText(text = field.label, override = field.style.toOverride())
                }
                OptionsContainer(field.orientation) { optionModifier ->
                    field.options.forEach { option ->
                        val isChecked = selected.any { it.id == option.id }
                        RenderOption(option, optionModifier) {
                            AppCheckbox(
                                checked = isChecked,
                                onCheckedChange = { checked ->
                                    val newSelected = if (checked) {
                                        selected + FormValue.Option(option.id, option.value)
                                    } else {
                                        selected.filterNot { it.id == option.id }
                                    }
                                    state.update(field.key, FormValue.Options(newSelected))
                                },
                                label = option.value,
                                labelOverride = option.style.toOverride(),
                            )
                        }
                    }
                }
            }
        }

        is FormField.Submit -> {
            when (field.appearance) {
                FormSubmitAppearance.Plain -> AppButton(
                    text = field.label,
                    onClick = { onSubmit(state.values) },
                    enabled = errors.isEmpty(),
                    textOverride = field.style.toOverride(),
                    modifier = sizeModifier,
                )

                FormSubmitAppearance.Stepper -> AppStepperButton(
                    label = field.label,
                    onClick = { onSubmit(state.values) },
                    enabled = errors.isEmpty(),
                    progressText = field.progressText,
                    textOverride = field.style.toOverride(),
                    modifier = sizeModifier,
                )
            }
        }
    }
}

@Composable
private fun OptionsContainer(orientation: FormOrientation, content: @Composable (Modifier) -> Unit) {
    if (orientation == FormOrientation.Horizontal) {
        Row { content(Modifier.weight(1f)) }
    } else {
        Column { content(Modifier) }
    }
}

@Composable
private fun RenderOption(option: FormOption, modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Box(modifier = modifier.then(option.margin.toInsetModifier())) {
        Box(modifier = option.border.toModifier()) {
            Box(modifier = option.padding.toInsetModifier()) {
                content()
            }
        }
    }
}

private fun FormInsets.toInsetModifier(): Modifier =
    Modifier.padding(start = left.dp, top = top.dp, end = right.dp, bottom = bottom.dp)
