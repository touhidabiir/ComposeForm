package com.touhid.composeform.designsystem.components.icon

import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.graphics.vector.ImageVector

// A non-interactive icon (unlike AppIconButton, which is always clickable) - lets callers outside
// :designsystem render a decorative or informational glyph without a raw Material3 dependency.
@Composable
fun AppIcon(
    icon: ImageVector,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    tint: Color = Color.Unspecified,
) {
    Icon(
        imageVector = icon,
        contentDescription = contentDescription,
        modifier = modifier,
        tint = if (tint.isSpecified) tint else LocalContentColor.current,
    )
}
