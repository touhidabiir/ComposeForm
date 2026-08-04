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
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.touhid.composeform.designsystem.components.button.AppStepperButton
import com.touhid.composeform.designsystem.components.icon.AppIcon
import com.touhid.composeform.designsystem.components.input.AppSearchField
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
import com.touhid.composeform.designsystem.theme.BrandPrimary
import com.touhid.composeform.designsystem.theme.StatusNeutral

private val RowIconSize = 16.dp
private val CopyIconSize = 14.dp

// Same brand secondary accent as LeadDashboardScreen - one caller each, kept local rather than
// promoted into :designsystem's theme (see LeadDashboardScreen.kt for the fuller rationale).
private val AccentIndigo = Color(0xFF675C92)

@Composable
fun AcquisitionApprovalListScreen(
    onBack: () -> Unit,
    onReview: (String) -> Unit,
    modifier: Modifier = Modifier,
    listItems: List<AcquisitionListItem> = remember { sampleAcquisitionListItems() },
) {
    var searchQuery by rememberSaveable { mutableStateOf("") }

    val visibleItems = listItems.filter { item ->
        searchQuery.isBlank() || item.shopName.contains(searchQuery, ignoreCase = true) || item.walletNumber.contains(searchQuery)
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
                    placeholder = "মার্চেন্ট সার্চ করুন...",
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = {
                        AppIcon(
                            icon = if (searchQuery.isBlank()) Icons.Filled.Search else Icons.Filled.Clear,
                            contentDescription = if (searchQuery.isBlank()) null else "Clear",
                            tint = AccentIndigo,
                        )
                    },
                )
                Spacer(modifier = Modifier.height(AppSpacing.Small))
                AppText(
                    text = "${visibleItems.size}টি ফলাফল পাওয়া গেছে",
                    style = AppTextStyle.Label,
                    override = AppTextOverride(color = StatusNeutral),
                )
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = AppSpacing.Medium, vertical = AppSpacing.Small),
                verticalArrangement = Arrangement.spacedBy(AppSpacing.Medium),
            ) {
                items(items = visibleItems, key = { it.id }) { item ->
                    AcquisitionListCard(item = item, onReview = { onReview(item.id.toString()) })
                }
            }
        }
    }
}

@Composable
private fun AcquisitionListCard(item: AcquisitionListItem, onReview: () -> Unit) {
    val iconModifier = Modifier.size(RowIconSize)
    val copyIcon: @Composable () -> Unit = {
        AppIcon(
            icon = Icons.Filled.ContentCopy,
            contentDescription = "Copy",
            modifier = Modifier.size(CopyIconSize),
            tint = BrandPrimary,
        )
    }

    AppCard(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Column {
                AppText(text = item.shopName, style = AppTextStyle.TitleMedium)
                AppText(text = item.displayId, style = AppTextStyle.Label, override = AppTextOverride(color = StatusNeutral))
            }
            AppStatusBadge(text = "পেন্ডিং", tone = AppStatusTone.Warning)
        }

        Spacer(modifier = Modifier.height(AppSpacing.Small))
        AppIconLabelValue(
            label = "ওয়ালেট নম্বর",
            value = item.walletNumber,
            icon = { AppIcon(icon = Icons.Filled.Phone, contentDescription = null, modifier = iconModifier, tint = AccentIndigo) },
            trailingIcon = copyIcon,
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
            trailingIcon = copyIcon,
            subValue = "এম. এ.- ${item.leadCloser.servingMa}",
        )

        if (item.canReview) {
            Spacer(modifier = Modifier.height(AppSpacing.Medium))
            AppStepperButton(label = "Review", onClick = onReview, modifier = Modifier.fillMaxWidth())
        }
    }
}
