package com.touhid.composeform.designsystem.components.surface

import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.touhid.composeform.designsystem.theme.BrandPrimary

// A plain, non-blocking spinner for inline use - e.g. a screen's very first load, before there's
// any content or top bar action worth protecting behind a blocking dialog. Distinct from
// AppProgressDialog, which exists specifically to block interaction during a reload that has
// existing content behind it.
@Composable
fun AppProgressIndicator(modifier: Modifier = Modifier) {
    CircularProgressIndicator(modifier = modifier, color = BrandPrimary)
}
