package com.touhid.composeform.leaddashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.touhid.composeform.network.NetworkResult
import com.touhid.composeform.network.model.LeadListItem
import com.touhid.composeform.network.repository.AppRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LeadDashboardState(
    val isLoading: Boolean = true,
    val isLoadingMore: Boolean = false,
    val leads: List<LeadListItem> = emptyList(),
    val selectedFilter: LeadStatusFilter = LeadStatusFilter.Pending,
    val searchQuery: String = "",
    // Non-null once a search has been submitted - while active it replaces the status filter as
    // what drives the list (chips hide in this mode). Going back to an empty search box reverts
    // this to null and restores the normal status-filtered browsing.
    val activeSearchQuery: String? = null,
    val page: Int = 1,
    val hasMore: Boolean = true,
    val error: String? = null,
)

sealed interface LeadDashboardAction {
    data class OnFilterSelected(val filter: LeadStatusFilter) : LeadDashboardAction
    data class OnSearchQueryChanged(val query: String) : LeadDashboardAction
    data object OnSearchSubmitted : LeadDashboardAction
    data object OnRetry : LeadDashboardAction
    data object OnRefresh : LeadDashboardAction
    data object OnLoadNextPage : LeadDashboardAction
}

@HiltViewModel
class LeadDashboardViewModel @Inject constructor(
    private val repository: AppRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(LeadDashboardState())
    val state = _state.asStateFlow()

    init {
        loadFirstPage()
    }

    fun onAction(action: LeadDashboardAction) {
        when (action) {
            is LeadDashboardAction.OnFilterSelected -> {
                if (action.filter == _state.value.selectedFilter) return
                _state.update { it.copy(selectedFilter = action.filter) }
                loadFirstPage()
            }
            is LeadDashboardAction.OnSearchQueryChanged -> {
                _state.update { it.copy(searchQuery = action.query) }
                if (action.query.isBlank() && _state.value.activeSearchQuery != null) {
                    _state.update { it.copy(activeSearchQuery = null) }
                    loadFirstPage()
                }
            }
            LeadDashboardAction.OnSearchSubmitted -> {
                val query = _state.value.searchQuery
                if (query.isBlank()) return
                _state.update { it.copy(activeSearchQuery = query) }
                loadFirstPage()
            }
            LeadDashboardAction.OnRefresh -> loadFirstPage()
            LeadDashboardAction.OnLoadNextPage -> loadNextPage()
            LeadDashboardAction.OnRetry -> if (_state.value.leads.isEmpty()) loadFirstPage() else loadNextPage()
        }
    }

    private fun loadFirstPage() {
        val requestedFilter = _state.value.selectedFilter
        val requestedSearch = _state.value.activeSearchQuery
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            val status = if (requestedSearch != null) null else requestedFilter.apiValue
            when (val result = repository.getLeadDashboard(status, requestedSearch, 1)) {
                is NetworkResult.Success -> _state.update { current ->
                    // The user may have switched chips or submitted/cleared a search while this
                    // was in flight - a stale page landing after a newer selection would
                    // otherwise clobber it.
                    if (!current.matchesRequest(requestedFilter, requestedSearch)) return@update current
                    // Track the requested page locally rather than trusting the response's own
                    // page_no/total_pages - MockDataInterceptor always echoes back the same
                    // canned page_no=1, so relying on it here would never advance and hasMore
                    // would stay true forever.
                    current.copy(
                        isLoading = false,
                        leads = result.data.results,
                        page = 1,
                        hasMore = 1 < result.data.totalPages,
                    )
                }
                is NetworkResult.Failure -> _state.update { current ->
                    if (!current.matchesRequest(requestedFilter, requestedSearch)) return@update current
                    current.copy(isLoading = false, error = result.error.message)
                }
            }
        }
    }

    private fun loadNextPage() {
        val current = _state.value
        if (current.isLoading || current.isLoadingMore || !current.hasMore) return
        val requestedFilter = current.selectedFilter
        val requestedSearch = current.activeSearchQuery
        val nextPage = current.page + 1
        viewModelScope.launch {
            _state.update { it.copy(isLoadingMore = true, error = null) }
            val status = if (requestedSearch != null) null else requestedFilter.apiValue
            when (val result = repository.getLeadDashboard(status, requestedSearch, nextPage)) {
                is NetworkResult.Success -> _state.update { state ->
                    if (!state.matchesRequest(requestedFilter, requestedSearch)) return@update state
                    state.copy(
                        isLoadingMore = false,
                        leads = state.leads + result.data.results,
                        page = nextPage,
                        hasMore = nextPage < result.data.totalPages,
                    )
                }
                is NetworkResult.Failure -> _state.update { state ->
                    if (!state.matchesRequest(requestedFilter, requestedSearch)) return@update state
                    state.copy(isLoadingMore = false, error = result.error.message)
                }
            }
        }
    }

    private fun LeadDashboardState.matchesRequest(filter: LeadStatusFilter, search: String?): Boolean =
        selectedFilter == filter && activeSearchQuery == search
}
