package com.touhid.composeform.camera

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import com.touhid.composeform.designsystem.components.text.AppText
import com.touhid.composeform.designsystem.components.text.AppTextOverride
import com.touhid.composeform.designsystem.theme.AppSpacing
import com.touhid.composeform.designsystem.theme.ComposeFormTheme

// The camera screens use fixed colors whatever the theme, so there are no Light/Dark pairs:
// they'd look identical.

/** Stands in for [CameraFeed]: a preview has no camera. */
@Composable
private fun CameraFeedPlaceholder() {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFF3A3A3A), Color(0xFF111111)))),
    ) {
        AppText(
            text = "Camera feed",
            override = AppTextOverride(color = Color.White.copy(alpha = 0.6f)),
        )
    }
}

@Preview(name = "Back camera - flash off", device = Devices.PIXEL_7)
@Composable
private fun BackCameraFlashOffPreview() {
    ComposeFormTheme {
        PhotoCaptureLayout(
            lens = CameraLens.Back,
            capturing = false,
            flash = FlashToggle(on = false, onToggle = {}),
            onCaptureClick = {},
            onBackClick = {},
        ) { CameraFeedPlaceholder() }
    }
}

@Preview(name = "Back camera - flash on", device = Devices.PIXEL_7)
@Composable
private fun BackCameraFlashOnPreview() {
    ComposeFormTheme {
        PhotoCaptureLayout(
            lens = CameraLens.Back,
            capturing = false,
            flash = FlashToggle(on = true, onToggle = {}),
            onCaptureClick = {},
            onBackClick = {},
        ) { CameraFeedPlaceholder() }
    }
}

@Preview(name = "Back camera - capturing", device = Devices.PIXEL_7)
@Composable
private fun BackCameraCapturingPreview() {
    ComposeFormTheme {
        PhotoCaptureLayout(
            lens = CameraLens.Back,
            capturing = true,
            flash = FlashToggle(on = false, onToggle = {}),
            onCaptureClick = {},
            onBackClick = {},
        ) { CameraFeedPlaceholder() }
    }
}

@Preview(name = "Front camera (selfie) - no flash", device = Devices.PIXEL_7)
@Composable
private fun FrontCameraPreview() {
    ComposeFormTheme {
        PhotoCaptureLayout(
            lens = CameraLens.Front,
            capturing = false,
            flash = null,
            onCaptureClick = {},
            onBackClick = {},
        ) { CameraFeedPlaceholder() }
    }
}

@Preview(name = "Front camera (selfie) - with flash", device = Devices.PIXEL_7)
@Composable
private fun FrontCameraWithFlashPreview() {
    ComposeFormTheme {
        PhotoCaptureLayout(
            lens = CameraLens.Front,
            capturing = false,
            flash = FlashToggle(on = false, onToggle = {}),
            onCaptureClick = {},
            onBackClick = {},
        ) { CameraFeedPlaceholder() }
    }
}

@Preview(name = "No camera on device", device = Devices.PIXEL_7)
@Composable
private fun NoCameraMessagePreview() {
    ComposeFormTheme {
        NoCameraMessage()
    }
}

@Preview(name = "Permission", device = Devices.PIXEL_7)
@Composable
private fun CameraPermissionRequestPreview() {
    ComposeFormTheme {
        CameraPermissionRequest(permanentlyDenied = false, onAllowClick = {})
    }
}

@Preview(name = "Permission - permanently denied", device = Devices.PIXEL_7)
@Composable
private fun CameraPermissionRequestDeniedPreview() {
    ComposeFormTheme {
        CameraPermissionRequest(permanentlyDenied = true, onAllowClick = {})
    }
}

@Preview(name = "Flash + capture buttons", showBackground = true, backgroundColor = 0xFF000000)
@Composable
private fun CaptureButtonPreview() {
    ComposeFormTheme {
        // Flash + capture side by side, enabled and then disabled (while capturing).
        Row(
            horizontalArrangement = Arrangement.spacedBy(AppSpacing.Large),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(AppSpacing.Large),
        ) {
            FlashButton(flash = FlashToggle(on = true, onToggle = {}), enabled = true, color = Color.White)
            CaptureButton(enabled = true, onClick = {})
            FlashButton(flash = FlashToggle(on = false, onToggle = {}), enabled = false, color = Color.White)
            CaptureButton(enabled = false, onClick = {})
        }
    }
}
