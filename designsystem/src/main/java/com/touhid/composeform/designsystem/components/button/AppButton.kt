package com.touhid.composeform.designsystem.components.button

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.touhid.composeform.designsystem.components.text.AppText
import com.touhid.composeform.designsystem.components.text.AppTextOverride
import com.touhid.composeform.designsystem.components.text.AppTextStyle
import com.touhid.composeform.designsystem.theme.AppSpacing
import com.touhid.composeform.designsystem.theme.StatusError
import com.touhid.composeform.designsystem.theme.StatusSuccess

// Lets callers ask for a semantically-colored action (e.g. Approve/Reject) without leaking raw
// Material3 ButtonColors through the public signature.
enum class AppButtonTone { Primary, Success, Danger }

@Composable
fun AppButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    tone: AppButtonTone = AppButtonTone.Primary,
    leadingIcon: (@Composable () -> Unit)? = null,
    textOverride: AppTextOverride = AppTextOverride(),
) {
    val colors = when (tone) {
        AppButtonTone.Primary -> ButtonDefaults.buttonColors()
        AppButtonTone.Success -> ButtonDefaults.buttonColors(containerColor = StatusSuccess, contentColor = Color.White)
        AppButtonTone.Danger -> ButtonDefaults.buttonColors(containerColor = StatusError, contentColor = Color.White)
    }
    Button(onClick = onClick, modifier = modifier, enabled = enabled, colors = colors) {
        leadingIcon?.let {
            it()
            Spacer(modifier = Modifier.width(AppSpacing.Small))
        }
        AppText(text = text, style = AppTextStyle.Label, override = textOverride)
    }
}

@Composable
fun AppOutlinedButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    tone: AppButtonTone = AppButtonTone.Primary,
    leadingIcon: (@Composable () -> Unit)? = null,
    textOverride: AppTextOverride = AppTextOverride(),
) {
    val contentColor = when (tone) {
        AppButtonTone.Primary -> MaterialTheme.colorScheme.primary
        AppButtonTone.Success -> StatusSuccess
        AppButtonTone.Danger -> StatusError
    }
    val borderColor = if (enabled) contentColor else contentColor.copy(alpha = 0.38f)
    OutlinedButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        colors = ButtonDefaults.outlinedButtonColors(contentColor = contentColor),
        border = BorderStroke(width = 1.dp, color = borderColor),
    ) {
        leadingIcon?.let {
            it()
            Spacer(modifier = Modifier.width(AppSpacing.Small))
        }
        AppText(text = text, style = AppTextStyle.Label, override = textOverride)
    }
}

// Matches Material3 ButtonDefaults.buttonColors()' disabled container/content colors,
// so AppButton and AppStepperButton look identical when disabled.
private const val DisabledContainerAlpha = 0.12f
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
    val disabledColor = MaterialTheme.colorScheme.onSurface
    Surface(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier,
        shape = RoundedCornerShape(percent = 50),
        color = if (enabled) containerColor else disabledColor.copy(alpha = DisabledContainerAlpha),
        contentColor = if (enabled) contentColor else disabledColor.copy(alpha = DisabledContentAlpha),
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
