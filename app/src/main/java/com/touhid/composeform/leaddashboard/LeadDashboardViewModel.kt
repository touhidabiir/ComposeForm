package com.touhid.composeform.leaddashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.touhid.composeform.network.NetworkResult
import com.touhid.composeform.network.model.LeadListItem
import com.touhid.composeform.data.repository.AppRepository
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
    // Lead ids with an eKYC submission currently in flight - a Set (not a single nullable id) so
    // tapping one card's submit button doesn't disable another card's, and two different leads can
    // submit concurrently without one cancelling the other.
    val submittingEkycLeadIds: Set<Long> = emptySet(),
    // One-shot: set to the lead just successfully submitted, so the screen can invoke its own
    // onSubmitEkyc callback exactly once - cleared via OnEkycSubmitHandled right after.
    val submittedEkycLead: LeadListItem? = null,
    val ekycSubmitError: String? = null,
)

sealed interface LeadDashboardAction {
    // Dispatched once from LaunchedEffect(Unit) in LeadDashboardScreen - kept as an explicit,
    // intent-driven trigger rather than an init{} side effect so construction stays cheap and
    // ViewModel tests can build the ViewModel without a load firing automatically.
    data object OnScreenStart : LeadDashboardAction
    data class OnFilterSelected(val filter: LeadStatusFilter) : LeadDashboardAction
    data class OnSearchQueryChanged(val query: String) : LeadDashboardAction
    data object OnSearchSubmitted : LeadDashboardAction
    data object OnRetry : LeadDashboardAction
    data object OnRefresh : LeadDashboardAction
    data object OnLoadNextPage : LeadDashboardAction
    data class OnSubmitEkycTapped(val lead: LeadListItem) : LeadDashboardAction
    // Dispatched by the screen right after it acts on a successful submittedEkycLead (invoking its
    // own onSubmitEkyc callback) - clears the one-shot signal so a config-change-driven
    // recomposition (the ViewModel survives, the Compose slot table doesn't) can't re-fire it.
    data object OnEkycSubmitHandled : LeadDashboardAction
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

    // Guards OnScreenStart so a re-dispatch (e.g. LaunchedEffect(Unit) re-running after process
    // death restores the same ViewModel instance) doesn't fire a second first-page load.
    private var hasLoaded = false

    fun onAction(action: LeadDashboardAction) {
        when (action) {
            LeadDashboardAction.OnScreenStart -> {
                if (hasLoaded) return
                hasLoaded = true
                loadFirstPage()
            }
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
            is LeadDashboardAction.OnSubmitEkycTapped -> submitEkyc(action.lead)
            LeadDashboardAction.OnEkycSubmitHandled -> _state.update { it.copy(submittedEkycLead = null) }
        }
    }

    private fun submitEkyc(lead: LeadListItem) {
        if (lead.id in _state.value.submittingEkycLeadIds) return
        _state.update { it.copy(submittingEkycLeadIds = it.submittingEkycLeadIds + lead.id, ekycSubmitError = null) }
        viewModelScope.launch {
            when (val result = repository.submitEkyc(lead.id)) {
                is NetworkResult.Success -> _state.update {
                    it.copy(submittingEkycLeadIds = it.submittingEkycLeadIds - lead.id, submittedEkycLead = lead)
                }
                is NetworkResult.Failure -> _state.update {
                    it.copy(submittingEkycLeadIds = it.submittingEkycLeadIds - lead.id, ekycSubmitError = result.error.message)
                }
            }
        }
    }

    private fun loadFirstPage(isRefresh: Boolean = false) {
        val filter = _state.value.selectedFilter
        val search = _state.value.activeSearchQuery
        retryLoadsNextPage = false
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            // Reset pagination state as the request starts, not just on success - otherwise a
            // failed reload leaves a stale page from the previous filter behind. leads is cleared
            // too (unless this is a refresh) - otherwise a failed filter switch or search leaves
            // the previous filter's results on screen under the new filter/search state, with
            // only the error snackbar hinting anything went wrong. A refresh keeps the old list
            // visible while it reloads, which is the point of pull-to-refresh. hasMore resets to
            // false, not true - we don't know yet whether this filter/search has a next page, and
            // if this request fails while old items are still on screen (the refresh case), a
            // stale hasMore=true would let a scroll-triggered OnLoadNextPage fetch a "next page"
            // for a page 1 that never actually loaded.
            _state.update {
                it.copy(
                    isLoading = !isRefresh,
                    isRefreshing = isRefresh,
                    isLoadingMore = false,
                    leads = if (isRefresh) it.leads else emptyList(),
                    error = null,
                    page = 1,
                    hasMore = false,
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
