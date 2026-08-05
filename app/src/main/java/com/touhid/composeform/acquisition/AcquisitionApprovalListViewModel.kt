package com.touhid.composeform.acquisition

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.touhid.composeform.network.NetworkResult
import com.touhid.composeform.network.model.AcquisitionListItem
import com.touhid.composeform.network.repository.AppRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AcquisitionApprovalListState(
    val isLoading: Boolean = true,
    val isLoadingMore: Boolean = false,
    val items: List<AcquisitionListItem> = emptyList(),
    val searchQuery: String = "",
    // Non-null once a search has been submitted. Going back to an empty search box reverts this
    // to null and restores the normal unfiltered list.
    val activeSearchQuery: String? = null,
    val page: Int = 1,
    val hasMore: Boolean = true,
    val error: String? = null,
)

sealed interface AcquisitionApprovalListAction {
    data class OnSearchQueryChanged(val query: String) : AcquisitionApprovalListAction
    data object OnSearchSubmitted : AcquisitionApprovalListAction
    data object OnRetry : AcquisitionApprovalListAction
    data object OnRefresh : AcquisitionApprovalListAction
    data object OnLoadNextPage : AcquisitionApprovalListAction
}

@HiltViewModel
class AcquisitionApprovalListViewModel @Inject constructor(
    private val repository: AppRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(AcquisitionApprovalListState())
    val state = _state.asStateFlow()

    // Cancelling the previous load before starting a new one means an in-flight response can
    // never land after a newer request superseded it (e.g. two OnRefresh taps) - the old
    // coroutine is stopped before it can call _state.update at all.
    private var loadJob: Job? = null

    // state.items being non-empty doesn't reliably mean "a next-page load failed" - a failed
    // reload leaves the previous search's items sitting in state until a new load resolves - so
    // Retry needs its own record of which kind of load actually failed.
    private var retryLoadsNextPage = false

    init {
        loadFirstPage()
    }

    fun onAction(action: AcquisitionApprovalListAction) {
        when (action) {
            is AcquisitionApprovalListAction.OnSearchQueryChanged -> {
                _state.update { it.copy(searchQuery = action.query) }
                if (action.query.isBlank() && _state.value.activeSearchQuery != null) {
                    _state.update { it.copy(activeSearchQuery = null) }
                    loadFirstPage()
                }
            }
            AcquisitionApprovalListAction.OnSearchSubmitted -> {
                val query = _state.value.searchQuery
                if (query.isBlank()) return
                _state.update { it.copy(activeSearchQuery = query) }
                loadFirstPage()
            }
            AcquisitionApprovalListAction.OnRefresh -> loadFirstPage()
            AcquisitionApprovalListAction.OnLoadNextPage -> loadNextPage()
            AcquisitionApprovalListAction.OnRetry -> if (retryLoadsNextPage) loadNextPage() else loadFirstPage()
        }
    }

    private fun loadFirstPage() {
        val search = _state.value.activeSearchQuery
        retryLoadsNextPage = false
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            // Reset pagination state as the request starts, not just on success - otherwise a
            // failed reload leaves a stale hasMore=false/page from the previous search behind,
            // which makes a later Retry route into loadNextPage() and no-op.
            _state.update { it.copy(isLoading = true, isLoadingMore = false, error = null, page = 1, hasMore = true) }
            when (val result = repository.getAcquisitionList(search, 1)) {
                is NetworkResult.Success -> _state.update {
                    it.copy(isLoading = false, items = result.data.results, page = 1, hasMore = 1 < result.data.totalPages)
                }
                is NetworkResult.Failure -> _state.update { it.copy(isLoading = false, error = result.error.message) }
            }
        }
    }

    private fun loadNextPage() {
        val current = _state.value
        if (current.isLoading || current.isLoadingMore || !current.hasMore) return
        val search = current.activeSearchQuery
        val nextPage = current.page + 1
        retryLoadsNextPage = true
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            _state.update { it.copy(isLoadingMore = true, error = null) }
            when (val result = repository.getAcquisitionList(search, nextPage)) {
                is NetworkResult.Success -> _state.update {
                    it.copy(isLoadingMore = false, items = it.items + result.data.results, page = nextPage, hasMore = nextPage < result.data.totalPages)
                }
                is NetworkResult.Failure -> _state.update { it.copy(isLoadingMore = false, error = result.error.message) }
            }
        }
    }
}
