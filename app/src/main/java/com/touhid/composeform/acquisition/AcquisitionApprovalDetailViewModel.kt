package com.touhid.composeform.acquisition

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.touhid.composeform.network.NetworkResult
import com.touhid.composeform.network.model.AcquisitionDetail
import com.touhid.composeform.network.model.AcquisitionReason
import com.touhid.composeform.network.repository.AppRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "AcquisitionApprovalDetail"

enum class ReasonSheetType { Approve, Reject }

data class AcquisitionApprovalDetailState(
    val isLoading: Boolean = true,
    val detail: AcquisitionDetail? = null,
    val error: String? = null,
    // Which of the Approve/Reject reason sheets is currently open - null means neither. A single
    // nullable field (rather than two booleans) makes "both open at once" structurally impossible.
    val openReasonSheet: ReasonSheetType? = null,
    val reasonsLoading: Boolean = false,
    val reasons: List<AcquisitionReason> = emptyList(),
    val reasonsError: String? = null,
)

sealed interface AcquisitionApprovalDetailAction {
    data object OnRetry : AcquisitionApprovalDetailAction
    data object OnApproveTapped : AcquisitionApprovalDetailAction
    data object OnRejectTapped : AcquisitionApprovalDetailAction
    data object OnReasonSheetDismissed : AcquisitionApprovalDetailAction
    data class OnApproveConfirmed(val reasonIds: List<Int>, val note: String) : AcquisitionApprovalDetailAction
    data class OnRejectConfirmed(val reasonIds: List<Int>, val note: String) : AcquisitionApprovalDetailAction
}

@HiltViewModel
class AcquisitionApprovalDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: AppRepository,
) : ViewModel() {

    private val leadId: String = checkNotNull(savedStateHandle["leadId"]) { "acquisitionApprovalDetail route requires a leadId arg" }

    private val _state = MutableStateFlow(AcquisitionApprovalDetailState())
    val state = _state.asStateFlow()

    // Cancelling the previous load before starting a new one means a stale response can't land
    // after a newer retry superseded it (e.g. tapping Retry twice quickly).
    private var loadJob: Job? = null

    // Same cancel-before-restart guard as loadJob, for the reasons fetch - toggling between
    // Approve/Reject (or dismissing) quickly can't let a stale reasons response land in the
    // wrong (or now-closed) sheet.
    private var reasonsJob: Job? = null

    init {
        load()
    }

    fun onAction(action: AcquisitionApprovalDetailAction) {
        when (action) {
            AcquisitionApprovalDetailAction.OnRetry -> load()
            AcquisitionApprovalDetailAction.OnApproveTapped -> openReasonSheet(ReasonSheetType.Approve)
            AcquisitionApprovalDetailAction.OnRejectTapped -> openReasonSheet(ReasonSheetType.Reject)
            AcquisitionApprovalDetailAction.OnReasonSheetDismissed -> closeReasonSheet()
            is AcquisitionApprovalDetailAction.OnApproveConfirmed -> {
                // Stub: no submit endpoint exists yet - a later task wires the real approve call
                // here using action.reasonIds/action.note.
                Log.d(TAG, "Approve confirmed: reasonIds=${action.reasonIds}, note=${action.note}")
                closeReasonSheet()
            }
            is AcquisitionApprovalDetailAction.OnRejectConfirmed -> {
                Log.d(TAG, "Reject confirmed: reasonIds=${action.reasonIds}, note=${action.note}")
                closeReasonSheet()
            }
        }
    }

    private fun openReasonSheet(type: ReasonSheetType) {
        reasonsJob?.cancel()
        _state.update { it.copy(openReasonSheet = type, reasonsLoading = true, reasons = emptyList(), reasonsError = null) }
        reasonsJob = viewModelScope.launch {
            // UI-facing "Approve"/"Reject" naming intentionally maps to the API's "accept"/"reject"
            // type values - not a typo.
            val apiType = when (type) {
                ReasonSheetType.Approve -> "accept"
                ReasonSheetType.Reject -> "reject"
            }
            when (val result = repository.getAcquisitionReasons(leadId, apiType)) {
                is NetworkResult.Success -> _state.update { it.copy(reasonsLoading = false, reasons = result.data) }
                is NetworkResult.Failure -> _state.update { it.copy(reasonsLoading = false, reasonsError = result.error.message) }
            }
        }
    }

    private fun closeReasonSheet() {
        reasonsJob?.cancel()
        _state.update { it.copy(openReasonSheet = null) }
    }

    private fun load() {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            when (val result = repository.getAcquisitionDetail(leadId)) {
                is NetworkResult.Success -> _state.update { it.copy(isLoading = false, detail = result.data) }
                is NetworkResult.Failure -> _state.update { it.copy(isLoading = false, error = result.error.message) }
            }
        }
    }
}
