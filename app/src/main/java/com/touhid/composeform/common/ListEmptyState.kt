package com.touhid.composeform.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FindInPage
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.touhid.composeform.designsystem.components.icon.AppIcon
import com.touhid.composeform.designsystem.components.text.AppText
import com.touhid.composeform.designsystem.components.text.AppTextOverride
import com.touhid.composeform.designsystem.components.text.AppTextStyle
import com.touhid.composeform.designsystem.theme.AppSpacing
import com.touhid.composeform.designsystem.theme.StatusNeutral

// Shared by Lead Dashboard and Acquisition Approval List's empty states (status-filtered,
// searched, or otherwise) - a tinted circle behind a single icon, with a message below. icon
// defaults to a placeholder (Icons.Filled.FindInPage) until a custom SVG replaces it per the
// design; only the message differs per caller/context.
private val AccentIndigo = Color(0xFF675C92)
private val EmptyStateCircleSize = 120.dp
private val EmptyStateIconSize = 56.dp

@Composable
fun ListEmptyState(message: String, modifier: Modifier = Modifier, icon: ImageVector = Icons.Filled.FindInPage) {
    Column(
        modifier = modifier.fillMaxWidth().padding(AppSpacing.Medium),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .size(EmptyStateCircleSize)
                .background(color = AccentIndigo.copy(alpha = 0.12f), shape = CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            AppIcon(icon = icon, contentDescription = null, modifier = Modifier.size(EmptyStateIconSize), tint = AccentIndigo)
        }
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
