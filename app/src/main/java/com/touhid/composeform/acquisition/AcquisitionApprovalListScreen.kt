package com.touhid.composeform.acquisition

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.touhid.composeform.designsystem.components.button.AppStepperButton
import com.touhid.composeform.designsystem.components.icon.AppIcon
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
import com.touhid.composeform.designsystem.theme.StatusInfo

private val RowIconSize = 16.dp

@Composable
fun AcquisitionApprovalListScreen(
    onBack: () -> Unit,
    onReview: (String) -> Unit,
    modifier: Modifier = Modifier,
    listItems: List<AcquisitionListItem> = remember { sampleAcquisitionListItems() },
) {
    AppScaffold(
        modifier = modifier.fillMaxSize(),
        topBar = { scrollBehavior ->
            AppTopBar(
                title = "Acquisition Approval",
                navigationIcon = Icons.AutoMirrored.Filled.ArrowBack,
                onNavigationClick = onBack,
                scrollBehavior = scrollBehavior,
                actions = listOf(
                    AppTopBarAction(icon = Icons.Filled.Search, contentDescription = "Search", onClick = {}),
                ),
            )
        },
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(AppSpacing.Medium),
            verticalArrangement = Arrangement.spacedBy(AppSpacing.Medium),
        ) {
            items(items = listItems, key = { it.id }) { item ->
                AcquisitionListCard(item = item, onReview = { onReview(item.id) })
            }
        }
    }
}

@Composable
private fun AcquisitionListCard(item: AcquisitionListItem, onReview: () -> Unit) {
    val iconModifier = Modifier.size(RowIconSize)

    AppCard(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            AppText(text = item.merchantName, style = AppTextStyle.TitleMedium)
            AppStatusBadge(text = "পেন্ডিং", tone = AppStatusTone.Warning)
        }

        Spacer(modifier = Modifier.height(AppSpacing.Small))
        AppLabeledValue(
            value = item.phoneNumber,
            icon = { AppIcon(icon = Icons.Filled.Phone, contentDescription = null, modifier = iconModifier) },
        )

        Spacer(modifier = Modifier.height(AppSpacing.Small))
        AppLabeledValue(
            value = item.address,
            icon = { AppIcon(icon = Icons.Filled.LocationOn, contentDescription = null, modifier = iconModifier) },
        )

        Spacer(modifier = Modifier.height(AppSpacing.Small))
        AppLabeledValue(
            label = "লিড অফিসার",
            value = item.leadOfficer.name,
            icon = { AppIcon(icon = Icons.Filled.Person, contentDescription = null, modifier = iconModifier) },
            valueOverride = AppTextOverride(color = StatusInfo),
        )

        Spacer(modifier = Modifier.height(AppSpacing.Medium))
        AppStepperButton(label = "Review", onClick = onReview, modifier = Modifier.fillMaxWidth())
    }
}
