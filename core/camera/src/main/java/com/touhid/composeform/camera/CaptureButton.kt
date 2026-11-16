package com.touhid.composeform.camera

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.touhid.composeform.designsystem.theme.AppSpacing

/** 64dp, as in the XML camera screen; [FlashButton]'s 40dp icon is sized to sit beside it. */
private val CaptureButtonSize = 64.dp

/**
 * The round shutter button: a ring around a disc, both in [color] (white over the camera image,
 * dark on the front camera's light screen), drawn entirely with modifiers (so it uses `Box`'s
 * modifier-only overload, with no content). Dimmed and not clickable while [enabled] is false,
 * which is how a second tap during a capture is ignored.
 */
@Composable
internal fun CaptureButton(
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    color: Color = Color.White,
) {
    Box(
        modifier = modifier
            .size(CaptureButtonSize)
            .border(4.dp, color, CircleShape)
            .padding(AppSpacing.Small)
            .clip(CircleShape)
            .background(if (enabled) color else color.copy(alpha = 0.4f))
            .clickable(
                enabled = enabled,
                role = Role.Button,
                onClickLabel = stringResource(R.string.camera_take_photo),
                onClick = onClick,
            ),
    )
}
