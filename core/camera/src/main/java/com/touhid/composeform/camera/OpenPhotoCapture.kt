package com.touhid.composeform.camera

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import java.io.File

/**
 * What to capture: the [file] to write the photo to (a new name per photo, in app-specific storage)
 * and which camera to open. The user can't switch cameras on the capture screen.
 */
data class PhotoCaptureRequest @JvmOverloads constructor(
    val file: File,
    val lens: CameraLens = CameraLens.Back,
)

/**
 * Opens the camera on a screen of its own and saves the photo to the request's file. For screens
 * that can't embed [PhotoCapture], e.g. XML Activities and Fragments:
 *
 * ```
 * private val takePhoto = registerForActivityResult(OpenPhotoCapture()) { file -> ... }
 * takePhoto.launch(PhotoCaptureRequest(File(cacheDir, "photo_${System.currentTimeMillis()}.jpg"), CameraLens.Front))
 * ```
 *
 * Result: the saved file, or null when the user left without taking one.
 */
class OpenPhotoCapture : ActivityResultContract<PhotoCaptureRequest, File?>() {

    override fun createIntent(context: Context, input: PhotoCaptureRequest): Intent =
        Intent(context, PhotoCaptureActivity::class.java)
            .putExtra(PhotoCaptureActivity.EXTRA_FILE_PATH, input.file.absolutePath)
            .putExtra(PhotoCaptureActivity.EXTRA_LENS, input.lens.name)

    override fun parseResult(resultCode: Int, intent: Intent?): File? =
        intent?.getStringExtra(PhotoCaptureActivity.EXTRA_FILE_PATH)
            ?.takeIf { resultCode == Activity.RESULT_OK }
            ?.let(::File)
}
