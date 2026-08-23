package com.touhid.composeform.specificform

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonObject
import com.touhid.composeform.formbuilder.parseSpecificFormSchema
import com.touhid.composeform.formbuilder.schema.FormField
import com.touhid.composeform.formbuilder.schema.FormSchema
import com.touhid.composeform.formbuilder.schema.FormSubmitAppearance
import com.touhid.composeform.formbuilder.schema.FormValue
import com.touhid.composeform.formbuilder.singleAnswerValue
import com.touhid.composeform.network.NetworkResult
import com.touhid.composeform.network.repository.AppRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface SpecificFormUiState {
    data object Loading : SpecificFormUiState
    data class Error(val message: String) : SpecificFormUiState
    data class Ready(val schema: FormSchema, val title: String, val key: String) : SpecificFormUiState
}

@HiltViewModel
class SpecificFormViewModel @Inject constructor(
    private val repository: AppRepository,
) : ViewModel() {

    private val _state = MutableStateFlow<SpecificFormUiState>(SpecificFormUiState.Loading)
    val state = _state.asStateFlow()

    // Guards onScreenStart so a re-dispatch (e.g. LaunchedEffect(Unit) re-running after process
    // death restores the same ViewModel instance) doesn't fire a second load.
    private var hasLoaded = false

    // This fragment's 1-based position among the host Activity's other fragments, and how many
    // there are in total - supplied by the host via SpecificFormFragment's arguments, not
    // anything this form's own schema/response knows about. Drives the stepper button's
    // progressText below.
    private var position = 1
    private var totalCount = 1

    // Dispatched once from LaunchedEffect(Unit) in SpecificFormFragment - kept as an explicit,
    // intent-driven trigger rather than an init{} side effect so construction stays cheap and the
    // ViewModel can be tested without a load firing automatically.
    fun onScreenStart(position: Int, totalCount: Int) {
        if (hasLoaded) return
        hasLoaded = true
        this.position = position
        this.totalCount = totalCount
        load()
    }

    fun retry() = load()

    private fun load() {
        _state.update { SpecificFormUiState.Loading }
        viewModelScope.launch {
            when (val result = repository.getSpecificForm()) {
                is NetworkResult.Success -> {
                    val payload = result.data
                    val questionsJson = JsonObject().apply { add("questions", payload.questions) }.toString()
                    val parsed = parseSpecificFormSchema(questionsJson)
                    val submitField = FormField.Submit(
                        key = "submit",
                        label = "Submit",
                        appearance = FormSubmitAppearance.Stepper,
                        progressText = "$position/$totalCount",
                    )
                    val schema = parsed.copy(fields = parsed.fields + submitField)
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
