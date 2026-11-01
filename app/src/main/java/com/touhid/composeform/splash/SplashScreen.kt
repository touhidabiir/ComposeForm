package com.touhid.composeform.splash

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.touhid.composeform.common.BrandLogo
import com.touhid.composeform.designsystem.theme.BrandPrimary
import com.touhid.composeform.designsystem.theme.ComposeFormTheme

@Composable
fun SplashScreen(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize().background(BrandPrimary),
        contentAlignment = Alignment.Center,
    ) {
        BrandLogo(onBrandBackground = true)
    }
}

// Single preview: the splash is brand pink in both modes.
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
private fun SplashScreenPreview() {
    ComposeFormTheme {
        SplashScreen()
    }
}
