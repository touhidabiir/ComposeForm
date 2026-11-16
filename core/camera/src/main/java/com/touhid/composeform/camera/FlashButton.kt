package com.touhid.composeform.camera

import androidx.annotation.StringRes
import androidx.camera.core.ImageCapture
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FlashAuto
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.touhid.composeform.designsystem.components.icon.AppIcon

/**
 * When the flash fires. Each tap on [FlashButton] moves to [next]: Off -> Auto -> On -> Off.
 * Auto lets the camera decide at capture time, from the light in the scene.
 */
internal enum class FlashMode(
    val icon: ImageVector,
    @StringRes val labelRes: Int,
    val imageCaptureMode: Int,
) {
    Off(Icons.Filled.FlashOff, R.string.camera_flash_off, ImageCapture.FLASH_MODE_OFF),
    Auto(Icons.Filled.FlashAuto, R.string.camera_flash_auto, ImageCapture.FLASH_MODE_AUTO),
    On(Icons.Filled.FlashOn, R.string.camera_flash_on, ImageCapture.FLASH_MODE_ON),
    ;

    fun next(): FlashMode = entries[(ordinal + 1) % entries.size]
}

/** The flash button's state: the current [mode], and what a tap does. */
internal data class FlashControl(val mode: FlashMode, val onClick: () -> Unit)

/** The flash icon's size: 40dp, as in the XML camera screen, to sit in balance with [CaptureButton]. */
private val FlashIconSize = 40.dp

/** The tappable area around the icon: a little larger than the icon, never below the 48dp minimum. */
private val FlashTouchSize = 56.dp

/**
 * The flash button: a 40dp icon showing the current [FlashMode] (off, auto or on) in [color]; a
 * tap moves to the next mode. Built here from `AppIcon` rather than with `AppIconButton`, whose
 * icon is fixed at the standard 24dp. Dimmed and not clickable while [enabled] is false.
 */
@Composable
internal fun FlashButton(
    flash: FlashControl,
    enabled: Boolean,
    color: Color,
    modifier: Modifier = Modifier,
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(FlashTouchSize)
            .clip(CircleShape)
            .clickable(
                enabled = enabled,
                role = Role.Button,
                onClickLabel = stringResource(R.string.camera_flash_change),
                onClick = flash.onClick,
            ),
    ) {
        AppIcon(
            icon = flash.mode.icon,
            contentDescription = stringResource(flash.mode.labelRes),
            tint = if (enabled) color else color.copy(alpha = 0.4f),
            modifier = Modifier.size(FlashIconSize),
        )
    }
}
