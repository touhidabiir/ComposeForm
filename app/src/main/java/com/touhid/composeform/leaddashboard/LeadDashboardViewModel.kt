package com.touhid.composeform.leaddashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.touhid.composeform.network.NetworkResult
import com.touhid.composeform.network.model.LeadListItem
import com.touhid.composeform.network.repository.AppRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LeadDashboardState(
    val isLoading: Boolean = true,
    // Set only for a pull-to-refresh reload, never alongside isLoading - AppPullToRefreshBox
    // shows its own indicator for this, so the blocking AppProgressDialog (gated on isLoading)
    // doesn't also appear and double up.
    val isRefreshing: Boolean = false,
    val isLoadingMore: Boolean = false,
    val leads: List<LeadListItem> = emptyList(),
    // The API's total matching-result count, separate from leads.size (one page's worth) - what
    // the "N results found" label under an active search should read.
    val totalCount: Int = 0,
    val selectedFilter: LeadStatusFilter = LeadStatusFilter.Pending,
    val searchQuery: String = "",
    // Non-null once a search has been submitted - while active it replaces the status filter as
    // what drives the list (chips hide in this mode). Going back to an empty search box reverts
    // this to null and restores the normal status-filtered browsing.
    val activeSearchQuery: String? = null,
    val page: Int = 1,
    val hasMore: Boolean = false,
    val error: String? = null,
    // Bumped on every successful first-page load (filter switch, search, refresh, retry) - the
    // screen scrolls the list back to the top whenever this changes, but never on a page append.
    val loadedRevision: Int = 0,
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

    // Cancelling the previous load before starting a new one means an in-flight response can
    // never land after a newer request superseded it (e.g. switching filters mid-scroll, or two
    // OnRefresh taps) - the old coroutine is stopped before it can call _state.update at all.
    private var loadJob: Job? = null

    // state.leads being non-empty doesn't reliably mean "a next-page load failed" - e.g.
    // switching filters leaves the previous filter's leads sitting in state until the new load
    // resolves - so Retry needs its own record of which kind of load actually failed.
    private var retryLoadsNextPage = false

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
            LeadDashboardAction.OnRefresh -> loadFirstPage(isRefresh = true)
            LeadDashboardAction.OnLoadNextPage -> loadNextPage()
            LeadDashboardAction.OnRetry -> if (retryLoadsNextPage) loadNextPage() else loadFirstPage()
        }
    }

    private fun loadFirstPage(isRefresh: Boolean = false) {
        val filter = _state.value.selectedFilter
        val search = _state.value.activeSearchQuery
        retryLoadsNextPage = false
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            // Reset pagination state as the request starts, not just on success - otherwise a
            // failed reload leaves a stale hasMore=false/page from the previous filter behind,
            // which makes a later Retry route into loadNextPage() and no-op. leads is cleared
            // too (unless this is a refresh) - otherwise a failed filter switch or search leaves
            // the previous filter's results on screen under the new filter/search state, with
            // only the error snackbar hinting anything went wrong. A refresh keeps the old list
            // visible while it reloads, which is the point of pull-to-refresh.
            _state.update {
                it.copy(
                    isLoading = !isRefresh,
                    isRefreshing = isRefresh,
                    isLoadingMore = false,
                    leads = if (isRefresh) it.leads else emptyList(),
                    error = null,
                    page = 1,
                    hasMore = true,
                )
            }
            val status = if (search != null) null else filter.apiValue
            when (val result = repository.getLeadDashboard(status, search, 1)) {
                is NetworkResult.Success -> _state.update {
                    it.copy(
                        isLoading = false,
                        isRefreshing = false,
                        leads = result.data.results,
                        totalCount = result.data.count,
                        page = 1,
                        hasMore = 1 < result.data.totalPages,
                        loadedRevision = it.loadedRevision + 1,
                    )
                }
                is NetworkResult.Failure -> _state.update { it.copy(isLoading = false, isRefreshing = false, error = result.error.message) }
            }
        }
    }

    private fun loadNextPage() {
        val current = _state.value
        if (current.isLoading || current.isRefreshing || current.isLoadingMore || !current.hasMore) return
        val filter = current.selectedFilter
        val search = current.activeSearchQuery
        val nextPage = current.page + 1
        retryLoadsNextPage = true
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            _state.update { it.copy(isLoadingMore = true, error = null) }
            val status = if (search != null) null else filter.apiValue
            when (val result = repository.getLeadDashboard(status, search, nextPage)) {
                is NetworkResult.Success -> _state.update {
                    it.copy(isLoadingMore = false, leads = it.leads + result.data.results, page = nextPage, hasMore = nextPage < result.data.totalPages)
                }
                is NetworkResult.Failure -> _state.update { it.copy(isLoadingMore = false, error = result.error.message) }
            }
        }
    }
}
