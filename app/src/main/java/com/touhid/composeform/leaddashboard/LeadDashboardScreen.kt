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
import com.touhid.composeform.designsystem.components.text.AppLabeledValue
import com.touhid.composeform.designsystem.components.text.AppText
import com.touhid.composeform.designsystem.components.text.AppTextOverride
import com.touhid.composeform.designsystem.components.text.AppTextStyle
import com.touhid.composeform.designsystem.theme.AppSpacing
import com.touhid.composeform.designsystem.theme.StatusError
import com.touhid.composeform.designsystem.theme.StatusInfo

private val RowIconSize = 16.dp

private fun LeadStatus.toBadgeTone(): AppStatusTone = when (this) {
    LeadStatus.Pending -> AppStatusTone.Warning
    LeadStatus.Approved -> AppStatusTone.Success
    LeadStatus.EKycSubmitted -> AppStatusTone.Info
    LeadStatus.Cancelled -> AppStatusTone.Error
}

private fun LeadStatus.toFilter(): LeadStatusFilter = when (this) {
    LeadStatus.Pending -> LeadStatusFilter.Pending
    LeadStatus.Approved -> LeadStatusFilter.Approved
    LeadStatus.EKycSubmitted -> LeadStatusFilter.EKyc
    LeadStatus.Cancelled -> LeadStatusFilter.Cancelled
}

@Composable
fun LeadDashboardScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    leads: List<LeadCard> = remember { sampleLeadCards() },
) {
    var selectedFilter by rememberSaveable { mutableStateOf(LeadStatusFilter.Pending) }
    var searchQuery by rememberSaveable { mutableStateOf("") }

    val visibleLeads = leads.filter { lead ->
        lead.status.toFilter() == selectedFilter &&
            (searchQuery.isBlank() || lead.merchantName.contains(searchQuery, ignoreCase = true) || lead.phoneNumber.contains(searchQuery))
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
                AppText(text = "সর্বশেষ আপডেট: 12 May 2023, 1:13 PM", style = AppTextStyle.Label)
                Spacer(modifier = Modifier.height(AppSpacing.Small))
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
                    LeadListItem(lead = lead)
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
private fun LeadListItem(lead: LeadCard) {
    val iconModifier = Modifier.size(RowIconSize)

    AppCard(modifier = Modifier.fillMaxWidth()) {
        lead.cancellationReason?.let { reason ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = AppSpacing.Small),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    AppText(text = "বাতিল করার কারণ", style = AppTextStyle.Label, override = AppTextOverride(color = StatusError))
                    AppText(text = reason, style = AppTextStyle.BodyMedium)
                }
                AppIcon(icon = Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null)
            }
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            AppText(text = lead.merchantName, style = AppTextStyle.TitleMedium)
            AppStatusBadge(text = lead.status.badgeLabel, tone = lead.status.toBadgeTone())
        }

        Spacer(modifier = Modifier.height(AppSpacing.Small))
        AppLabeledValue(
            value = lead.phoneNumber,
            icon = { AppIcon(icon = Icons.Filled.Phone, contentDescription = null, modifier = iconModifier) },
        )

        Spacer(modifier = Modifier.height(AppSpacing.Small))
        AppLabeledValue(
            label = "বিস্তারিত ঠিকানা",
            value = lead.address,
            icon = { AppIcon(icon = Icons.Filled.LocationOn, contentDescription = null, modifier = iconModifier) },
        )

        Spacer(modifier = Modifier.height(AppSpacing.Small))
        AppLabeledValue(
            label = "লিড অফিসার",
            value = lead.leadOfficer.name,
            icon = { AppIcon(icon = Icons.Filled.Person, contentDescription = null, modifier = iconModifier) },
            valueOverride = AppTextOverride(color = StatusInfo),
        )

        lead.approver?.let { approver ->
            Spacer(modifier = Modifier.height(AppSpacing.Small))
            AppLabeledValue(
                label = "অনুমোদনকারী",
                value = approver.name,
                icon = { AppIcon(icon = Icons.Filled.Person, contentDescription = null, modifier = iconModifier) },
                valueOverride = AppTextOverride(color = StatusInfo),
            )
        }

        lead.eKycDoneBy?.let { doneBy ->
            Spacer(modifier = Modifier.height(AppSpacing.Small))
            AppLabeledValue(label = "ই-কেওয়াইসি করেছেন", value = doneBy)
        }

        when (lead.status) {
            LeadStatus.Pending -> {
                Spacer(modifier = Modifier.height(AppSpacing.Medium))
                AppButton(
                    text = "লিড লক করুন",
                    onClick = {},
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = { AppIcon(icon = Icons.Filled.Lock, contentDescription = null, modifier = iconModifier) },
                )
            }
            LeadStatus.Approved -> {
                Spacer(modifier = Modifier.height(AppSpacing.Medium))
                AppStepperButton(label = "ই-কেওয়াইসি জমা দিন", onClick = {}, modifier = Modifier.fillMaxWidth())
            }
            LeadStatus.EKycSubmitted, LeadStatus.Cancelled -> Unit
        }
    }
}
