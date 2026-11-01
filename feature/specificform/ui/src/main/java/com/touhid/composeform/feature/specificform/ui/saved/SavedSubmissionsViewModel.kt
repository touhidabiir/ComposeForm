package com.touhid.composeform.feature.specificform.ui.saved

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.touhid.composeform.feature.specificform.domain.SavedSubmission
import com.touhid.composeform.feature.specificform.domain.SpecificFormRepository
import com.touhid.composeform.model.AppResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface SavedSubmissionsResult {
    data object Uploaded : SavedSubmissionsResult
    data object Deleted : SavedSubmissionsResult
    data class Failed(val message: String) : SavedSubmissionsResult
}

data class SavedSubmissionsState(
    val isLoading: Boolean = true,
    val items: List<SavedSubmission> = emptyList(),
    // The row whose upload is in flight; its button shows progress and the others stay usable.
    val uploadingId: String? = null,
    // Non-null while the delete confirmation for that row is open.
    val deleteConfirmId: String? = null,
    // One-shot: set when an upload or delete finishes, cleared once the screen has shown it.
    val result: SavedSubmissionsResult? = null,
)

@HiltViewModel
class SavedSubmissionsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: SpecificFormRepository,
) : ViewModel() {

    private val formKey: String = checkNotNull(savedStateHandle[OpenSavedSubmissions.ExtraFormKey]) {
        "SavedSubmissionsActivity requires a form key; open it through OpenSavedSubmissions"
    }

    private val _state = MutableStateFlow(SavedSubmissionsState())
    val state = _state.asStateFlow()

    private var hasStarted = false

    // Dispatched once from LaunchedEffect(Unit), like the other screens' onScreenStart. The list
    // then follows the database: uploads and deletes update it without any refresh.
    fun onScreenStart() {
        if (hasStarted) return
        hasStarted = true
        viewModelScope.launch {
            repository.observeSavedSubmissions(formKey).collect { items ->
                _state.update { it.copy(isLoading = false, items = items) }
            }
        }
    }

    fun onUpload(id: String) {
        if (_state.value.uploadingId != null) return
        _state.update { it.copy(uploadingId = id) }
        viewModelScope.launch {
            val result = when (val upload = repository.uploadSaved(id)) {
                is AppResult.Success -> SavedSubmissionsResult.Uploaded
                is AppResult.Failure -> SavedSubmissionsResult.Failed(upload.error.message)
            }
            _state.update { it.copy(uploadingId = null, result = result) }
        }
    }

    fun onDeleteTapped(id: String) {
        _state.update { it.copy(deleteConfirmId = id) }
    }

    fun onDeleteCancelled() {
        _state.update { it.copy(deleteConfirmId = null) }
    }

    fun onDeleteConfirmed() {
        val id = _state.value.deleteConfirmId ?: return
        _state.update { it.copy(deleteConfirmId = null) }
        viewModelScope.launch {
            val result = when (val delete = repository.deleteSaved(id)) {
                is AppResult.Success -> SavedSubmissionsResult.Deleted
                is AppResult.Failure -> SavedSubmissionsResult.Failed(delete.error.message)
            }
            _state.update { it.copy(result = result) }
        }
    }

    fun onResultShown() {
        _state.update { it.copy(result = null) }
    }
}
