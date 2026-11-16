package com.touhid.composeform.camera

import androidx.camera.core.ImageCapture
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import com.touhid.composeform.designsystem.components.icon.AppIconButton
import com.touhid.composeform.designsystem.components.text.AppText
import com.touhid.composeform.designsystem.components.text.AppTextOverride
import com.touhid.composeform.designsystem.components.text.AppTextStyle
import com.touhid.composeform.designsystem.theme.AppSpacing
import kotlinx.coroutines.launch
import java.io.File

/**
 * A full camera to embed in any Compose screen: asks for the permission, shows the live camera, a
 * flash on/off button (when the camera has a flash) and a capture button, and writes each photo to
 * the file [newFile] returns.
 *
 * [lens] picks the camera, and the layout with it; the user can't switch. The back camera's image
 * fills the screen with the buttons over it. The front camera's image sits in a box on a light
 * screen, with the selfie instructions and the buttons below it. If the device lacks the requested
 * camera, the other one (and its layout) is used.
 *
 * [newFile] is called once per capture: return a new name each time, in app-specific storage
 * (`cacheDir`/`filesDir`, no storage permission needed). [onCaptured] runs only once the file is
 * completely written; [onError] when the capture failed (the partial file is already deleted).
 * [onBackClick] shows a back arrow at the top start; null hides it.
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
    onBackClick: (() -> Unit)? = null,
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
                onBackClick = onBackClick,
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
    onBackClick: (() -> Unit)?,
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
        lens = camera.lens,
        capturing = capturing,
        flash = if (camera.hasFlash) FlashToggle(on = flashOn, onToggle = { flashOn = !flashOn }) else null,
        onBackClick = onBackClick,
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

/** Text and controls on the front camera's light screen. */
private val OnLightColor = Color(0xFF362B73)

/**
 * What [PhotoCapture] draws, without any camera logic. Taking [cameraFeed] as a slot is what lets
 * the `@Preview`s pass a placeholder, since a preview has no camera.
 *
 * - [CameraLens.Back]: the feed fills the screen; the controls row sits over it at the bottom.
 * - [CameraLens.Front]: a light screen with the feed in a box from the top down to the
 *   instructions, then the controls row.
 *
 * Either way the back arrow (when [onBackClick] isn't null) sits at the top start, above the feed.
 */
@Composable
internal fun PhotoCaptureLayout(
    lens: CameraLens,
    capturing: Boolean,
    flash: FlashToggle?,
    onCaptureClick: () -> Unit,
    modifier: Modifier = Modifier,
    onBackClick: (() -> Unit)? = null,
    cameraFeed: @Composable () -> Unit,
) {
    val controlsColor = if (lens == CameraLens.Front) OnLightColor else Color.White

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(if (lens == CameraLens.Front) Color.White else Color.Black),
    ) {
        when (lens) {
            CameraLens.Back -> {
                cameraFeed()
                CaptureControls(
                    capturing = capturing,
                    flash = flash,
                    color = controlsColor,
                    onCaptureClick = onCaptureClick,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .navigationBarsPadding(),
                )
            }

            CameraLens.Front -> Column(Modifier.fillMaxSize().systemBarsPadding()) {
                // The "box" the instructions refer to: full width, from the top to the instructions.
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(vertical = AppSpacing.Medium),
                ) {
                    cameraFeed()
                }
                SelfieGuide()
                CaptureControls(
                    capturing = capturing,
                    flash = flash,
                    color = controlsColor,
                    onCaptureClick = onCaptureClick,
                )
            }
        }

        if (onBackClick != null) {
            AppIconButton(
                icon = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = stringResource(R.string.camera_back),
                onClick = onBackClick,
                tint = controlsColor,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .statusBarsPadding()
                    // The button's 48dp touch target centres the 24dp arrow; this puts the arrow
                    // itself about 16dp from the edges, like the XML layout's margins.
                    .padding(AppSpacing.ExtraSmall),
            )
        }
    }
}

/** The two selfie instructions under the front camera's image. */
@Composable
private fun SelfieGuide() {
    Column(Modifier.padding(horizontal = AppSpacing.Medium)) {
        AppText(
            text = stringResource(R.string.camera_selfie_guide_title),
            style = AppTextStyle.TitleMedium,
            override = AppTextOverride(color = OnLightColor),
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = AppSpacing.Small),
        )
        AppText(
            text = stringResource(R.string.camera_selfie_guide_message),
            style = AppTextStyle.BodyMedium,
            override = AppTextOverride(color = OnLightColor),
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = AppSpacing.Large),
        )
    }
}

/**
 * The bottom row: the capture button in the middle and, when the camera has one, the flash button
 * centred in the space to its left (the right side is left empty so the capture button stays
 * centred on screen).
 */
@Composable
private fun CaptureControls(
    capturing: Boolean,
    flash: FlashToggle?,
    color: Color,
    onCaptureClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = AppSpacing.Large),
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.weight(1f)) {
            if (flash != null) {
                AppIconButton(
                    icon = if (flash.on) Icons.Filled.FlashOn else Icons.Filled.FlashOff,
                    contentDescription = stringResource(
                        if (flash.on) R.string.camera_flash_turn_off else R.string.camera_flash_turn_on,
                    ),
                    onClick = flash.onToggle,
                    enabled = !capturing,
                    tint = color,
                )
            }
        }
        CaptureButton(enabled = !capturing, onClick = onCaptureClick, color = color)
        Spacer(Modifier.weight(1f))
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
