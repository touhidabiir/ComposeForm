package com.touhid.composeform.camera

import androidx.camera.core.ImageCapture
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.touhid.composeform.designsystem.components.icon.AppIconButton
import com.touhid.composeform.designsystem.components.text.AppText
import com.touhid.composeform.designsystem.components.text.AppTextOverride
import com.touhid.composeform.designsystem.theme.AppSpacing
import kotlinx.coroutines.launch
import java.io.File

/**
 * A full camera to embed in any Compose screen: asks for the permission, shows the live camera,
 * a flash on/off button (when the camera has a flash) and a capture button, and writes each photo
 * to the file [newFile] returns.
 *
 * [lens] picks the front or back camera; the user can't switch it. If the device lacks that camera
 * the other one is used. [newFile] is called once per capture: return a new name each time, in
 * app-specific storage (`cacheDir`/`filesDir`, no storage permission needed). [onCaptured] runs
 * only once the file is completely written; [onError] when the capture failed (the partial file is
 * already deleted).
 *
 * Screens that aren't Compose open the same camera through [OpenPhotoCapture] instead.
 */
@Composable
fun PhotoCapture(
    newFile: () -> File,
    onCaptured: (File) -> Unit,
    onError: (Throwable) -> Unit,
    modifier: Modifier = Modifier,
    lens: CameraLens = CameraLens.Back,
) {
    WithCameraPermission {
        val context = LocalContext.current

        // Pending until CameraX reports the device's cameras; then the camera to use, or NoCamera
        // when the device has none.
        val camera by produceState<CameraLookup>(CameraLookup.Pending, lens) {
            value = resolveCamera(context, lens)?.let(CameraLookup::Found) ?: CameraLookup.NoCamera
        }

        when (val lookup = camera) {
            CameraLookup.Pending -> Box(modifier.fillMaxSize().background(Color.Black))
            CameraLookup.NoCamera -> NoCameraMessage(modifier)
            is CameraLookup.Found -> PhotoCaptureWithCamera(
                camera = lookup.camera,
                newFile = newFile,
                onCaptured = onCaptured,
                onError = onError,
                modifier = modifier,
            )
        }
    }
}

private sealed interface CameraLookup {
    data object Pending : CameraLookup
    data object NoCamera : CameraLookup
    data class Found(val camera: ResolvedCamera) : CameraLookup
}

/** [PhotoCapture] once the camera is known: owns the CameraX controller, the flash and capture state. */
@Composable
private fun PhotoCaptureWithCamera(
    camera: ResolvedCamera,
    newFile: () -> File,
    onCaptured: (File) -> Unit,
    onError: (Throwable) -> Unit,
    modifier: Modifier,
) {
    val context = LocalContext.current
    val controller = remember(camera.lens) {
        LifecycleCameraController(context).apply {
            setEnabledUseCases(CameraController.IMAGE_CAPTURE)
            cameraSelector = camera.lens.toSelector()
        }
    }
    val scope = rememberCoroutineScope()
    var capturing by remember { mutableStateOf(false) }
    // Saveable: survives rotation and process death, like the rest of the user's choices on screen.
    var flashOn by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(controller, flashOn) {
        controller.imageCaptureFlashMode =
            if (flashOn) ImageCapture.FLASH_MODE_ON else ImageCapture.FLASH_MODE_OFF
    }

    PhotoCaptureLayout(
        capturing = capturing,
        flash = if (camera.hasFlash) FlashToggle(on = flashOn, onToggle = { flashOn = !flashOn }) else null,
        modifier = modifier,
        onCaptureClick = {
            capturing = true
            scope.launch {
                runCatching { controller.capturePhotoToFile(context, newFile()) }
                    .onSuccess(onCaptured)
                    .onFailure(onError)
                capturing = false
            }
        },
        cameraFeed = { CameraFeed(controller, Modifier.fillMaxSize()) },
    )
}

/** The flash button's state. Null in [PhotoCaptureLayout] when the camera has no flash. */
internal data class FlashToggle(val on: Boolean, val onToggle: () -> Unit)

/**
 * What [PhotoCapture] draws, without any camera logic: [cameraFeed] fills the screen, the flash
 * button (only when [flash] isn't null) sits at the top end, and the [CaptureButton] at the bottom.
 * Taking the feed as a slot is what lets the `@Preview`s pass a placeholder, since a preview has no
 * camera.
 */
@Composable
internal fun PhotoCaptureLayout(
    capturing: Boolean,
    flash: FlashToggle?,
    onCaptureClick: () -> Unit,
    modifier: Modifier = Modifier,
    cameraFeed: @Composable () -> Unit,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black),
    ) {
        cameraFeed()

        if (flash != null) {
            AppIconButton(
                icon = if (flash.on) Icons.Filled.FlashOn else Icons.Filled.FlashOff,
                contentDescription = stringResource(
                    if (flash.on) R.string.camera_flash_turn_off else R.string.camera_flash_turn_on,
                ),
                onClick = flash.onToggle,
                enabled = !capturing,
                tint = Color.White,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .statusBarsPadding()
                    .padding(AppSpacing.Small),
            )
        }

        CaptureButton(
            enabled = !capturing,
            onClick = onCaptureClick,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp),
        )
    }
}

/** Shown on a device with no camera at all (the manifest lets those devices install the app). */
@Composable
internal fun NoCameraMessage(modifier: Modifier = Modifier) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(AppSpacing.Large),
    ) {
        AppText(
            text = stringResource(R.string.camera_unavailable),
            override = AppTextOverride(color = Color.White),
            textAlign = TextAlign.Center,
        )
    }
}
