package com.touhid.composeform.camera

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.touhid.composeform.designsystem.components.icon.AppIcon

/** The flash icon's size: 40dp, as in the XML camera screen, to sit in balance with [CaptureButton]. */
private val FlashIconSize = 40.dp

/** The tappable area around the icon: a little larger than the icon, never below the 48dp minimum. */
private val FlashTouchSize = 56.dp

/**
 * The flash on/off button: a 40dp flash icon (on or off glyph, so the user sees the current state)
 * in [color]. Built here from `AppIcon` rather than with `AppIconButton`, whose icon is fixed at the
 * standard 24dp. Dimmed and not clickable while [enabled] is false.
 */
@Composable
internal fun FlashButton(
    flash: FlashToggle,
    enabled: Boolean,
    color: Color,
    modifier: Modifier = Modifier,
) {
    val label = stringResource(
        if (flash.on) R.string.camera_flash_turn_off else R.string.camera_flash_turn_on,
    )
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(FlashTouchSize)
            .clip(CircleShape)
            .clickable(enabled = enabled, role = Role.Button, onClickLabel = label, onClick = flash.onToggle),
    ) {
        AppIcon(
            icon = if (flash.on) Icons.Filled.FlashOn else Icons.Filled.FlashOff,
            contentDescription = label,
            tint = if (enabled) color else color.copy(alpha = 0.4f),
            modifier = Modifier.size(FlashIconSize),
        )
    }
}
