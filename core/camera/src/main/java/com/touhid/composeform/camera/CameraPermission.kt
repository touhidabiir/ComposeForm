package com.touhid.composeform.camera

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LifecycleResumeEffect
import com.touhid.composeform.designsystem.components.button.AppButton
import com.touhid.composeform.designsystem.components.text.AppText
import com.touhid.composeform.designsystem.components.text.AppTextOverride
import com.touhid.composeform.designsystem.components.text.AppTextStyle
import com.touhid.composeform.designsystem.theme.AppSpacing

/**
 * Shows [content] only once the camera permission is granted. Until then it asks for it (right
 * away, the first time) and shows [CameraPermissionRequest] explaining why. After a permanent
 * denial Android stops showing the dialog, so the button opens the app's Settings page instead;
 * the permission is checked again on every resume, for when the user comes back from there.
 */
@Composable
internal fun WithCameraPermission(content: @Composable () -> Unit) {
    val context = LocalContext.current
    var granted by remember { mutableStateOf(context.hasCameraPermission()) }
    var permanentlyDenied by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { isGranted ->
        granted = isGranted
        permanentlyDenied = !isGranted && context.findActivity()?.let {
            !ActivityCompat.shouldShowRequestPermissionRationale(it, Manifest.permission.CAMERA)
        } == true
    }

    LaunchedEffect(Unit) {
        if (!granted) permissionLauncher.launch(Manifest.permission.CAMERA)
    }

    LifecycleResumeEffect(Unit) {
        granted = context.hasCameraPermission()
        onPauseOrDispose { }
    }

    if (granted) {
        content()
    } else {
        CameraPermissionRequest(
            permanentlyDenied = permanentlyDenied,
            onAllowClick = {
                if (permanentlyDenied) {
                    context.startActivity(
                        Intent(
                            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                            Uri.fromParts("package", context.packageName, null),
                        ),
                    )
                } else {
                    permissionLauncher.launch(Manifest.permission.CAMERA)
                }
            },
        )
    }
}

/** The "why we need the camera" screen. Stateless, so it has its own `@Preview`s. */
@Composable
internal fun CameraPermissionRequest(
    permanentlyDenied: Boolean,
    onAllowClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(AppSpacing.Large),
        verticalArrangement = Arrangement.spacedBy(AppSpacing.Medium, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        AppText(
            text = stringResource(R.string.camera_permission_title),
            style = AppTextStyle.TitleLarge,
            override = AppTextOverride(color = Color.White),
            textAlign = TextAlign.Center,
        )
        AppText(
            text = stringResource(
                if (permanentlyDenied) {
                    R.string.camera_permission_message_settings
                } else {
                    R.string.camera_permission_message
                },
            ),
            override = AppTextOverride(color = Color.White.copy(alpha = 0.8f)),
            textAlign = TextAlign.Center,
        )
        AppButton(
            text = stringResource(
                if (permanentlyDenied) {
                    R.string.camera_permission_open_settings
                } else {
                    R.string.camera_permission_allow
                },
            ),
            onClick = onAllowClick,
        )
    }
}

private fun Context.hasCameraPermission(): Boolean =
    ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) ==
        PackageManager.PERMISSION_GRANTED

private tailrec fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}
