package com.touhid.composeform.capture

import android.content.Context
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

@Composable
internal fun CameraPreview(
    modifier: Modifier = Modifier,
    onImageCaptureReady: (ImageCapture) -> Unit,
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val previewView = remember { PreviewView(context) }

    LaunchedEffect(previewView, lifecycleOwner) {
        val cameraProvider = context.awaitCameraProvider()
        val preview = Preview.Builder().build().apply {
            surfaceProvider = previewView.surfaceProvider
        }
        val imageCapture = ImageCapture.Builder().build()
        cameraProvider.unbindAll()
        cameraProvider.bindToLifecycle(
            lifecycleOwner,
            CameraSelector.DEFAULT_BACK_CAMERA,
            preview,
            imageCapture,
        )
        onImageCaptureReady(imageCapture)
    }

    AndroidView(factory = { previewView }, modifier = modifier.fillMaxSize())
}

private suspend fun Context.awaitCameraProvider(): ProcessCameraProvider = suspendCancellableCoroutine { continuation ->
    val future = ProcessCameraProvider.getInstance(this)
    future.addListener(
        { continuation.resume(future.get()) },
        ContextCompat.getMainExecutor(this),
    )
}
