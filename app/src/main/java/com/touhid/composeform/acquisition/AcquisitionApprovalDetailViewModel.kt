package com.touhid.composeform.acquisition

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
    val submitting: Boolean = false,
    val submitError: String? = null,
    // Set to the just-submitted type for one shot after a successful decision submit, so the
    // screen can navigate away (via its own onApprove/onReject) - the ViewModel itself has no
    // navigation concept, only whether the decision succeeded.
    val submittedDecision: ReasonSheetType? = null,
)

sealed interface AcquisitionApprovalDetailAction {
    data object OnRetry : AcquisitionApprovalDetailAction
    data object OnApproveTapped : AcquisitionApprovalDetailAction
    data object OnRejectTapped : AcquisitionApprovalDetailAction
    data object OnReasonSheetDismissed : AcquisitionApprovalDetailAction
    data class OnApproveConfirmed(val reasonIds: List<Int>, val note: String, val agreementIndex: Int) : AcquisitionApprovalDetailAction
    data class OnRejectConfirmed(val reasonIds: List<Int>, val note: String, val agreementIndex: Int) : AcquisitionApprovalDetailAction
    // Dispatched by the screen right after it acts on a successful submittedDecision (navigating
    // away via onApprove/onReject) - clears the one-shot signal so a config-change-driven
    // recomposition (the ViewModel survives, the Compose slot table doesn't) can't re-fire it.
    data object OnDecisionHandled : AcquisitionApprovalDetailAction
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

    private var submitJob: Job? = null

    init {
        load()
    }

    fun onAction(action: AcquisitionApprovalDetailAction) {
        when (action) {
            AcquisitionApprovalDetailAction.OnRetry -> load()
            AcquisitionApprovalDetailAction.OnApproveTapped -> openReasonSheet(ReasonSheetType.Approve)
            AcquisitionApprovalDetailAction.OnRejectTapped -> openReasonSheet(ReasonSheetType.Reject)
            AcquisitionApprovalDetailAction.OnReasonSheetDismissed -> closeReasonSheet()
            is AcquisitionApprovalDetailAction.OnApproveConfirmed ->
                submitDecision(ReasonSheetType.Approve, action.reasonIds, action.note, action.agreementIndex)
            is AcquisitionApprovalDetailAction.OnRejectConfirmed ->
                submitDecision(ReasonSheetType.Reject, action.reasonIds, action.note, action.agreementIndex)
            AcquisitionApprovalDetailAction.OnDecisionHandled -> _state.update { it.copy(submittedDecision = null) }
        }
    }

    private fun openReasonSheet(type: ReasonSheetType) {
        reasonsJob?.cancel()
        _state.update { it.copy(openReasonSheet = type, reasonsLoading = true, reasons = emptyList(), reasonsError = null, submitError = null) }
        reasonsJob = viewModelScope.launch {
            // UI-facing "Approve"/"Reject" naming intentionally maps to the reasons API's
            // "accept"/"reject" type values - not a typo (the decision-submit API below uses a
            // different vocabulary, "approve"/"reject", for the same UI concept).
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
        submitJob?.cancel()
        _state.update { it.copy(openReasonSheet = null, submitting = false, submitError = null) }
    }

    private fun submitDecision(type: ReasonSheetType, reasonIds: List<Int>, note: String, agreementIndex: Int) {
        submitJob?.cancel()
        _state.update { it.copy(submitting = true, submitError = null) }
        submitJob = viewModelScope.launch {
            val apiType = when (type) {
                ReasonSheetType.Approve -> "approve"
                ReasonSheetType.Reject -> "reject"
            }
            val scoreAgreement = when (agreementIndex) {
                0 -> "strongly_disagree"
                1 -> "disagree"
                3 -> "agree"
                4 -> "strongly_agree"
                else -> "neutral"
            }
            when (val result = repository.submitAcquisitionDecision(leadId, apiType, reasonIds, note, scoreAgreement)) {
                is NetworkResult.Success -> _state.update {
                    it.copy(submitting = false, openReasonSheet = null, submittedDecision = type)
                }
                is NetworkResult.Failure -> _state.update { it.copy(submitting = false, submitError = result.error.message) }
            }
        }
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
