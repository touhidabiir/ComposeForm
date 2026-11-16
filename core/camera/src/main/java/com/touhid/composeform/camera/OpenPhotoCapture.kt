package com.touhid.composeform.camera

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import java.io.File

/**
 * Opens the camera on a screen of its own and saves the photo to the given file. For screens that
 * can't embed [PhotoCapture], e.g. XML Activities and Fragments:
 *
 * ```
 * private val takePhoto = registerForActivityResult(OpenPhotoCapture()) { file -> ... }
 * takePhoto.launch(File(cacheDir, "photo_${System.currentTimeMillis()}.jpg"))
 * ```
 *
 * Result: the saved file, or null when the user left without taking one.
 */
class OpenPhotoCapture : ActivityResultContract<File, File?>() {

    override fun createIntent(context: Context, input: File): Intent =
        Intent(context, PhotoCaptureActivity::class.java)
            .putExtra(PhotoCaptureActivity.EXTRA_FILE_PATH, input.absolutePath)

    override fun parseResult(resultCode: Int, intent: Intent?): File? =
        intent?.getStringExtra(PhotoCaptureActivity.EXTRA_FILE_PATH)
            ?.takeIf { resultCode == Activity.RESULT_OK }
            ?.let(::File)
}
