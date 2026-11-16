package com.touhid.composeform.camera

import android.content.Context
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.view.LifecycleCameraController
import androidx.core.content.ContextCompat
import kotlinx.coroutines.suspendCancellableCoroutine
import java.io.File
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * Takes one photo and writes it to [file]: the only place in this module that writes a photo.
 *
 * Resumes only from `onImageSaved`, once the file is completely written, so a caller never reads
 * a half-written image. On failure the partial file is deleted and the error is thrown.
 */
internal suspend fun LifecycleCameraController.capturePhotoToFile(context: Context, file: File): File =
    suspendCancellableCoroutine { continuation ->
        takePicture(
            ImageCapture.OutputFileOptions.Builder(file).build(),
            ContextCompat.getMainExecutor(context),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    continuation.resume(file)
                }

                override fun onError(exception: ImageCaptureException) {
                    file.delete()
                    continuation.resumeWithException(exception)
                }
            },
        )
    }
