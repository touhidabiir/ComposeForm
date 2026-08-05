package com.touhid.composeform.common

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember

// Shared by every paginated LazyColumn (Lead Dashboard, Acquisition Approval List) - fires
// onLoadMore once the user scrolls within `buffer` items of the end, so the next page starts
// fetching before the user actually hits the bottom.
@Composable
fun LazyListState.OnEndOfListReached(buffer: Int = 3, onLoadMore: () -> Unit) {
    val shouldLoadMore by remember(this) {
        derivedStateOf {
            val lastVisible = layoutInfo.visibleItemsInfo.lastOrNull() ?: return@derivedStateOf false
            lastVisible.index >= layoutInfo.totalItemsCount - 1 - buffer
        }
    }
    LaunchedEffect(shouldLoadMore) {
        if (shouldLoadMore) onLoadMore()
    }
}
