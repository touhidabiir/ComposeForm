package com.touhid.composeform.designsystem.components.icon

import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

@Composable
fun AppIconButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    tint: Color = Color.Unspecified,
) {
    IconButton(onClick = onClick, modifier = modifier, enabled = enabled) {
        Icon(imageVector = icon, contentDescription = contentDescription, tint = tint)
    }
}
