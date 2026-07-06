package com.touhid.composeform.formbuilder

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.touhid.composeform.formbuilder.schema.FormField
import com.touhid.composeform.formbuilder.schema.FormSchema
import com.touhid.composeform.formbuilder.schema.FormValue
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

internal class FormState(
    initialValues: Map<String, FormValue>,
    initialTouched: Set<String>,
) {
    var values: Map<String, FormValue> by mutableStateOf(initialValues)
        private set
    var touched: Set<String> by mutableStateOf(initialTouched)
        private set

    fun update(key: String, value: FormValue) {
        values = values + (key to value)
        touched = touched + key
    }
}

@Serializable
private data class FormStateSnapshot(
    val values: Map<String, FormValue>,
    val touched: List<String>,
)

private val formStateJson = Json { ignoreUnknownKeys = true }

private val FormStateSaver: Saver<FormState, String> = Saver(
    save = { state ->
        formStateJson.encodeToString(
            FormStateSnapshot.serializer(),
            FormStateSnapshot(state.values, state.touched.toList()),
        )
    },
    restore = { raw ->
        val snapshot = formStateJson.decodeFromString(FormStateSnapshot.serializer(), raw)
        FormState(snapshot.values, snapshot.touched.toSet())
    },
)

@Composable
internal fun rememberFormState(schema: FormSchema): FormState {
    return rememberSaveable(schema, saver = FormStateSaver) {
        FormState(
            initialValues = schema.fields.mapNotNull { field ->
                field.initialValue()?.let { field.key to it }
            }.toMap(),
            initialTouched = emptySet(),
        )
    }
}

private fun FormField.initialValue(): FormValue? = when (this) {
    is FormField.Text -> null
    is FormField.Submit -> null
    is FormField.InputBox -> FormValue.Text(defaultValue)
    is FormField.Checkbox -> FormValue.Text(defaultValue.toString())
    is FormField.Switch -> FormValue.Text(defaultValue.toString())
    is FormField.Radio -> options.firstOrNull { it.default }?.let { FormValue.Option(it.id, it.value) }
    is FormField.Dropdown -> options.firstOrNull { it.default }?.let { FormValue.Option(it.id, it.value) }
    is FormField.CheckboxGroup -> {
        val defaults = options.filter { it.default }
        if (defaults.isEmpty()) null else FormValue.Options(defaults.map { FormValue.Option(it.id, it.value) })
    }
}
