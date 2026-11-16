package com.touhid.composeform.camera

import android.content.Context
import androidx.camera.core.CameraSelector
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.google.common.util.concurrent.ListenableFuture
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/** Which camera to open. Chosen by the caller; the user can't switch it on the camera screen. */
enum class CameraLens { Back, Front }

internal fun CameraLens.toSelector(): CameraSelector = when (this) {
    CameraLens.Back -> CameraSelector.DEFAULT_BACK_CAMERA
    CameraLens.Front -> CameraSelector.DEFAULT_FRONT_CAMERA
}

private fun CameraLens.other(): CameraLens = when (this) {
    CameraLens.Back -> CameraLens.Front
    CameraLens.Front -> CameraLens.Back
}

/** The camera that will actually be used, and whether it can flash. */
internal data class ResolvedCamera(val lens: CameraLens, val hasFlash: Boolean)

/**
 * Looks up the requested camera on this device. Falls back to the other lens when the requested one
 * doesn't exist (e.g. a tablet with only a front camera), and returns null when there's no camera
 * at all. `hasFlash` is false for most front cameras, so the flash button is hidden for them.
 */
internal suspend fun resolveCamera(context: Context, requested: CameraLens): ResolvedCamera? {
    val cameras = ProcessCameraProvider.getInstance(context).await(context).availableCameraInfos

    fun infoFor(lens: CameraLens) = runCatching { lens.toSelector().filter(cameras).firstOrNull() }.getOrNull()

    val lens = listOf(requested, requested.other()).firstOrNull { infoFor(it) != null } ?: return null
    return ResolvedCamera(lens = lens, hasFlash = infoFor(lens)?.hasFlashUnit() == true)
}

private suspend fun <T> ListenableFuture<T>.await(context: Context): T =
    suspendCancellableCoroutine { continuation ->
        addListener(
            {
                runCatching { get() }.fold(
                    onSuccess = { continuation.resume(it) },
                    onFailure = { continuation.resumeWithException(it) },
                )
            },
            ContextCompat.getMainExecutor(context),
        )
    }
