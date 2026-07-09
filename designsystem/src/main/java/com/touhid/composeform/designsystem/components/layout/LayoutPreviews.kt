package com.touhid.composeform.designsystem.components.layout

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.touhid.composeform.designsystem.components.surface.AppTopBar
import com.touhid.composeform.designsystem.components.text.AppText
import com.touhid.composeform.designsystem.components.text.AppTextStyle
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
