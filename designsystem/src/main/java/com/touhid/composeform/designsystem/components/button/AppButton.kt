package com.touhid.composeform.designsystem.components.button

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.touhid.composeform.designsystem.components.text.AppText
import com.touhid.composeform.designsystem.components.text.AppTextOverride
import com.touhid.composeform.designsystem.components.text.AppTextStyle
import com.touhid.composeform.designsystem.theme.AppSpacing

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

private const val DisabledContentAlpha = 0.38f

@Composable
fun AppStepperButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    progressText: String? = null,
    textOverride: AppTextOverride = AppTextOverride(),
) {
    val containerColor = MaterialTheme.colorScheme.primary
    val contentColor = MaterialTheme.colorScheme.onPrimary
    Surface(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier,
        shape = RoundedCornerShape(percent = 50),
        color = if (enabled) containerColor else containerColor.copy(alpha = DisabledContentAlpha),
        contentColor = if (enabled) contentColor else contentColor.copy(alpha = DisabledContentAlpha),
    ) {
        Row(
            modifier = Modifier.height(IntrinsicSize.Min),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (progressText != null) {
                Box(modifier = Modifier.padding(horizontal = AppSpacing.Medium, vertical = AppSpacing.Small)) {
                    AppText(text = progressText, style = AppTextStyle.Label)
                }
                VerticalDivider(modifier = Modifier.fillMaxHeight().padding(vertical = AppSpacing.Small))
            }
            Row(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = AppSpacing.Medium, vertical = AppSpacing.Small),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                AppText(text = label, style = AppTextStyle.Label, override = textOverride)
                Icon(imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null)
            }
        }
    }
}
