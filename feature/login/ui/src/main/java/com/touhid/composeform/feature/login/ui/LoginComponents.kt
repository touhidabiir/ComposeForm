package com.touhid.composeform.feature.login.ui

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.touhid.composeform.designsystem.components.text.AppText
import com.touhid.composeform.designsystem.components.text.AppTextOverride
import com.touhid.composeform.designsystem.components.text.AppTextStyle

private val VersionColor = Color(0xFF2E2A6B)
internal val MutedTextColor = Color(0xFF757575)

// "version: 1.0" at the top of both sign-in screens. Read from the installed package, since
// :app's BuildConfig isn't visible from a feature module.
@Composable
internal fun VersionLabel(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val versionName = remember(context) { context.versionName() }
    AppText(
        text = stringResource(R.string.login_version, versionName),
        modifier = modifier,
        style = AppTextStyle.BodyMedium,
        color = VersionColor,
    )
}

@Composable
internal fun LoginTitle(text: String, modifier: Modifier = Modifier) {
    AppText(
        text = text,
        modifier = modifier,
        style = AppTextStyle.TitleLarge,
        override = AppTextOverride(fontWeight = FontWeight.Bold),
    )
}

@Suppress("DEPRECATION") // getPackageInfo(String, PackageInfoFlags) is API 33+.
private fun Context.versionName(): String =
    runCatching { packageManager.getPackageInfo(packageName, 0).versionName }.getOrNull().orEmpty()
