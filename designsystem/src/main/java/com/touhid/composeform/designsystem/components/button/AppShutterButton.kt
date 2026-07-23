package com.touhid.composeform.designsystem.components.button

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun AppShutterButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    Surface(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.size(72.dp),
        shape = CircleShape,
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(4.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)),
    ) {
        Box(modifier = Modifier.padding(6.dp)) {
            Surface(
                shape = CircleShape,
                color = if (enabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                modifier = Modifier.fillMaxSize(),
            ) {}
        }
    }
}
