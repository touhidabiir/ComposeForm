package com.touhid.composeform.capture

import android.content.Context
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.touhid.composeform.designsystem.components.button.AppButton
import com.touhid.composeform.designsystem.components.button.AppShutterButton
import com.touhid.composeform.designsystem.components.button.AppStepperButton
import com.touhid.composeform.designsystem.components.icon.AppIconButton
import com.touhid.composeform.designsystem.components.text.AppText
import com.touhid.composeform.designsystem.theme.AppSpacing
import java.io.File

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

    LaunchedEffect(permission.state) {
        if (permission.state is CameraPermissionState.NotRequested) permission.request()
    }

    Column(modifier = modifier.fillMaxSize()) {
        Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
            when (permission.state) {
                CameraPermissionState.Granted -> {
                    val file = capturedFile
                    if (file == null) {
                        CameraPreview(modifier = Modifier.fillMaxSize(), onImageCaptureReady = { imageCapture = it })
                        AppShutterButton(
                            onClick = {
                                imageCapture?.let { capture ->
                                    takePicture(context, capture, onSaved = { capturedFile = it }, onError = {})
                                }
                            },
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(bottom = AppSpacing.Large),
                        )
                    } else {
                        val bitmap = remember(file) { BitmapFactory.decodeFile(file.path)?.asImageBitmap() }
                        bitmap?.let {
                            Image(
                                bitmap = it,
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop,
                            )
                        }
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
                                    file.delete()
                                    capturedFile = null
                                },
                            )
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
