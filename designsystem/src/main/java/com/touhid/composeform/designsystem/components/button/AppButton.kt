package com.touhid.composeform.designsystem.components.button

import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.touhid.composeform.designsystem.components.text.AppText
import com.touhid.composeform.designsystem.components.text.AppTextOverride
import com.touhid.composeform.designsystem.components.text.AppTextStyle

@Composable
fun AppButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    textOverride: AppTextOverride = AppTextOverride(),
) {
    Button(onClick = onClick, modifier = modifier, enabled = enabled) {
        AppText(text = text, style = AppTextStyle.Label, override = textOverride)
    }
}

@Composable
fun AppOutlinedButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    textOverride: AppTextOverride = AppTextOverride(),
) {
    OutlinedButton(onClick = onClick, modifier = modifier, enabled = enabled) {
        AppText(text = text, style = AppTextStyle.Label, override = textOverride)
    }
}
