package com.touhid.composeform.designsystem.components.media

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.touhid.composeform.designsystem.theme.AppSpacing
import com.touhid.composeform.designsystem.theme.ComposeFormTheme

@Preview(name = "Light", showBackground = true)
@Preview(name = "Dark", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun ImageCarouselPreview() {
    ComposeFormTheme {
        AppImageCarousel(
            modifier = Modifier.padding(AppSpacing.Medium),
            pages = listOf(
                AppCarouselPage(caption = "Outlet outside photo") {
                    Box(modifier = Modifier.fillMaxSize().background(Color(0xFFB0BEC5)))
                },
                AppCarouselPage(caption = "Outlet inside photo") {
                    Box(modifier = Modifier.fillMaxSize().background(Color(0xFF90A4AE)))
                },
            ),
        )
    }
}
