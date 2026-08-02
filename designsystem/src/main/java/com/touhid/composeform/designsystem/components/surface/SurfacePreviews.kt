package com.touhid.composeform.designsystem.components.surface

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.touhid.composeform.designsystem.components.text.AppText
import com.touhid.composeform.designsystem.theme.AppSpacing
import com.touhid.composeform.designsystem.theme.ComposeFormTheme

@Preview(name = "Light", showBackground = true)
@Preview(name = "Dark", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun TopBarPreview() {
    ComposeFormTheme {
        Column(verticalArrangement = Arrangement.spacedBy(AppSpacing.Small)) {
            AppTopBar(title = "Title only")
            AppTopBar(
                title = "With back",
                navigationIcon = Icons.AutoMirrored.Filled.ArrowBack,
                onNavigationClick = {},
            )
            AppTopBar(
                title = "With actions",
                navigationIcon = Icons.AutoMirrored.Filled.ArrowBack,
                onNavigationClick = {},
                actions = listOf(
                    AppTopBarAction(Icons.Filled.Search, "Search") {},
                    AppTopBarAction(Icons.Filled.MoreVert, "More") {},
                ),
            )
        }
    }
}

@Preview(name = "Light", showBackground = true)
@Preview(name = "Dark", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun CardDividerBadgePreview() {
    ComposeFormTheme {
        Column(
            modifier = Modifier.padding(AppSpacing.Medium),
            verticalArrangement = Arrangement.spacedBy(AppSpacing.Medium),
        ) {
            AppStatusBadge(text = "Approved", tone = AppStatusTone.Success)
            AppStatusBadge(text = "Pending", tone = AppStatusTone.Warning)
            AppStatusBadge(text = "Cancelled", tone = AppStatusTone.Error)
            AppStatusBadge(text = "e-KYC submitted", tone = AppStatusTone.Info)
            AppStatusBadge(text = "Neutral", tone = AppStatusTone.Neutral)
            AppCard {
                AppText(text = "Card title")
                AppDivider(modifier = Modifier.padding(vertical = AppSpacing.Small))
                AppText(text = "Card body content")
            }
        }
    }
}
