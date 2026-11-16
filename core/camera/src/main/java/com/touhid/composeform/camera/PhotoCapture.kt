package com.touhid.composeform.camera

import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import java.io.File

/**
 * A full camera to embed in any Compose screen: asks for the permission, shows the live camera
 * and a capture button, and writes each photo to the file [newFile] returns.
 *
 * [newFile] is called once per capture: return a new name each time, in app-specific storage
 * (`cacheDir`/`filesDir`, no storage permission needed). [onCaptured] runs only once the file is
 * completely written; [onError] when the capture failed (the partial file is already deleted).
 *
 * Screens that aren't Compose open the same camera through [OpenPhotoCapture] instead.
 */
@Composable
fun PhotoCapture(
    newFile: () -> File,
    onCaptured: (File) -> Unit,
    onError: (Throwable) -> Unit,
    modifier: Modifier = Modifier,
) {
    WithCameraPermission {
        val context = LocalContext.current
        val controller = remember {
            LifecycleCameraController(context).apply {
                setEnabledUseCases(CameraController.IMAGE_CAPTURE)
            }
        }
        val scope = rememberCoroutineScope()
        var capturing by remember { mutableStateOf(false) }

        PhotoCaptureLayout(
            capturing = capturing,
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
}

/**
 * What [PhotoCapture] draws, without any camera logic: [cameraFeed] fills the screen and the
 * [CaptureButton] sits at the bottom. Taking the feed as a slot is what lets the `@Preview`s pass a
 * placeholder, since a preview has no camera.
 */
@Composable
internal fun PhotoCaptureLayout(
    capturing: Boolean,
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

        CaptureButton(
            enabled = !capturing,
            onClick = onCaptureClick,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp),
        )
    }
}
