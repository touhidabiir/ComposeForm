package com.touhid.composeform.capture

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.touhid.composeform.designsystem.components.button.AppButton
import com.touhid.composeform.designsystem.components.button.AppShutterButton
import com.touhid.composeform.designsystem.components.button.AppStepperButton
import com.touhid.composeform.designsystem.components.icon.AppIconButton
import com.touhid.composeform.designsystem.components.surface.AppLoadingOverlay
import com.touhid.composeform.designsystem.components.text.AppText
import com.touhid.composeform.designsystem.theme.AppSpacing
import java.io.File
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

// Downsamples decoded bitmaps against this target so a multi-megapixel camera JPEG isn't fully
// decoded at native resolution just to fill a phone-sized preview slot.
private const val MaxDecodedDimensionPx = 1080

@Composable
internal fun ImagePickerCameraScreen(
    isUploading: Boolean,
    nextStepLabel: String,
    progressText: String?,
    onNextStep: (File) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val permission = rememberCameraPermissionController()
    var imageCapture by remember { mutableStateOf<ImageCapture?>(null) }
    var capturedFile by remember { mutableStateOf<File?>(null) }
    var cameraError by remember { mutableStateOf<String?>(null) }
    var captureError by remember { mutableStateOf<String?>(null) }
    var deleteError by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(permission.state) {
        if (permission.state is CameraPermissionState.NotRequested) permission.request()
    }

    Column(modifier = modifier.fillMaxSize()) {
        Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
            when (permission.state) {
                CameraPermissionState.Granted -> {
                    val file = capturedFile
                    if (file == null) {
                        if (cameraError == null) {
                            CameraPreview(
                                modifier = Modifier.fillMaxSize(),
                                onImageCaptureReady = { imageCapture = it },
                                onError = { cameraError = it.message ?: "Could not start the camera" },
                            )
                            AppShutterButton(
                                contentDescription = "Take photo",
                                onClick = {
                                    imageCapture?.let { capture ->
                                        captureError = null
                                        takePicture(
                                            context = context,
                                            imageCapture = capture,
                                            onSaved = { capturedFile = it },
                                            onError = { captureError = it.message ?: "Could not capture the photo, try again" },
                                        )
                                    }
                                },
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .padding(bottom = AppSpacing.Large),
                            )
                            captureError?.let { message ->
                                Box(modifier = Modifier.align(Alignment.TopCenter).padding(AppSpacing.Medium)) {
                                    AppText(text = message, color = Color.White)
                                }
                            }
                        } else {
                            PermissionRationale(message = cameraError.orEmpty(), actionLabel = "Retry", onAction = { cameraError = null })
                        }
                    } else {
                        val imageState = rememberCapturedImageState(file)
                        when (imageState) {
                            CapturedImageState.Loading -> {
                                AppLoadingOverlay(message = "Loading photo…")
                            }

                            is CapturedImageState.Success -> {
                                Image(
                                    bitmap = imageState.bitmap,
                                    contentDescription = null,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop,
                                )
                            }

                            CapturedImageState.Error -> {
                                PermissionRationale(
                                    message = "Could not load the photo you just took.",
                                    actionLabel = "Retake",
                                    onAction = {
                                        file.delete()
                                        capturedFile = null
                                    },
                                )
                            }
                        }
                        if (imageState != CapturedImageState.Error) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .padding(bottom = AppSpacing.Large)
                                    .size(48.dp)
                                    .background(Color.Black.copy(alpha = 0.45f), CircleShape),
                                contentAlignment = Alignment.Center,
                            ) {
                                AppIconButton(
                                    icon = Icons.Filled.Delete,
                                    contentDescription = "Delete photo",
                                    tint = Color.White,
                                    onClick = {
                                        if (file.delete()) {
                                            capturedFile = null
                                            deleteError = null
                                        } else {
                                            deleteError = "Could not delete the photo, please try again"
                                        }
                                    },
                                )
                            }
                            deleteError?.let { message ->
                                Box(modifier = Modifier.align(Alignment.TopCenter).padding(AppSpacing.Medium)) {
                                    AppText(text = message, color = Color.White)
                                }
                            }
                        }
                    }
                }

                CameraPermissionState.NotRequested, CameraPermissionState.Denied -> {
                    PermissionRationale(
                        message = "Camera access is needed to take a photo.",
                        actionLabel = "Grant permission",
                        onAction = permission.request,
                    )
                }

                CameraPermissionState.PermanentlyDenied -> {
                    PermissionRationale(
                        message = "Camera permission was denied. Enable it from app settings to continue.",
                        actionLabel = "Open Settings",
                        onAction = { openAppSettings(context) },
                    )
                }
            }
        }
        if (capturedFile != null && permission.state == CameraPermissionState.Granted) {
            AppStepperButton(
                label = nextStepLabel,
                progressText = progressText,
                enabled = !isUploading,
                onClick = { capturedFile?.let(onNextStep) },
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun PermissionRationale(message: String, actionLabel: String, onAction: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(AppSpacing.Large),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        AppText(text = message, modifier = Modifier.padding(bottom = AppSpacing.Medium))
        AppButton(text = actionLabel, onClick = onAction)
    }
}

private sealed interface CapturedImageState {
    data object Loading : CapturedImageState
    data class Success(val bitmap: ImageBitmap) : CapturedImageState
    data object Error : CapturedImageState
}

// Decoding a full-resolution camera JPEG is expensive enough to jank the UI thread, so this runs
// on Dispatchers.IO and downsamples to MaxDecodedDimensionPx before ever allocating the full bitmap.
@Composable
private fun rememberCapturedImageState(file: File): CapturedImageState =
    produceState<CapturedImageState>(initialValue = CapturedImageState.Loading, file) {
        value = withContext(Dispatchers.IO) {
            decodeSampledBitmap(file.path, MaxDecodedDimensionPx)
                ?.let { CapturedImageState.Success(it.asImageBitmap()) }
                ?: CapturedImageState.Error
        }
    }.value

private fun decodeSampledBitmap(path: String, maxDimensionPx: Int): Bitmap? {
    val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
    BitmapFactory.decodeFile(path, bounds)
    if (bounds.outWidth <= 0 || bounds.outHeight <= 0) return null

    var sampleSize = 1
    while (bounds.outWidth / (sampleSize * 2) >= maxDimensionPx || bounds.outHeight / (sampleSize * 2) >= maxDimensionPx) {
        sampleSize *= 2
    }
    val options = BitmapFactory.Options().apply { inSampleSize = sampleSize }
    return BitmapFactory.decodeFile(path, options)
}

private fun takePicture(
    context: Context,
    imageCapture: ImageCapture,
    onSaved: (File) -> Unit,
    onError: (ImageCaptureException) -> Unit,
) {
    val dir = File(context.cacheDir, "images").apply { mkdirs() }
    val file = File(dir, "capture_${System.currentTimeMillis()}.jpg")
    val outputOptions = ImageCapture.OutputFileOptions.Builder(file).build()
    imageCapture.takePicture(
        outputOptions,
        ContextCompat.getMainExecutor(context),
        object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) = onSaved(file)
            override fun onError(exception: ImageCaptureException) = onError(exception)
        },
    )
}
