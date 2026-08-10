package com.touhid.composeform.acquisition

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.touhid.composeform.ComposeFormAppTheme
import com.touhid.composeform.common.OnEndOfListReached
import com.touhid.composeform.designsystem.components.button.AppStepperButton
import com.touhid.composeform.designsystem.components.icon.AppIcon
import com.touhid.composeform.designsystem.components.icon.AppIconButton
import com.touhid.composeform.designsystem.components.input.AppSearchField
import com.touhid.composeform.designsystem.components.layout.AppPullToRefreshBox
import com.touhid.composeform.designsystem.components.layout.AppScaffold
import com.touhid.composeform.designsystem.components.surface.AppCard
import com.touhid.composeform.designsystem.components.surface.AppProgressDialog
import com.touhid.composeform.designsystem.components.surface.AppSnackbarHost
import com.touhid.composeform.designsystem.components.surface.AppSnackbarResult
import com.touhid.composeform.designsystem.components.surface.AppStatusBadge
import com.touhid.composeform.designsystem.components.surface.AppStatusTone
import com.touhid.composeform.designsystem.components.surface.AppTopBar
import com.touhid.composeform.designsystem.components.surface.AppTopBarAction
import com.touhid.composeform.designsystem.components.surface.rememberAppSnackbarHostState
import com.touhid.composeform.designsystem.components.text.AppIconLabelValue
import com.touhid.composeform.designsystem.components.text.AppText
import com.touhid.composeform.designsystem.components.text.AppTextOverride
import com.touhid.composeform.designsystem.components.text.AppTextStyle
import com.touhid.composeform.designsystem.theme.AppSpacing
import com.touhid.composeform.designsystem.theme.BrandPrimary
import com.touhid.composeform.designsystem.theme.StatusNeutral
import com.touhid.composeform.network.model.AcquisitionListItem
import com.touhid.composeform.network.model.LeadCloser

private val RowIconSize = 16.dp
private val CopyIconSize = 14.dp

// Same brand secondary accent as LeadDashboardScreen - one caller each, kept local rather than
// promoted into :designsystem's theme (see LeadDashboardScreen.kt for the fuller rationale).
private val AccentIndigo = Color(0xFF675C92)

// A copy-to-clipboard trailing icon for a specific piece of row text - a function (not a fixed
// composable) since each row copies different text; reads LocalClipboardManager once per call so
// the returned lambda's onClick can write straight to the system clipboard.
@Composable
private fun copyIconButton(text: String): @Composable () -> Unit {
    val clipboardManager = LocalClipboardManager.current
    return {
        AppIconButton(
            icon = Icons.Filled.ContentCopy,
            contentDescription = "Copy",
            onClick = { clipboardManager.setText(AnnotatedString(text)) },
            tint = BrandPrimary,
        )
    }
}

@Composable
fun AcquisitionApprovalListScreen(
    onBack: () -> Unit,
    onReview: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AcquisitionApprovalListViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    AcquisitionApprovalListContent(
        state = state,
        onBack = onBack,
        onReview = onReview,
        onAction = viewModel::onAction,
        modifier = modifier,
    )
}

@Composable
private fun AcquisitionApprovalListContent(
    state: AcquisitionApprovalListState,
    onBack: () -> Unit,
    onReview: (String) -> Unit,
    onAction: (AcquisitionApprovalListAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    val listState = rememberLazyListState()
    listState.OnEndOfListReached { onAction(AcquisitionApprovalListAction.OnLoadNextPage) }

    // Every successful first-page load (search, refresh, retry) bumps loadedRevision -
    // scrolling back to the top here, not on every items change, is what keeps a paginated
    // append from yanking the user's scroll position back up.
    LaunchedEffect(state.loadedRevision) {
        listState.scrollToItem(0)
    }

    val snackbarHostState = rememberAppSnackbarHostState()
    LaunchedEffect(state.error) {
        if (state.error == null) return@LaunchedEffect
        val result = snackbarHostState.showMessage(message = "Please try again", actionLabel = "Retry")
        if (result == AppSnackbarResult.ActionPerformed) onAction(AcquisitionApprovalListAction.OnRetry)
    }

    if (state.isLoading) {
        AppProgressDialog()
    }

    AppScaffold(
        modifier = modifier.fillMaxSize(),
        topBar = { scrollBehavior ->
            AppTopBar(
                title = "Acquisition Approval",
                navigationIcon = Icons.AutoMirrored.Filled.ArrowBack,
                onNavigationClick = onBack,
                scrollBehavior = scrollBehavior,
                actions = listOf(
                    AppTopBarAction(icon = Icons.Filled.Refresh, contentDescription = "Refresh", onClick = { onAction(AcquisitionApprovalListAction.OnRefresh) }),
                ),
            )
        },
        snackbarHost = { AppSnackbarHost(snackbarHostState) },
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.padding(AppSpacing.Medium)) {
                AppSearchField(
                    value = state.searchQuery,
                    onValueChange = { onAction(AcquisitionApprovalListAction.OnSearchQueryChanged(it)) },
                    placeholder = "মার্চেন্ট সার্চ করুন...",
                    modifier = Modifier.fillMaxWidth(),
                    // AppSearchField's keyboardOptions already default to imeAction = Search -
                    // only the action handler needs wiring here so the IME's search key submits
                    // the same way the trailing search icon already does.
                    keyboardActions = KeyboardActions(onSearch = { onAction(AcquisitionApprovalListAction.OnSearchSubmitted) }),
                    trailingIcon = {
                        AppIconButton(
                            icon = Icons.Filled.Search,
                            contentDescription = "Search",
                            onClick = { onAction(AcquisitionApprovalListAction.OnSearchSubmitted) },
                            tint = AccentIndigo,
                        )
                    },
                )
                if (state.activeSearchQuery != null) {
                    Spacer(modifier = Modifier.height(AppSpacing.Small))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(AppSpacing.ExtraSmall),
                    ) {
                        AppIcon(
                            icon = Icons.Filled.CheckCircle,
                            contentDescription = null,
                            modifier = Modifier.size(CopyIconSize),
                            tint = StatusNeutral,
                        )
                        AppText(
                            text = "${state.totalCount}টি ফলাফল পাওয়া গেছে",
                            style = AppTextStyle.Label,
                            override = AppTextOverride(color = StatusNeutral),
                        )
                    }
                }
            }

            AppPullToRefreshBox(
                isRefreshing = state.isRefreshing,
                onRefresh = { onAction(AcquisitionApprovalListAction.OnRefresh) },
                modifier = Modifier.fillMaxSize(),
            ) {
                if (state.items.isEmpty()) {
                    // isLoading already surfaces its own AppProgressDialog - avoid flashing the
                    // empty-state text underneath it while that first load is still in flight.
                    if (!state.isLoading) {
                        AppText(
                            text = "কোনো ফলাফল পাওয়া যায়নি",
                            modifier = Modifier.fillMaxWidth().padding(AppSpacing.Medium),
                            textAlign = TextAlign.Center,
                        )
                    }
                } else {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = AppSpacing.Medium, vertical = AppSpacing.Small),
                        verticalArrangement = Arrangement.spacedBy(AppSpacing.Medium),
                    ) {
                        // Composite id+index key: MockDataInterceptor returns the same fixed set
                        // of ids on every page, so a bare id key would collide once a second page
                        // is appended.
                        itemsIndexed(items = state.items, key = { index, item -> "${item.id}_$index" }) { _, item ->
                            AcquisitionListCard(item = item, onReview = { onReview(item.id.toString()) })
                        }
                        if (state.isLoadingMore) {
                            item {
                                AppText(
                                    text = "Loading more…",
                                    modifier = Modifier.fillMaxWidth().padding(AppSpacing.Medium),
                                    textAlign = TextAlign.Center,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AcquisitionListCard(item: AcquisitionListItem, onReview: () -> Unit) {
    val iconModifier = Modifier.size(RowIconSize)

    AppCard(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            AppText(text = item.shopName, style = AppTextStyle.TitleMedium)
            AppStatusBadge(text = "পেন্ডিং", tone = AppStatusTone.Warning)
        }

        Spacer(modifier = Modifier.height(AppSpacing.Small))
        AppIconLabelValue(
            label = "ওয়ালেট নম্বর",
            value = item.walletNumber,
            icon = { AppIcon(icon = Icons.Filled.Phone, contentDescription = null, modifier = iconModifier, tint = AccentIndigo) },
            trailingIcon = copyIconButton(item.walletNumber),
        )

        Spacer(modifier = Modifier.height(AppSpacing.Small))
        AppIconLabelValue(
            label = "বিস্তারিত ঠিকানা",
            value = item.address,
            icon = { AppIcon(icon = Icons.Filled.LocationOn, contentDescription = null, modifier = iconModifier) },
        )

        Spacer(modifier = Modifier.height(AppSpacing.Small))
        AppIconLabelValue(
            label = "লিড ক্লোজার এ. টি. ও.",
            value = "${item.leadCloser.name} (${item.leadCloser.employeeId})",
            icon = { AppIcon(icon = Icons.Filled.Person, contentDescription = null, modifier = iconModifier, tint = AccentIndigo) },
            trailingIcon = copyIconButton(item.leadCloser.employeeId),
            subValue = "এম. এ.- ${item.leadCloser.servingMa}",
        )

        if (item.canReview) {
            Spacer(modifier = Modifier.height(AppSpacing.Medium))
            AppStepperButton(label = "Review", onClick = onReview, modifier = Modifier.fillMaxWidth())
        }
    }
}

private val PreviewItems = listOf(
    AcquisitionListItem(
        id = 100238471,
        displayId = "LEAD-2026-100238471",
        shopName = "Romij Electric",
        walletNumber = "01723456789",
        address = "2 No. Road, Block-B, Syed Shah Road, Bakalia",
        leadCloser = LeadCloser(name = "Jamal Bhuiyan", employeeId = "A11002912", whitelistingNumber = "01930119876", servingMa = "01930198765"),
        submittedAt = "2026-07-13T14:30:00+06:00",
        canReview = true,
    ),
)

// Single preview (no Dark variant) since ComposeFormAppTheme forces light theme regardless of
// system setting - that's how this screen actually renders in the real app, so a "Dark" tile
// here would show something the app never does.
@Preview(name = "Acquisition Approval List", showBackground = true)
@Composable
private fun AcquisitionApprovalListScreenPreview() {
    ComposeFormAppTheme {
        AcquisitionApprovalListContent(
            state = AcquisitionApprovalListState(isLoading = false, items = PreviewItems),
            onBack = {},
            onReview = {},
            onAction = {},
        )
    }
}
