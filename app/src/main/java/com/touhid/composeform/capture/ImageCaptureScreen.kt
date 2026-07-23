package com.touhid.composeform.capture

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.touhid.composeform.designsystem.components.layout.AppScaffold
import com.touhid.composeform.designsystem.components.surface.AppLoadingOverlay
import com.touhid.composeform.designsystem.components.surface.AppTopBar
import com.touhid.composeform.flow.DemoFormApi
import com.touhid.composeform.formbuilder.schema.FormField
import com.touhid.composeform.formbuilder.schema.FormValue
import kotlinx.coroutines.launch

private sealed interface CaptureStep {
    data object Info : CaptureStep
    data object Camera : CaptureStep
}

/**
 * Owns the whole "take a photo" flow for a single [FormField.ImagePicker]: an info screen, a
 * CameraX capture screen, and the (fake) upload. Only sets the field's value on [onResult] - the
 * surrounding form page's own Submit button still drives the actual submit/next-page transition.
 */
@Composable
fun ImageCaptureScreen(
    field: FormField.ImagePicker,
    onResult: (FormValue.Image) -> Unit,
    onCancel: () -> Unit,
) {
    var step by remember { mutableStateOf<CaptureStep>(CaptureStep.Info) }
    var isUploading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    BackHandler(enabled = step is CaptureStep.Camera) { step = CaptureStep.Info }

    AppScaffold(topBar = { scrollBehavior ->
        AppTopBar(
            title = field.screenTitle,
            scrollBehavior = scrollBehavior,
            navigationIcon = Icons.AutoMirrored.Filled.ArrowBack,
            onNavigationClick = { if (step is CaptureStep.Camera) step = CaptureStep.Info else onCancel() },
        )
    }) {
        Box(modifier = Modifier.fillMaxSize()) {
            when (step) {
                CaptureStep.Info -> ImagePickerInfoScreen(
                    field = field,
                    onTakePhoto = { step = CaptureStep.Camera },
                )

                CaptureStep.Camera -> ImagePickerCameraScreen(
                    isUploading = isUploading,
                    nextStepLabel = field.nextStepLabel,
                    progressText = field.progressText,
                    onNextStep = { file ->
                        isUploading = true
                        scope.launch {
                            val url = DemoFormApi.uploadImage(field.uploadUrl, file.absolutePath)
                            isUploading = false
                            onResult(FormValue.Image(url = url, localPath = file.absolutePath))
                        }
                    },
                )
            }
            if (isUploading) {
                AppLoadingOverlay(message = field.uploadingMessage)
            }
        }
    }
}
