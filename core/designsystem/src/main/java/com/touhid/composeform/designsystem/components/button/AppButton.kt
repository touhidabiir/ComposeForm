package com.touhid.composeform.designsystem.components.button

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.unit.dp
import com.touhid.composeform.designsystem.components.text.AppText
import com.touhid.composeform.designsystem.components.text.AppTextOverride
import com.touhid.composeform.designsystem.components.text.AppTextStyle
import com.touhid.composeform.designsystem.theme.AppSpacing
import com.touhid.composeform.designsystem.theme.StatusError
import com.touhid.composeform.designsystem.theme.StatusSuccess

// Lets callers ask for a semantically-colored action (e.g. Approve/Reject) without leaking raw
// Material3 ButtonColors through the public signature.
enum class AppButtonStyle { Primary, Success, Danger }

// A disabled button's container is its own enabled container color at this alpha, not a fixed
// color - so a disabled Danger button still reads as a faint red, a disabled Success button a
// faint green, etc. Shared by AppButton, AppOutlinedButton and AppStepperButton below.
private const val DisabledContainerAlpha = 0.12f

@Composable
fun AppButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    buttonType: AppButtonStyle = AppButtonStyle.Primary,
    // Escape hatch for a one-off brand color that doesn't warrant its own AppButtonStyle case -
    // no-op (falls back to buttonType) when left unspecified, same convention as AppTextOverride.
    containerColor: Color = Color.Unspecified,
    contentColor: Color = Color.Unspecified,
    leadingIcon: (@Composable () -> Unit)? = null,
    textOverride: AppTextOverride = AppTextOverride(),
) {
    val styleColors = when (buttonType) {
        AppButtonStyle.Primary -> ButtonDefaults.buttonColors()
        AppButtonStyle.Success -> ButtonDefaults.buttonColors(containerColor = StatusSuccess, contentColor = Color.White)
        AppButtonStyle.Danger -> ButtonDefaults.buttonColors(containerColor = StatusError, contentColor = Color.White)
    }
    val resolvedContainerColor = containerColor.takeOrElse { styleColors.containerColor }
    // Defaults content to white when only a custom containerColor is given - a colored fill
    // almost always wants light text, and the caller can still pass contentColor to override that.
    val colors = ButtonDefaults.buttonColors(
        containerColor = resolvedContainerColor,
        contentColor = contentColor.takeOrElse { if (containerColor.isSpecified) Color.White else styleColors.contentColor },
        disabledContainerColor = resolvedContainerColor.copy(alpha = DisabledContainerAlpha),
    )
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
    buttonType: AppButtonStyle = AppButtonStyle.Primary,
    // Same escape hatch as AppButton's containerColor/contentColor - no fill here, so one color
    // covers both the text and the border.
    contentColor: Color = Color.Unspecified,
    leadingIcon: (@Composable () -> Unit)? = null,
    textOverride: AppTextOverride = AppTextOverride(),
) {
    val styleColor = when (buttonType) {
        AppButtonStyle.Primary -> MaterialTheme.colorScheme.primary
        AppButtonStyle.Success -> StatusSuccess
        AppButtonStyle.Danger -> StatusError
    }
    val resolvedColor = contentColor.takeOrElse { styleColor }
    val borderColor = if (enabled) resolvedColor else resolvedColor.copy(alpha = 0.38f)
    OutlinedButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = Color.Transparent,
            contentColor = resolvedColor,
            // AppOutlinedButton has no fill when enabled - its "container color" for this formula
            // is its own brand color, same as its border/text, not literally its (transparent)
            // enabled container.
            disabledContainerColor = resolvedColor.copy(alpha = DisabledContainerAlpha),
        ),
        border = BorderStroke(width = 1.dp, color = borderColor),
    ) {
        leadingIcon?.let {
            it()
            Spacer(modifier = Modifier.width(AppSpacing.Small))
        }
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
    val disabledColor = MaterialTheme.colorScheme.onSurface
    Surface(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier,
        shape = RoundedCornerShape(percent = 50),
        color = if (enabled) containerColor else containerColor.copy(alpha = DisabledContainerAlpha),
        contentColor = if (enabled) contentColor else disabledColor.copy(alpha = DisabledContentAlpha),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min),
        ) {
            // Centered against the button's full width via Box + align, not a weight(1f) Row
            // sharing space with the progress box - a weight(1f) row only centers within
            // whatever's left over, drifting the label right whenever progressText is shown.
            Row(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(horizontal = AppSpacing.Medium, vertical = AppSpacing.Small),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                AppText(text = label, style = AppTextStyle.Label, override = textOverride)
                Icon(imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null)
            }
            if (progressText != null) {
                Row(
                    modifier = Modifier.align(Alignment.CenterStart).fillMaxHeight(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(modifier = Modifier.padding(horizontal = AppSpacing.Medium, vertical = AppSpacing.Small)) {
                        AppText(text = progressText, style = AppTextStyle.Label)
                    }
                    VerticalDivider(modifier = Modifier.fillMaxHeight().padding(vertical = AppSpacing.Small))
                }
            }
        }
    }
}
