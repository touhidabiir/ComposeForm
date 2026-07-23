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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver

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

    // Catches permission grants/revokes made from the system Settings screen while this screen
    // was backgrounded - the remembered state above would otherwise go stale until re-requested.
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME && isCameraPermissionGranted(context)) {
                state = CameraPermissionState.Granted
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
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
