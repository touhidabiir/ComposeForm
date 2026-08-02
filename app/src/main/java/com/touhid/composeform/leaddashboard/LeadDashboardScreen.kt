package com.touhid.composeform.leaddashboard

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.touhid.composeform.designsystem.components.button.AppButton
import com.touhid.composeform.designsystem.components.button.AppStepperButton
import com.touhid.composeform.designsystem.components.icon.AppIcon
import com.touhid.composeform.designsystem.components.input.AppTextField
import com.touhid.composeform.designsystem.components.layout.AppScaffold
import com.touhid.composeform.designsystem.components.surface.AppCard
import com.touhid.composeform.designsystem.components.surface.AppStatusBadge
import com.touhid.composeform.designsystem.components.surface.AppStatusTone
import com.touhid.composeform.designsystem.components.surface.AppTopBar
import com.touhid.composeform.designsystem.components.surface.AppTopBarAction
import com.touhid.composeform.designsystem.components.text.AppIconLabelValue
import com.touhid.composeform.designsystem.components.text.AppText
import com.touhid.composeform.designsystem.components.text.AppTextOverride
import com.touhid.composeform.designsystem.components.text.AppTextStyle
import com.touhid.composeform.designsystem.theme.AppSpacing
import com.touhid.composeform.designsystem.theme.StatusError
import com.touhid.composeform.designsystem.theme.StatusInfo
import com.touhid.composeform.designsystem.theme.StatusNeutral

private val RowIconSize = 16.dp
private val MonthNames = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")

private val LeadListItem.isEkycSubmitted: Boolean get() = ekycSubmitter != null

private fun LeadListItem.toFilter(): LeadStatusFilter = when {
    status == LeadStatus.Approved && isEkycSubmitted -> LeadStatusFilter.EKyc
    status == LeadStatus.Approved -> LeadStatusFilter.Approved
    status == LeadStatus.Pending -> LeadStatusFilter.Pending
    status == LeadStatus.Rejected -> LeadStatusFilter.Rejected
    else -> LeadStatusFilter.Pending
}

// "2026-07-15T10:30:00+06:00" -> "15 Jul 2026" without pulling in java.time (minSdk 24 has no
// desugaring configured here) or a Locale-sensitive formatter for what's just a display label.
private fun formatLeadDate(isoDateTime: String): String {
    val datePart = isoDateTime.substringBefore('T')
    val parts = datePart.split("-")
    if (parts.size != 3) return datePart
    val (year, month, day) = parts
    val monthName = month.toIntOrNull()?.let { MonthNames.getOrNull(it - 1) } ?: month
    return "$day $monthName $year"
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
                AppTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = "লিড বেইজ সার্চ করুন...",
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(modifier = Modifier.height(AppSpacing.Medium))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(AppSpacing.Small),
                ) {
                    LeadStatusFilter.entries.forEach { filter ->
                        FilterChip(
                            label = filter.label,
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
private fun FilterChip(label: String, selected: Boolean, onClick: () -> Unit) {
    AppStatusBadge(
        text = label,
        tone = if (selected) AppStatusTone.Info else AppStatusTone.Neutral,
        modifier = Modifier.clickable(onClick = onClick),
    )
}

@Composable
private fun LeadListCard(lead: LeadListItem) {
    val iconModifier = Modifier.size(RowIconSize)
    val (badgeLabel, badgeTone) = when {
        lead.status == LeadStatus.Approved && lead.isEkycSubmitted -> "ই-কেওয়াইসি জমা হয়েছে" to AppStatusTone.Info
        lead.status == LeadStatus.Approved -> "অনুমোদিত" to AppStatusTone.Success
        lead.status == LeadStatus.Pending -> "পেন্ডিং" to AppStatusTone.Warning
        else -> "বাতিল" to AppStatusTone.Error
    }

    AppCard(modifier = Modifier.fillMaxWidth()) {
        lead.rejection?.let { rejection ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = AppSpacing.Small),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    AppText(text = "বাতিল করার কারণ", style = AppTextStyle.Label, override = AppTextOverride(color = StatusError))
                    AppText(text = rejection.reason, style = AppTextStyle.BodyMedium)
                }
                AppIcon(icon = Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null)
            }
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Column {
                AppText(text = lead.shopName, style = AppTextStyle.TitleMedium)
                AppText(text = lead.displayId, style = AppTextStyle.Label, override = AppTextOverride(color = StatusNeutral))
            }
            AppStatusBadge(text = badgeLabel, tone = badgeTone)
        }

        Spacer(modifier = Modifier.height(AppSpacing.Small))
        AppIconLabelValue(
            value = lead.walletNumber,
            icon = { AppIcon(icon = Icons.Filled.Phone, contentDescription = null, modifier = iconModifier) },
        )

        Spacer(modifier = Modifier.height(AppSpacing.Small))
        AppIconLabelValue(
            label = "বিস্তারিত ঠিকানা",
            value = lead.address,
            icon = { AppIcon(icon = Icons.Filled.LocationOn, contentDescription = null, modifier = iconModifier) },
        )

        Spacer(modifier = Modifier.height(AppSpacing.Small))
        AppIconLabelValue(
            label = "লিড অফিসার",
            value = "${lead.leadCloser.name} (${lead.leadCloser.employeeId})",
            icon = { AppIcon(icon = Icons.Filled.Person, contentDescription = null, modifier = iconModifier) },
            valueOverride = AppTextOverride(color = StatusInfo),
        )

        lead.reviewer?.let { reviewer ->
            Spacer(modifier = Modifier.height(AppSpacing.Small))
            AppIconLabelValue(
                label = "অনুমোদনকারী",
                value = "${reviewer.name} (${reviewer.designation})",
                icon = { AppIcon(icon = Icons.Filled.Person, contentDescription = null, modifier = iconModifier) },
                valueOverride = AppTextOverride(color = StatusInfo),
            )
        }

        lead.ekycSubmitter?.let { submitter ->
            Spacer(modifier = Modifier.height(AppSpacing.Small))
            AppIconLabelValue(label = "ই-কেওয়াইসি করেছেন", value = submitter.name)
        }

        Spacer(modifier = Modifier.height(AppSpacing.Small))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            AppIconLabelValue(label = "স্কোর", value = lead.premiumnessScore.toString())
            AppIconLabelValue(label = "তৈরি হয়েছে", value = formatLeadDate(lead.createdAt))
        }

        when {
            lead.status == LeadStatus.Pending -> {
                Spacer(modifier = Modifier.height(AppSpacing.Medium))
                AppButton(
                    text = "লিড লক করুন",
                    onClick = {},
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = { AppIcon(icon = Icons.Filled.Lock, contentDescription = null, modifier = iconModifier) },
                )
            }
            lead.status == LeadStatus.Approved && lead.canSubmitEkyc && !lead.isEkycSubmitted -> {
                Spacer(modifier = Modifier.height(AppSpacing.Medium))
                AppStepperButton(label = "ই-কেওয়াইসি জমা দিন", onClick = {}, modifier = Modifier.fillMaxWidth())
            }
        }
    }
}
