package com.touhid.composeform.leaddashboard

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.touhid.composeform.ComposeFormAppTheme
import com.touhid.composeform.common.OnEndOfListReached
import com.touhid.composeform.designsystem.components.button.AppButton
import com.touhid.composeform.designsystem.components.button.AppStepperButton
import com.touhid.composeform.designsystem.components.icon.AppIcon
import com.touhid.composeform.designsystem.components.icon.AppIconButton
import com.touhid.composeform.designsystem.components.input.AppSearchField
import com.touhid.composeform.designsystem.components.layout.AppScaffold
import com.touhid.composeform.designsystem.components.surface.AppCard
import com.touhid.composeform.designsystem.components.surface.AppChip
import com.touhid.composeform.designsystem.components.surface.AppStatusBadge
import com.touhid.composeform.designsystem.components.surface.AppStatusTone
import com.touhid.composeform.designsystem.components.surface.AppTopBar
import com.touhid.composeform.designsystem.components.surface.AppTopBarAction
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

private val RowIconSize = 16.dp

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
    modifier: Modifier = Modifier,
    viewModel: LeadDashboardViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    LeadDashboardContent(
        state = state,
        onBack = onBack,
        onAction = viewModel::onAction,
        modifier = modifier,
    )
}

@Composable
private fun LeadDashboardContent(
    state: LeadDashboardState,
    onBack: () -> Unit,
    onAction: (LeadDashboardAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    val listState = rememberLazyListState()
    listState.OnEndOfListReached { onAction(LeadDashboardAction.OnLoadNextPage) }

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
                }
            }

            when {
                state.isLoading -> {
                    AppText(text = "লোড হচ্ছে...", modifier = Modifier.padding(AppSpacing.Medium))
                }

                state.error != null -> {
                    Column(modifier = Modifier.padding(AppSpacing.Medium)) {
                        AppText(text = state.error)
                        Spacer(modifier = Modifier.height(AppSpacing.Small))
                        AppButton(text = "Retry", onClick = { onAction(LeadDashboardAction.OnRetry) })
                    }
                }

                state.leads.isEmpty() -> {
                    AppText(
                        text = "কোনো লিড পাওয়া যায়নি",
                        modifier = Modifier.fillMaxWidth().padding(AppSpacing.Medium),
                        textAlign = TextAlign.Center,
                    )
                }

                else -> {
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
                            LeadListCard(lead = lead)
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
private fun LeadListCard(lead: LeadListItem) {
    val iconModifier = Modifier.size(RowIconSize)
    val (badgeLabel, badgeTone) = when {
        lead.status == LeadStatus.Approved && lead.isEkycSubmitted -> "ই-কেওয়াইসি জমা হয়েছে" to AppStatusTone.Success
        lead.status == LeadStatus.Approved -> "অনুমোদিত" to AppStatusTone.Success
        lead.status == LeadStatus.Pending -> "পেন্ডিং" to AppStatusTone.Warning
        else -> "বাতিল" to AppStatusTone.Error
    }

    AppCard(
        modifier = Modifier.fillMaxWidth(),
        topContent = lead.rejection?.let { rejection -> { RejectionBanner(reason = rejection.reason) } },
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
                AppStepperButton(label = "ই-কেওয়াইসি জমা দিন", onClick = {}, modifier = Modifier.fillMaxWidth())
            }
        }
    }
}

// The rejection-reason callout at the top of a rejected lead's card - a pale pink banner holding a
// solid pink "reason" pill plus a single-line, truncated reason with a trailing chevron. Built from
// Foundation background()/RoundedCornerShape + AppText/AppIcon, one caller, no Material3-derived
// default - stays in :app rather than :designsystem per the feature-specificity test in CLAUDE.md.
@Composable
private fun RejectionBanner(reason: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
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
        )
    }
}
