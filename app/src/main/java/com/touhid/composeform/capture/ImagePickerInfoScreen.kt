package com.touhid.composeform.capture

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.touhid.composeform.R
import com.touhid.composeform.designsystem.components.button.AppButton
import com.touhid.composeform.designsystem.components.text.AppHtmlText
import com.touhid.composeform.designsystem.theme.AppSpacing
import com.touhid.composeform.formbuilder.schema.FormField

// field.demoImageUrl is intentionally unused - no image-loading library (e.g. Coil) exists in this
// demo project, so the example photo is a bundled local placeholder instead of a fetched one.
@Composable
internal fun ImagePickerInfoScreen(
    field: FormField.ImagePicker,
    onTakePhoto: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(AppSpacing.Medium),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(AppSpacing.Medium),
        ) {
            Image(
                painter = painterResource(R.drawable.ic_demo_receipt_sample),
                contentDescription = null,
                modifier = Modifier.size(200.dp),
            )
            AppHtmlText(html = field.instructionsHtml, modifier = Modifier.fillMaxWidth())
        }
        AppButton(
            text = field.takePhotoLabel,
            onClick = onTakePhoto,
            modifier = Modifier
                .fillMaxWidth()
                .padding(AppSpacing.Medium),
        )
    }
}
