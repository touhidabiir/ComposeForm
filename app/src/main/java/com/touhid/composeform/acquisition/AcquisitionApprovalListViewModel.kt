package com.touhid.composeform.acquisition

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.touhid.composeform.network.NetworkResult
import com.touhid.composeform.network.model.AcquisitionListItem
import com.touhid.composeform.network.repository.AppRepository
import dagger.hilt.android.lifecycle.HiltViewModel
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
            AcquisitionApprovalListAction.OnRetry -> if (_state.value.items.isEmpty()) loadFirstPage() else loadNextPage()
        }
    }

    private fun loadFirstPage() {
        val requestedSearch = _state.value.activeSearchQuery
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            when (val result = repository.getAcquisitionList(requestedSearch, 1)) {
                is NetworkResult.Success -> _state.update { current ->
                    if (current.activeSearchQuery != requestedSearch) return@update current
                    // Track the requested page locally rather than trusting the response's own
                    // page_no/total_pages - MockDataInterceptor always echoes back the same
                    // canned page_no=1, so relying on it here would never advance and hasMore
                    // would stay true forever.
                    current.copy(
                        isLoading = false,
                        items = result.data.results,
                        page = 1,
                        hasMore = 1 < result.data.totalPages,
                    )
                }
                is NetworkResult.Failure -> _state.update { current ->
                    if (current.activeSearchQuery != requestedSearch) return@update current
                    current.copy(isLoading = false, error = result.error.message)
                }
            }
        }
    }

    private fun loadNextPage() {
        val current = _state.value
        if (current.isLoading || current.isLoadingMore || !current.hasMore) return
        val requestedSearch = current.activeSearchQuery
        val nextPage = current.page + 1
        viewModelScope.launch {
            _state.update { it.copy(isLoadingMore = true, error = null) }
            when (val result = repository.getAcquisitionList(requestedSearch, nextPage)) {
                is NetworkResult.Success -> _state.update { state ->
                    if (state.activeSearchQuery != requestedSearch) return@update state
                    state.copy(
                        isLoadingMore = false,
                        items = state.items + result.data.results,
                        page = nextPage,
                        hasMore = nextPage < result.data.totalPages,
                    )
                }
                is NetworkResult.Failure -> _state.update { state ->
                    if (state.activeSearchQuery != requestedSearch) return@update state
                    state.copy(isLoadingMore = false, error = result.error.message)
                }
            }
        }
    }
}
