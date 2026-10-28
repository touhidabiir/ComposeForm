package com.touhid.composeform.designsystem.components.surface

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.window.DialogProperties
import com.touhid.composeform.designsystem.components.button.AppButtonStyle
import com.touhid.composeform.designsystem.components.text.AppText
import com.touhid.composeform.designsystem.components.text.AppTextOverride
import com.touhid.composeform.designsystem.components.text.AppTextStyle
import com.touhid.composeform.designsystem.theme.StatusError
import com.touhid.composeform.designsystem.theme.StatusSuccess

// A plain confirm/alert dialog (title + message + confirm/optional dismiss actions) - distinct
// from AppProgressDialog (spinner-only, no actions) and AppBottomSheet (arbitrary content).
// Wraps Material3's AlertDialog with a narrowed API: plain strings instead of composable slots,
// and AppButtonStyle for the confirm action's tone (e.g. Danger for a destructive "Delete")
// instead of raw ButtonColors.
//
// dismissText is optional: leave it null for an acknowledge-only alert with a single "OK"-style
// action. onDismissRequest fires on back press / outside tap (when dismissible) and is also what
// the dismiss action calls, so callers only have one "closed without confirming" path to handle.
// Showing/hiding is the caller's state (`if (showDialog) AppAlertDialog(...)`), same as Material3.
@Composable
fun AppAlertDialog(
    title: String,
    message: String,
    confirmText: String,
    onConfirm: () -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    dismissText: String? = null,
    confirmButtonType: AppButtonStyle = AppButtonStyle.Primary,
    // false blocks back press / outside tap, e.g. while a decision must be made explicitly.
    dismissible: Boolean = true,
    titleOverride: AppTextOverride = AppTextOverride(),
    messageOverride: AppTextOverride = AppTextOverride(),
) {
    val confirmColor = when (confirmButtonType) {
        AppButtonStyle.Primary -> Color.Unspecified
        AppButtonStyle.Success -> StatusSuccess
        AppButtonStyle.Danger -> StatusError
    }
    val dismissButton: (@Composable () -> Unit)? = dismissText?.let { text ->
        {
            TextButton(onClick = onDismissRequest) {
                AppText(text = text, style = AppTextStyle.Label)
            }
        }
    }
    AlertDialog(
        onDismissRequest = onDismissRequest,
        modifier = modifier,
        properties = DialogProperties(dismissOnBackPress = dismissible, dismissOnClickOutside = dismissible),
        title = { AppText(text = title, style = AppTextStyle.TitleMedium, override = titleOverride) },
        text = { AppText(text = message, style = AppTextStyle.BodyMedium, override = messageOverride) },
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = confirmColor.takeOrElse { ButtonDefaults.textButtonColors().contentColor },
                ),
            ) {
                AppText(text = confirmText, style = AppTextStyle.Label)
            }
        },
        dismissButton = dismissButton,
    )
}
