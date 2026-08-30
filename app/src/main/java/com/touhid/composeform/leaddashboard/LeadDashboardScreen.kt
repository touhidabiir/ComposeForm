package com.touhid.composeform.leaddashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.touhid.composeform.ComposeFormAppTheme
import com.touhid.composeform.common.ListEmptyState
import com.touhid.composeform.common.OnEndOfListReached
import com.touhid.composeform.common.copyIconButton
import com.touhid.composeform.designsystem.components.button.AppButton
import com.touhid.composeform.designsystem.components.button.AppStepperButton
import com.touhid.composeform.designsystem.components.icon.AppIcon
import com.touhid.composeform.designsystem.components.icon.AppIconButton
import com.touhid.composeform.designsystem.components.input.AppSearchField
import com.touhid.composeform.designsystem.components.layout.AppPullToRefreshBox
import com.touhid.composeform.designsystem.components.layout.AppScaffold
import com.touhid.composeform.designsystem.components.surface.AppBottomSheet
import com.touhid.composeform.designsystem.components.surface.AppCard
import com.touhid.composeform.designsystem.components.surface.AppChip
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
import com.touhid.composeform.designsystem.theme.StatusNeutralContainer
import com.touhid.composeform.network.model.LeadCloser
import com.touhid.composeform.network.model.LeadListItem
import com.touhid.composeform.network.model.LeadStatus
import com.touhid.composeform.network.model.Rejection
import com.touhid.composeform.network.model.Reviewer
import java.text.SimpleDateFormat
import java.util.Locale

private val RowIconSize = 16.dp

// The brand's secondary accent (search icon, contact icons, assigned-person names) - distinct from
// BrandPrimary, the brand's primary pink used for the app bar/CTA/selected chip/rejection banner.
// One caller (this screen) with an always-explicit value, so it stays local rather than moving
// into :designsystem's theme.
private val AccentIndigo = Color(0xFF675C92)

// The rejection banner's own background - close to but distinct from StatusInfoContainer, so it
// stays a plain local constant rather than a shared designsystem token for a one-off shade.
private val RejectionBannerBackground = Color(0xFFFFF8FB)

@Composable
fun LeadDashboardScreen(
    onBack: () -> Unit,
    onSubmitEkyc: (LeadListItem) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: LeadDashboardViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    LeadDashboardContent(
        state = state,
        onBack = onBack,
        onAction = viewModel::onAction,
        onSubmitEkyc = onSubmitEkyc,
        modifier = modifier,
    )
}

@Composable
private fun LeadDashboardContent(
    state: LeadDashboardState,
    onBack: () -> Unit,
    onAction: (LeadDashboardAction) -> Unit,
    onSubmitEkyc: (LeadListItem) -> Unit,
    modifier: Modifier = Modifier,
) {
    LaunchedEffect(Unit) { onAction(LeadDashboardAction.OnScreenStart) }

    val listState = rememberLazyListState()
    listState.OnEndOfListReached { onAction(LeadDashboardAction.OnLoadNextPage) }

    // Every successful first-page load (filter switch, search, refresh, retry) bumps
    // loadedRevision - scrolling back to the top here, not on every leads change, is what keeps a
    // paginated append from yanking the user's scroll position back up.
    // LaunchedEffect(state.loadedRevision) fires on every fresh composition, not just when the
    // value changes - Navigation Compose disposes a covered destination's composition entirely, so
    // returning to this screen (e.g. from a pushed detail screen) re-mounts it from scratch and
    // would otherwise call scrollToItem(0) unconditionally, undoing the scroll position
    // rememberSaveable(listState) just restored. lastScrolledRevision is itself rememberSaveable so
    // it survives that same dispose/recreate cycle, letting the effect tell "genuinely new results"
    // apart from "just returning to a screen already caught up".
    var lastScrolledRevision by rememberSaveable { mutableStateOf(state.loadedRevision) }
    LaunchedEffect(state.loadedRevision) {
        if (state.loadedRevision == lastScrolledRevision) return@LaunchedEffect
        lastScrolledRevision = state.loadedRevision
        listState.scrollToItem(0)
    }

    val snackbarHostState = rememberAppSnackbarHostState()
    LaunchedEffect(state.error) {
        if (state.error == null) return@LaunchedEffect
        val result = snackbarHostState.showMessage(message = "Please try again", actionLabel = "Retry")
        if (result == AppSnackbarResult.ActionPerformed) onAction(LeadDashboardAction.OnRetry)
    }

    if (state.isLoading) {
        AppProgressDialog()
    }

    AppScaffold(
        modifier = modifier.fillMaxSize(),
        topBar = { scrollBehavior ->
            AppTopBar(
                title = "লিড ড্যাশবোর্ড",
                navigationIcon = Icons.AutoMirrored.Filled.ArrowBack,
                onNavigationClick = onBack,
                scrollBehavior = scrollBehavior,
                actions = listOf(
                    AppTopBarAction(icon = Icons.Filled.Refresh, contentDescription = "Refresh", onClick = { onAction(LeadDashboardAction.OnRefresh) }),
                ),
            )
        },
        snackbarHost = { AppSnackbarHost(snackbarHostState) },
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.padding(AppSpacing.Medium)) {
                AppSearchField(
                    value = state.searchQuery,
                    onValueChange = { onAction(LeadDashboardAction.OnSearchQueryChanged(it)) },
                    placeholder = "লিড বেইজ সার্চ করুন...",
                    modifier = Modifier.fillMaxWidth(),
                    // AppSearchField's keyboardOptions already default to imeAction = Search -
                    // only the action handler needs wiring here so the IME's search key submits
                    // the same way the trailing search icon already does.
                    keyboardActions = KeyboardActions(onSearch = { onAction(LeadDashboardAction.OnSearchSubmitted) }),
                    trailingIcon = {
                        AppIconButton(
                            icon = Icons.Filled.Search,
                            contentDescription = "Search",
                            onClick = { onAction(LeadDashboardAction.OnSearchSubmitted) },
                            tint = AccentIndigo,
                        )
                    },
                )
                if (state.activeSearchQuery == null) {
                    Spacer(modifier = Modifier.height(AppSpacing.Medium))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(AppSpacing.Small),
                    ) {
                        LeadStatusFilter.entries.forEach { filter ->
                            AppChip(
                                text = filter.label,
                                selected = filter == state.selectedFilter,
                                onClick = { onAction(LeadDashboardAction.OnFilterSelected(filter)) },
                            )
                        }
                    }
                } else {
                    Spacer(modifier = Modifier.height(AppSpacing.Small))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(AppSpacing.ExtraSmall),
                    ) {
                        AppIcon(
                            icon = Icons.Filled.CheckCircle,
                            contentDescription = null,
                            modifier = Modifier.size(RowIconSize),
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
                onRefresh = { onAction(LeadDashboardAction.OnRefresh) },
                modifier = Modifier.fillMaxSize(),
            ) {
                if (state.leads.isEmpty()) {
                    // isLoading already surfaces its own AppProgressDialog - avoid flashing the
                    // empty state underneath it while that first load is still in flight.
                    if (!state.isLoading) {
                        // Same illustration either way - only the message distinguishes "this
                        // status has nothing" from "this search matched nothing".
                        ListEmptyState(
                            message = if (state.activeSearchQuery != null) {
                                "কোনো লিড পাওয়া যায়নি"
                            } else {
                                "এই স্ট্যাটাসে কোনো লিড পাওয়া যায়নি"
                            },
                            modifier = Modifier.align(Alignment.Center),
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
                        itemsIndexed(items = state.leads, key = { index, lead -> "${lead.id}_$index" }) { _, lead ->
                            LeadListCard(lead = lead, onSubmitEkyc = onSubmitEkyc)
                        }
                        if (state.isLoadingMore) {
                            item {
                                AppText(
                                    text = "আরও লোড হচ্ছে...",
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
private fun LeadListCard(lead: LeadListItem, onSubmitEkyc: (LeadListItem) -> Unit) {
    val iconModifier = Modifier.size(RowIconSize)
    val (badgeLabel, badgeTone) = when {
        lead.status == LeadStatus.Approved && lead.isEkycSubmitted -> "ই-কেওয়াইসি জমা হয়েছে" to AppStatusTone.Success
        lead.status == LeadStatus.Approved -> "অনুমোদিত" to AppStatusTone.Success
        lead.status == LeadStatus.Pending -> "পেন্ডিং" to AppStatusTone.Warning
        else -> "বাতিল" to AppStatusTone.Error
    }
    var showRejectionDetails by remember { mutableStateOf(false) }

    AppCard(
        modifier = Modifier.fillMaxWidth(),
        topContent = lead.rejection?.let { rejection ->
            { RejectionBanner(reason = rejection.reason, onClick = { showRejectionDetails = true }) }
        },
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            AppText(text = lead.shopName, style = AppTextStyle.TitleMedium)
            AppStatusBadge(text = badgeLabel, tone = badgeTone)
        }

        Spacer(modifier = Modifier.height(AppSpacing.Small))
        AppIconLabelValue(
            label = "ওয়ালেট নম্বর",
            value = lead.walletNumber,
            icon = { AppIcon(icon = Icons.Filled.Phone, contentDescription = null, modifier = iconModifier, tint = AccentIndigo) },
            trailingIcon = copyIconButton(lead.walletNumber),
        )

        Spacer(modifier = Modifier.height(AppSpacing.Small))
        AppIconLabelValue(
            label = "বিস্তারিত ঠিকানা",
            value = lead.address,
            icon = { AppIcon(icon = Icons.Filled.LocationOn, contentDescription = null, modifier = iconModifier) },
        )

        Spacer(modifier = Modifier.height(AppSpacing.Small))
        AppIconLabelValue(
            label = "লিড ক্লোজার এ. টি. ও.",
            value = "${lead.leadCloser.name} (${lead.leadCloser.employeeId})",
            icon = { AppIcon(icon = Icons.Filled.Person, contentDescription = null, modifier = iconModifier, tint = AccentIndigo) },
            trailingIcon = copyIconButton(lead.leadCloser.employeeId),
            subValue = "এম. এ.- ${lead.leadCloser.servingMa}",
        )

        lead.reviewer?.let { reviewer ->
            Spacer(modifier = Modifier.height(AppSpacing.Small))
            AppIconLabelValue(
                label = "অনুমোদনকারী",
                value = "${reviewer.name} (${reviewer.designation})",
                icon = { AppIcon(icon = Icons.Filled.Person, contentDescription = null, modifier = iconModifier, tint = AccentIndigo) },
                valueOverride = AppTextOverride(color = AccentIndigo),
                subValue = "টেরিটরি- ${reviewer.territory}",
            )
        }

        lead.ekycSubmitter?.let { submitter ->
            Spacer(modifier = Modifier.height(AppSpacing.Small))
            AppIconLabelValue(
                label = "ই-কেওয়াইসি জমাকারী এম. সি. ও.",
                value = submitter.name,
                icon = { AppIcon(icon = Icons.Filled.Person, contentDescription = null, modifier = iconModifier, tint = AccentIndigo) },
                valueOverride = AppTextOverride(color = AccentIndigo),
            )
        }

        when {
            lead.status == LeadStatus.Pending -> {
                Spacer(modifier = Modifier.height(AppSpacing.Medium))
                AppButton(
                    text = "লিড লক করুন",
                    onClick = {},
                    modifier = Modifier.fillMaxWidth(),
                    containerColor = StatusNeutralContainer,
                    contentColor = StatusNeutral,
                    leadingIcon = { AppIcon(icon = Icons.Filled.Lock, contentDescription = null, modifier = iconModifier, tint = StatusNeutral) },
                )
            }
            lead.status == LeadStatus.Approved && lead.canSubmitEkyc && !lead.isEkycSubmitted -> {
                Spacer(modifier = Modifier.height(AppSpacing.Medium))
                AppStepperButton(label = "ই-কেওয়াইসি জমা দিন", onClick = { onSubmitEkyc(lead) }, modifier = Modifier.fillMaxWidth())
            }
        }
    }

    if (showRejectionDetails) {
        RejectionDetailsSheet(lead = lead, onDismissRequest = { showRejectionDetails = false })
    }
}

// The rejection-reason callout at the top of a rejected lead's card - a pale pink banner holding a
// solid pink "reason" pill plus a single-line, truncated reason with a trailing chevron. Tapping it
// opens RejectionDetailsSheet with the untruncated reason. Built from Foundation
// background()/RoundedCornerShape + AppText/AppIcon, one caller, no Material3-derived default -
// stays in :app rather than :designsystem per the feature-specificity test in CLAUDE.md.
@Composable
private fun RejectionBanner(reason: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .background(RejectionBannerBackground, RoundedCornerShape(topStart = AppSpacing.Medium, topEnd = AppSpacing.Medium))
            .padding(horizontal = AppSpacing.Medium, vertical = AppSpacing.Small),
    ) {
        Box(
            modifier = Modifier
                .background(BrandPrimary, RoundedCornerShape(percent = 50))
                .padding(horizontal = AppSpacing.Small, vertical = 2.dp),
        ) {
            AppText(text = "বাতিল করার কারণ", style = AppTextStyle.Label, color = Color.White)
        }
        Spacer(modifier = Modifier.height(AppSpacing.Small))
        Row(verticalAlignment = Alignment.CenterVertically) {
            AppText(
                text = reason,
                style = AppTextStyle.BodyMedium,
                color = AccentIndigo,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f),
            )
            AppIcon(icon = Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = BrandPrimary)
        }
    }
}

// The full rejection detail opened by tapping RejectionBanner - same shop name / reason data as
// the banner but untruncated, plus who processed it and when. lead.reviewer/lead.createdAt double
// as the approver name and timestamp here since Rejection itself carries only a reason - the mock
// data already populates both alongside a rejection (see MockJson.kt), and reviewer is already
// labeled "অনুমোদনকারী" elsewhere on this same card.
@Composable
private fun RejectionDetailsSheet(lead: LeadListItem, onDismissRequest: () -> Unit) {
    AppBottomSheet(onDismissRequest = onDismissRequest) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            AppText(
                text = lead.shopName,
                style = AppTextStyle.TitleMedium,
                override = AppTextOverride(fontSize = 24.sp, fontWeight = FontWeight.Bold),
                modifier = Modifier.weight(1f),
            )
            AppIconButton(icon = Icons.Filled.Close, contentDescription = "Close", onClick = onDismissRequest)
        }

        Spacer(modifier = Modifier.height(AppSpacing.Medium))
        Row(verticalAlignment = Alignment.CenterVertically) {
            AppIcon(icon = Icons.Filled.Close, contentDescription = null, modifier = Modifier.size(RowIconSize), tint = BrandPrimary)
            Spacer(modifier = Modifier.width(AppSpacing.ExtraSmall))
            AppText(text = "বাতিল করার কারণ", style = AppTextStyle.Label, color = StatusNeutral)
        }

        Spacer(modifier = Modifier.height(AppSpacing.Small))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(StatusNeutralContainer, RoundedCornerShape(AppSpacing.Small))
                .padding(AppSpacing.Medium),
        ) {
            AppText(text = lead.rejection?.reason.orEmpty(), style = AppTextStyle.BodyMedium)
        }

        Spacer(modifier = Modifier.height(AppSpacing.Medium))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            AppText(
                text = "অনুমোদনকারী: ${lead.reviewer?.name ?: "-"}",
                style = AppTextStyle.Label,
                color = StatusNeutral,
            )
            AppText(
                text = formatRejectionTimestamp(lead.createdAt),
                style = AppTextStyle.Label,
                color = StatusNeutral,
            )
        }
    }
}

private fun formatRejectionTimestamp(iso: String): String = runCatching {
    val parsed = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX", Locale.US).parse(iso)
    SimpleDateFormat("d MMM yyyy; h:mm a", Locale.US).format(parsed!!)
}.getOrDefault(iso)

private val PreviewLeads = listOf(
    LeadListItem(
        id = 100238471,
        displayId = "LEAD-2026-100238471",
        shopName = "Romij Electric",
        walletNumber = "01723456789",
        address = "2 No. Road, Block-B, Syed Shah Road, Bakalia",
        status = LeadStatus.Pending,
        premiumnessScore = 61.2,
        canSubmitEkyc = false,
        leadCloser = LeadCloser(name = "Jamal Bhuiyan", employeeId = "A11002912", whitelistingNumber = "01930119876", servingMa = "01930198765"),
        createdAt = "2026-07-15T10:30:00+06:00",
    ),
    LeadListItem(
        id = 100238472,
        displayId = "LEAD-2026-100238472",
        shopName = "টেস্ট মার্চেন্ট এ",
        walletNumber = "01723456780",
        address = "3 No. Road, Block-C, Syed Shah Road, Bakalia",
        status = LeadStatus.Rejected,
        premiumnessScore = 40.0,
        canSubmitEkyc = false,
        leadCloser = LeadCloser(name = "Jamal Bhuiyan", employeeId = "A11002912", whitelistingNumber = "01930119876", servingMa = "01930198765"),
        reviewer = Reviewer(name = "আকমল হোসেন", designation = "OM", territory = "Bakalia"),
        rejection = Rejection(reason = "ডকুমেন্ট সংগ্রহে অস্পষ্টতা রয়েছে এবং নেটওয়ার্ক সমস্যা থাকায় এটি একটি লো ইমপ্যাক্ট লিড হিসেবে গণ্য হচ্ছে।"),
        createdAt = "2023-05-12T13:13:00+06:00",
    ),
)

// Single preview (no Dark variant) since ComposeFormAppTheme forces light theme regardless of
// system setting - that's how this screen actually renders in the real app, so a "Dark" tile
// here would show something the app never does.
@Preview(name = "Lead Dashboard", showBackground = true)
@Composable
private fun LeadDashboardScreenPreview() {
    ComposeFormAppTheme {
        LeadDashboardContent(
            state = LeadDashboardState(isLoading = false, leads = PreviewLeads, selectedFilter = LeadStatusFilter.Pending),
            onBack = {},
            onAction = {},
            onSubmitEkyc = {},
        )
    }
}
