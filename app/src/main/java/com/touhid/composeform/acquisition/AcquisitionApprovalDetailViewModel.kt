package com.touhid.composeform.acquisition

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.touhid.composeform.network.NetworkResult
import com.touhid.composeform.network.model.AcquisitionDetail
import com.touhid.composeform.network.repository.AppRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AcquisitionApprovalDetailState(
    val isLoading: Boolean = true,
    val detail: AcquisitionDetail? = null,
    val error: String? = null,
)

sealed interface AcquisitionApprovalDetailAction {
    data object OnRetry : AcquisitionApprovalDetailAction
}

@HiltViewModel
class AcquisitionApprovalDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: AppRepository,
) : ViewModel() {

    private val leadId: String = checkNotNull(savedStateHandle["leadId"]) { "acquisitionApprovalDetail route requires a leadId arg" }

    private val _state = MutableStateFlow(AcquisitionApprovalDetailState())
    val state = _state.asStateFlow()

    init {
        load()
    }

    fun onAction(action: AcquisitionApprovalDetailAction) {
        when (action) {
            AcquisitionApprovalDetailAction.OnRetry -> load()
        }
    }

    private fun load() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            when (val result = repository.getAcquisitionDetail(leadId)) {
                is NetworkResult.Success -> _state.update { it.copy(isLoading = false, detail = result.data) }
                is NetworkResult.Failure -> _state.update { it.copy(isLoading = false, error = result.error.message) }
            }
        }
    }
}
