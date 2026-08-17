package com.touhid.composeform.specificform

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.touhid.composeform.formbuilder.parseSpecificFormSchema
import com.touhid.composeform.formbuilder.schema.FormField
import com.touhid.composeform.formbuilder.schema.FormSchema
import com.touhid.composeform.formbuilder.schema.FormValue
import com.touhid.composeform.formbuilder.singleAnswerValue
import com.touhid.composeform.network.NetworkResult
import com.touhid.composeform.network.repository.AppRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import javax.inject.Inject

sealed interface SpecificFormUiState {
    data object Loading : SpecificFormUiState
    data class Error(val message: String) : SpecificFormUiState
    data class Ready(val schema: FormSchema, val title: String, val key: String) : SpecificFormUiState
}

// The v1/forms/specific response body, e.g. {"data": {"key": ..., "title": ..., "questions": [...]}}.
// questions is decoded as a raw JsonElement and re-wrapped below rather than modeled field-by-field
// here, since :formbuilder's parseSpecificFormSchema already parses that exact "questions" shape
// from a JSON string - no need for a second, duplicate model of it.
@Serializable
private data class SpecificFormEnvelope(val data: SpecificFormPayload)

@Serializable
private data class SpecificFormPayload(val key: String, val title: String, val questions: JsonElement)

private val envelopeJson = Json { ignoreUnknownKeys = true }

@HiltViewModel
class SpecificFormViewModel @Inject constructor(
    private val repository: AppRepository,
) : ViewModel() {

    private val _state = MutableStateFlow<SpecificFormUiState>(SpecificFormUiState.Loading)
    val state = _state.asStateFlow()

    // Guards onScreenStart so a re-dispatch (e.g. LaunchedEffect(Unit) re-running after process
    // death restores the same ViewModel instance) doesn't fire a second load.
    private var hasLoaded = false

    // Dispatched once from LaunchedEffect(Unit) in SpecificFormFragment - kept as an explicit,
    // intent-driven trigger rather than an init{} side effect so construction stays cheap and the
    // ViewModel can be tested without a load firing automatically.
    fun onScreenStart() {
        if (hasLoaded) return
        hasLoaded = true
        load()
    }

    fun retry() = load()

    private fun load() {
        _state.update { SpecificFormUiState.Loading }
        viewModelScope.launch {
            when (val result = repository.getSpecificForm()) {
                is NetworkResult.Success -> {
                    val payload = envelopeJson.decodeFromString<SpecificFormEnvelope>(result.data).data
                    val questionsJson = buildJsonObject { put("questions", payload.questions) }.toString()
                    val parsed = parseSpecificFormSchema(questionsJson)
                    val schema = parsed.copy(fields = parsed.fields + FormField.Submit(key = "submit", label = "Submit"))
                    _state.update { SpecificFormUiState.Ready(schema, title = payload.title, key = payload.key) }
                }
                is NetworkResult.Failure -> _state.update { SpecificFormUiState.Error(result.error.message) }
            }
        }
    }

    fun onSubmit(values: Map<String, FormValue>) {
        val schema = (_state.value as? SpecificFormUiState.Ready)?.schema ?: return
        Log.d(TAG, "Submitted answer: ${schema.singleAnswerValue(values)}")
    }

    private companion object {
        const val TAG = "SpecificForm"
    }
}
