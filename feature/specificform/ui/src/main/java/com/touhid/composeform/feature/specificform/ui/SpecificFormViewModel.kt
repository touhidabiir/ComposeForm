package com.touhid.composeform.feature.specificform.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.touhid.composeform.feature.specificform.domain.FormSubmission
import com.touhid.composeform.feature.specificform.domain.SpecificFormRepository
import com.touhid.composeform.formbuilder.parseSpecificFormSchema
import com.touhid.composeform.formbuilder.schema.FormField
import com.touhid.composeform.formbuilder.schema.FormSchema
import com.touhid.composeform.formbuilder.schema.FormSubmitAppearance
import com.touhid.composeform.formbuilder.schema.FormValue
import com.touhid.composeform.formbuilder.toKeyValues
import com.touhid.composeform.model.AppResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LoadedSpecificForm(val key: String, val title: String, val schema: FormSchema)

// Flat, independent fields (see CLAUDE.md's "Screen loading/error state"): the form stays on
// screen while an upload is in flight, and an upload error sits alongside the loaded form.
data class SpecificFormState(
    val isLoading: Boolean = true,
    val form: LoadedSpecificForm? = null,
    val error: String? = null,
    val submitting: Boolean = false,
    val submitError: String? = null,
    // One-shot: true right after a successful upload, until the screen reports it shown.
    val submitted: Boolean = false,
)

@HiltViewModel
class SpecificFormViewModel @Inject constructor(
    private val repository: SpecificFormRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(SpecificFormState())
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

    private var submitJob: Job? = null

    // Kept so the failure snackbar's Retry can upload the same answers again.
    private var lastSubmission: FormSubmission? = null

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
        _state.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            when (val result = repository.getSpecificForm()) {
                is AppResult.Success -> {
                    val form = result.data
                    val parsed = parseSpecificFormSchema(form.schemaJson)
                    val submitField = FormField.Submit(
                        key = "submit",
                        label = "Submit",
                        appearance = FormSubmitAppearance.Stepper,
                        progressText = "$position/$totalCount",
                    )
                    val schema = parsed.copy(fields = parsed.fields + submitField)
                    _state.update {
                        it.copy(isLoading = false, form = LoadedSpecificForm(form.key, form.title, schema))
                    }
                }
                is AppResult.Failure -> _state.update { it.copy(isLoading = false, error = result.error.message) }
            }
        }
    }

    fun onSubmit(values: Map<String, FormValue>) {
        val form = _state.value.form ?: return
        // FormState already drops the values of fields hidden by visibleWhen, so values holds
        // exactly the visible answers. Option answers go out as option keys, not display text.
        upload(FormSubmission(formKey = form.key, answers = values.toKeyValues()))
    }

    fun retrySubmit() {
        lastSubmission?.let(::upload)
    }

    private fun upload(submission: FormSubmission) {
        lastSubmission = submission
        submitJob?.cancel()
        _state.update { it.copy(submitting = true, submitError = null, submitted = false) }
        submitJob = viewModelScope.launch {
            when (val result = repository.submit(submission)) {
                is AppResult.Success -> _state.update { it.copy(submitting = false, submitted = true) }
                is AppResult.Failure -> _state.update { it.copy(submitting = false, submitError = result.error.message) }
            }
        }
    }

    // Called by the screen once it has shown the upload's success or failure snackbar, so a
    // recomposition with the same ViewModel (e.g. after a configuration change) doesn't show it again.
    fun onSubmitResultShown() {
        _state.update { it.copy(submitted = false, submitError = null) }
    }
}
