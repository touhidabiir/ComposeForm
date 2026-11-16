package com.touhid.composeform.camera

import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.LocalLifecycleOwner

/**
 * The live image from the camera (CameraX's `PreviewView`, which has no Compose equivalent).
 *
 * Binds the camera to the current lifecycle, so it stops and restarts with the screen, and unbinds
 * when this leaves the composition. Named "feed" rather than "preview" so it isn't confused with
 * Compose's `@Preview` functions, which can't show a real camera (see PhotoCapturePreviews.kt).
 */
@Composable
internal fun CameraFeed(
    controller: LifecycleCameraController,
    modifier: Modifier = Modifier,
) {
    val lifecycleOwner = LocalLifecycleOwner.current

    AndroidView(
        modifier = modifier,
        factory = { context ->
            PreviewView(context).apply {
                scaleType = PreviewView.ScaleType.FILL_CENTER
                // TextureView-based: behaves correctly inside Compose layouts and animations.
                implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                this.controller = controller
            }
        },
    )

    DisposableEffect(controller, lifecycleOwner) {
        controller.bindToLifecycle(lifecycleOwner)
        onDispose { controller.unbind() }
    }
}
