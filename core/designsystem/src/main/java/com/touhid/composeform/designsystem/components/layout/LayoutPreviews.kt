package com.touhid.composeform.designsystem.components.layout

import android.content.res.Configuration
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.touhid.composeform.designsystem.components.button.AppButton
import com.touhid.composeform.designsystem.components.surface.AppTopBar
import com.touhid.composeform.designsystem.components.text.AppText
import com.touhid.composeform.designsystem.components.text.AppTextStyle
import com.touhid.composeform.designsystem.theme.AppSpacing
import com.touhid.composeform.designsystem.theme.ComposeFormTheme

@Preview(name = "Light", showBackground = true)
@Preview(name = "Dark", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun ScaffoldPreview() {
    ComposeFormTheme {
        AppScaffold {
            AppText("Content inside AppScaffold", style = AppTextStyle.TitleMedium)
        }
    }
}

@Preview(name = "Light", showBackground = true)
@Preview(name = "Dark", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun ScaffoldWithTopBarPreview() {
    ComposeFormTheme {
        AppScaffold(topBar = { scrollBehavior ->
            AppTopBar(title = "Screen Title", scrollBehavior = scrollBehavior)
        }) {
            AppText("Content inside AppScaffold", style = AppTextStyle.TitleMedium)
        }
    }
}

@Preview(name = "Light", showBackground = true)
@Preview(name = "Dark", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun PullToRefreshBoxPreview() {
    ComposeFormTheme {
        AppPullToRefreshBox(isRefreshing = false, onRefresh = {}) {
            AppText("Pull down to refresh", style = AppTextStyle.TitleMedium, modifier = Modifier.padding(AppSpacing.Medium))
        }
    }
}

@Preview(name = "Light", showBackground = true)
@Preview(name = "Dark", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun ScaffoldWithBottomBarPreview() {
    ComposeFormTheme {
        AppScaffold(
            topBar = { scrollBehavior -> AppTopBar(title = "Screen Title", scrollBehavior = scrollBehavior) },
            bottomBar = {
                Surface {
                    AppButton(
                        text = "Approve",
                        onClick = {},
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(AppSpacing.Medium),
                    )
                }
            },
        ) {
            AppText("Content inside AppScaffold", style = AppTextStyle.TitleMedium)
        }
    }
}
