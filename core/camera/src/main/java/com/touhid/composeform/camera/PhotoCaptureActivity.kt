package com.touhid.composeform.camera

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.touhid.composeform.designsystem.theme.ComposeFormAppTheme
import java.io.File

/**
 * Hosts [PhotoCapture] on a screen of its own, for callers that can't embed a composable (XML or
 * View-based screens). Never started directly: callers use [OpenPhotoCapture].
 *
 * Opens the camera the request asked for, writes the photo to the request's file and finishes with
 * `RESULT_OK` and that file's path. The result is `RESULT_CANCELED` otherwise (Back, or the
 * permission was denied). A failed capture keeps the camera open, so the user can try again.
 */
internal class PhotoCaptureActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val file = File(requireNotNull(intent.getStringExtra(EXTRA_FILE_PATH)) { "Missing $EXTRA_FILE_PATH" })
        val lens = intent.getStringExtra(EXTRA_LENS)
            ?.let { name -> CameraLens.entries.firstOrNull { it.name == name } }
            ?: CameraLens.Back

        setContent {
            ComposeFormAppTheme {
                PhotoCapture(
                    newFile = { file },
                    lens = lens,
                    onBackClick = { finish() }, // RESULT_CANCELED: the caller gets null
                    onCaptured = { saved ->
                        setResult(RESULT_OK, Intent().putExtra(EXTRA_FILE_PATH, saved.absolutePath))
                        finish()
                    },
                    onError = {
                        Toast.makeText(this, R.string.camera_capture_failed, Toast.LENGTH_SHORT).show()
                    },
                )
            }
        }
    }

    companion object {
        const val EXTRA_FILE_PATH = "com.touhid.composeform.camera.EXTRA_FILE_PATH"
        const val EXTRA_LENS = "com.touhid.composeform.camera.EXTRA_LENS"
    }
}
