package com.touhid.composeform.common

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FindInPage
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.touhid.composeform.designsystem.components.icon.AppIcon
import com.touhid.composeform.designsystem.components.text.AppText
import com.touhid.composeform.designsystem.components.text.AppTextOverride
import com.touhid.composeform.designsystem.components.text.AppTextStyle
import com.touhid.composeform.designsystem.theme.AppSpacing
import com.touhid.composeform.designsystem.theme.ComposeFormTheme
import com.touhid.composeform.designsystem.theme.StatusNeutral

// Shared by Lead Dashboard and Acquisition Approval List's empty states (status-filtered,
// searched, or otherwise) - a single icon with a message below; only the message differs per
// caller/context. icon is a composable slot (not a fixed ImageVector param) so any eventual
// custom asset - vector, SVG-derived drawable, PNG via painterResource, whatever - can be dropped
// in directly, already carrying its own circle backdrop baked in rather than one drawn here.
private val AccentIndigo = Color(0xFF675C92)
private val DefaultEmptyStateIconSize = 56.dp

@Composable
fun ListEmptyState(
    message: String,
    modifier: Modifier = Modifier,
    icon: @Composable () -> Unit = {
        AppIcon(icon = Icons.Filled.FindInPage, contentDescription = null, modifier = Modifier.size(DefaultEmptyStateIconSize), tint = AccentIndigo)
    },
) {
    Column(
        modifier = modifier.fillMaxWidth().padding(AppSpacing.Medium),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        icon()
        Spacer(modifier = Modifier.height(AppSpacing.Medium))
        AppText(
            text = message,
            style = AppTextStyle.BodyMedium,
            override = AppTextOverride(color = StatusNeutral),
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

// :app's ComposeFormAppTheme forces light-only at runtime, but :common can't depend on :app (that
// dependency would run backwards - :app assembles modules like this one, not the other way
// around), so this preview uses :designsystem's own ComposeFormTheme directly instead, with both
// Light/Dark variants like every other :designsystem-adjacent preview.
@Preview(name = "Light", showBackground = true)
@Preview(name = "Dark", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
private fun ListEmptyStatePreview() {
    ComposeFormTheme {
        ListEmptyState(message = "কোনো লিড পাওয়া যায়নি")
    }
}
