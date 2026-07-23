package com.touhid.composeform.capture

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat

internal sealed interface CameraPermissionState {
    data object Granted : CameraPermissionState
    data object NotRequested : CameraPermissionState
    data object Denied : CameraPermissionState
    data object PermanentlyDenied : CameraPermissionState
}

internal class CameraPermissionController(
    val state: CameraPermissionState,
    val request: () -> Unit,
)

@Composable
internal fun rememberCameraPermissionController(): CameraPermissionController {
    val context = LocalContext.current
    val activity = context as? Activity
    var state by remember {
        mutableStateOf(
            if (isCameraPermissionGranted(context)) {
                CameraPermissionState.Granted
            } else {
                CameraPermissionState.NotRequested
            },
        )
    }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        state = when {
            granted -> CameraPermissionState.Granted
            activity != null && !activity.shouldShowRequestPermissionRationale(Manifest.permission.CAMERA) ->
                CameraPermissionState.PermanentlyDenied
            else -> CameraPermissionState.Denied
        }
    }
    return remember(launcher, state) {
        CameraPermissionController(state = state, request = { launcher.launch(Manifest.permission.CAMERA) })
    }
}

private fun isCameraPermissionGranted(context: Context): Boolean =
    ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED

internal fun openAppSettings(context: Context) {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
        data = Uri.fromParts("package", context.packageName, null)
    }
    context.startActivity(intent)
}
