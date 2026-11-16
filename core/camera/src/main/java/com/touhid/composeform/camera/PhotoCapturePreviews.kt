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

// The camera screens are always black, so there are no Light/Dark pairs: they'd look identical.

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

@Preview(name = "Ready", device = Devices.PIXEL_7)
@Composable
private fun PhotoCaptureReadyPreview() {
    ComposeFormTheme {
        PhotoCaptureLayout(capturing = false, onCaptureClick = {}) { CameraFeedPlaceholder() }
    }
}

@Preview(name = "Capturing", device = Devices.PIXEL_7)
@Composable
private fun PhotoCaptureCapturingPreview() {
    ComposeFormTheme {
        PhotoCaptureLayout(capturing = true, onCaptureClick = {}) { CameraFeedPlaceholder() }
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

@Preview(name = "Capture button", showBackground = true, backgroundColor = 0xFF000000)
@Composable
private fun CaptureButtonPreview() {
    ComposeFormTheme {
        Row(
            horizontalArrangement = Arrangement.spacedBy(AppSpacing.Large),
            modifier = Modifier.padding(AppSpacing.Large),
        ) {
            CaptureButton(enabled = true, onClick = {})
            CaptureButton(enabled = false, onClick = {})
        }
    }
}
