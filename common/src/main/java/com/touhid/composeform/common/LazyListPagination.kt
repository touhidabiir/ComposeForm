package com.touhid.composeform.common

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull

// Shared by every paginated LazyColumn (Lead Dashboard, Acquisition Approval List) - fires
// onLoadMore once the user scrolls within `buffer` items of the end, so the next page starts
// fetching before the user actually hits the bottom. Keyed on the total item count (not a plain
// near-end boolean) so that a page which appends without moving the last-visible item far enough
// from the new end still re-arms the trigger for the next page - a boolean-keyed LaunchedEffect
// wouldn't restart when the value stays true across an append, stalling pagination after page 2.
@Composable
fun LazyListState.OnEndOfListReached(buffer: Int = 3, onLoadMore: () -> Unit) {
    val currentOnLoadMore by rememberUpdatedState(onLoadMore)
    LaunchedEffect(this, buffer) {
        snapshotFlow {
            val lastVisible = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: return@snapshotFlow null
            val total = layoutInfo.totalItemsCount
            if (lastVisible >= total - 1 - buffer) total else null
        }
            .distinctUntilChanged()
            .filterNotNull()
            .collect { currentOnLoadMore() }
    }
}
