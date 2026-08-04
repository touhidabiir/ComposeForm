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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.touhid.composeform.ComposeFormAppTheme
import com.touhid.composeform.designsystem.components.button.AppButton
import com.touhid.composeform.designsystem.components.button.AppStepperButton
import com.touhid.composeform.designsystem.components.icon.AppIcon
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

private val RowIconSize = 16.dp
private val CopyIconSize = 14.dp

// The brand's secondary accent (search icon, contact icons, assigned-person names) - distinct from
// BrandPrimary, the brand's primary pink used for the app bar/CTA/selected chip/rejection banner.
// One caller (this screen) with an always-explicit value, so it stays local rather than moving
// into :designsystem's theme.
private val AccentIndigo = Color(0xFF675C92)

// The rejection banner's own background - close to but distinct from StatusInfoContainer, so it
// stays a plain local constant rather than a shared designsystem token for a one-off shade.
private val RejectionBannerBackground = Color(0xFFFFF8FB)

private val LeadListItem.isEkycSubmitted: Boolean get() = ekycSubmitter != null

private fun LeadListItem.toFilter(): LeadStatusFilter = when {
    status == LeadStatus.Approved && isEkycSubmitted -> LeadStatusFilter.EKyc
    status == LeadStatus.Approved -> LeadStatusFilter.Approved
    status == LeadStatus.Pending -> LeadStatusFilter.Pending
    status == LeadStatus.Rejected -> LeadStatusFilter.Rejected
    else -> LeadStatusFilter.Pending
}

@Composable
fun LeadDashboardScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    leads: List<LeadListItem> = remember { sampleLeadDashboardResults() },
) {
    var selectedFilter by rememberSaveable { mutableStateOf(LeadStatusFilter.Pending) }
    var searchQuery by rememberSaveable { mutableStateOf("") }

    val visibleLeads = leads.filter { lead ->
        lead.toFilter() == selectedFilter &&
            (searchQuery.isBlank() || lead.shopName.contains(searchQuery, ignoreCase = true) || lead.walletNumber.contains(searchQuery))
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
                    AppTopBarAction(icon = Icons.Filled.Refresh, contentDescription = "Refresh", onClick = {}),
                ),
            )
        },
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.padding(AppSpacing.Medium)) {
                AppSearchField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = "লিড বেইজ সার্চ করুন...",
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = { AppIcon(icon = Icons.Filled.Search, contentDescription = null, tint = AccentIndigo) },
                )
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
                            selected = filter == selectedFilter,
                            onClick = { selectedFilter = filter },
                        )
                    }
                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = AppSpacing.Medium, vertical = AppSpacing.Small),
                verticalArrangement = Arrangement.spacedBy(AppSpacing.Medium),
            ) {
                items(items = visibleLeads, key = { it.id }) { lead ->
                    LeadListCard(lead = lead)
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
            trailingIcon = {
                AppIcon(
                    icon = Icons.Filled.ContentCopy,
                    contentDescription = "Copy",
                    modifier = Modifier.size(CopyIconSize),
                    tint = BrandPrimary,
                )
            },
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
            trailingIcon = {
                AppIcon(
                    icon = Icons.Filled.ContentCopy,
                    contentDescription = "Copy",
                    modifier = Modifier.size(CopyIconSize),
                    tint = BrandPrimary,
                )
            },
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

// Single preview (no Dark variant) since ComposeFormAppTheme forces light theme regardless of
// system setting - that's how this screen actually renders in the real app, so a "Dark" tile
// here would show something the app never does.
@Preview(name = "Lead Dashboard", showBackground = true)
@Composable
private fun LeadDashboardScreenPreview() {
    ComposeFormAppTheme {
        LeadDashboardScreen(onBack = {})
    }
}
