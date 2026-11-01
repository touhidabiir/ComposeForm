package com.touhid.composeform.feature.specificform.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.touhid.composeform.feature.specificform.domain.FormSubmission
import com.touhid.composeform.feature.specificform.domain.SpecificFormRepository
import com.touhid.composeform.formbuilder.decodeFormValues
import com.touhid.composeform.formbuilder.encodeFormValues
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

// What the screen reports with a snackbar once an action finishes.
sealed interface SpecificFormResult {
    data object Uploaded : SpecificFormResult
    data object SavedForLater : SpecificFormResult
    // Offers "Save for later" as the snackbar action, so answers aren't lost without a connection.
    data class UploadFailed(val message: String) : SpecificFormResult
    data class SaveFailed(val message: String) : SpecificFormResult
    data object ReopenFailed : SpecificFormResult
}

// Flat, independent fields (see CLAUDE.md's "Screen loading/error state"): the form stays on
// screen while an upload or save is in flight, and a failure is reported alongside the loaded form.
data class SpecificFormState(
    val isLoading: Boolean = true,
    val form: LoadedSpecificForm? = null,
    val error: String? = null,
    // Non-null while the "Upload now / Save for later" dialog is open: the answers it's about.
    val pendingValues: Map<String, FormValue>? = null,
    val submitting: Boolean = false,
    // One-shot: set when an action finishes, cleared once the screen has shown it.
    val result: SpecificFormResult? = null,
    // The values a reopened saved submission fills the form with. The screen recreates the form
    // whenever restoreVersion changes, since the form only reads initial values when created.
    val restoredValues: Map<String, FormValue> = emptyMap(),
    val restoreVersion: Int = 0,
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

    // The saved submission these answers belong to, once saved or reopened. Saving again
    // replaces it rather than adding a second copy, and a successful upload removes it.
    private var savedId: String? = null

    // The last answers sent, so a failed upload's "Save for later" action can save them.
    private var lastValues: Map<String, FormValue>? = null

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

    // FormState already drops the values of fields hidden by visibleWhen, so values holds exactly
    // the visible answers.
    fun onSubmit(values: Map<String, FormValue>) {
        _state.update { it.copy(pendingValues = values) }
    }

    fun onChoiceCancelled() {
        _state.update { it.copy(pendingValues = null) }
    }

    fun onUploadNow() {
        val values = _state.value.pendingValues ?: return
        _state.update { it.copy(pendingValues = null) }
        upload(values)
    }

    fun onSaveForLater() {
        val values = _state.value.pendingValues ?: return
        _state.update { it.copy(pendingValues = null) }
        save(values)
    }

    fun onSaveAfterFailedUpload() {
        lastValues?.let(::save)
    }

    private fun upload(values: Map<String, FormValue>) {
        val form = _state.value.form ?: return
        lastValues = values
        runSubmit {
            when (val result = repository.submit(form.toSubmission(values))) {
                is AppResult.Success -> {
                    // Uploaded, so a saved copy of these answers is no longer needed. If removing
                    // it fails, it stays in the saved list, where it can still be deleted by hand.
                    savedId?.let { repository.deleteSaved(it) }
                    savedId = null
                    SpecificFormResult.Uploaded
                }
                is AppResult.Failure -> SpecificFormResult.UploadFailed(result.error.message)
            }
        }
    }

    private fun save(values: Map<String, FormValue>) {
        val form = _state.value.form ?: return
        runSubmit {
            val result = repository.saveForLater(
                id = savedId,
                title = form.title,
                submission = form.toSubmission(values),
                formStateJson = encodeFormValues(values),
            )
            when (result) {
                is AppResult.Success -> {
                    savedId = result.data
                    SpecificFormResult.SavedForLater
                }
                is AppResult.Failure -> SpecificFormResult.SaveFailed(result.error.message)
            }
        }
    }

    // Called with the id the saved list returns when a saved submission is tapped.
    fun onReopenSaved(id: String) {
        runSubmit {
            when (val result = repository.getSavedSubmission(id)) {
                is AppResult.Success -> {
                    val saved = result.data ?: return@runSubmit SpecificFormResult.ReopenFailed
                    savedId = saved.id
                    _state.update {
                        it.copy(
                            restoredValues = decodeFormValues(saved.formStateJson),
                            restoreVersion = it.restoreVersion + 1,
                        )
                    }
                    null
                }
                is AppResult.Failure -> SpecificFormResult.ReopenFailed
            }
        }
    }

    // Runs one upload/save/reopen at a time behind the blocking progress dialog, then publishes
    // its result (null: nothing to report).
    private fun runSubmit(block: suspend () -> SpecificFormResult?) {
        submitJob?.cancel()
        _state.update { it.copy(submitting = true, result = null) }
        submitJob = viewModelScope.launch {
            val result = block()
            _state.update { it.copy(submitting = false, result = result) }
        }
    }

    // Called by the screen once it has shown the result's snackbar, so a recomposition with the
    // same ViewModel (e.g. after a configuration change) doesn't show it again.
    fun onResultShown() {
        _state.update { it.copy(result = null) }
    }

    // Option answers go out as option keys, not display text.
    private fun LoadedSpecificForm.toSubmission(values: Map<String, FormValue>) =
        FormSubmission(formKey = key, answers = values.toKeyValues())
}
