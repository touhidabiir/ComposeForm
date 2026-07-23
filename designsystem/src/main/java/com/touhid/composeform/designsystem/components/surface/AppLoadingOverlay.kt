package com.touhid.composeform.designsystem.components.surface

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.touhid.composeform.designsystem.components.text.AppText
import com.touhid.composeform.designsystem.components.text.AppTextOverride
import com.touhid.composeform.designsystem.theme.AppSpacing

@Composable
fun AppLoadingOverlay(
    message: String,
    modifier: Modifier = Modifier,
    messageOverride: AppTextOverride = AppTextOverride(),
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.35f))
            // Consumes taps so content underneath (e.g. a delete button) can't be triggered
            // while this scrim is shown, since Box draws on top but doesn't block input on its own.
            .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null, onClick = {}),
        contentAlignment = Alignment.Center,
    ) {
        Surface(
            shape = RoundedCornerShape(AppSpacing.Medium),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = AppSpacing.Small,
        ) {
            Row(
                modifier = Modifier.padding(horizontal = AppSpacing.Large, vertical = AppSpacing.Medium),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(AppSpacing.Medium),
            ) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp))
                AppText(text = message, override = messageOverride)
            }
        }
    }
}
