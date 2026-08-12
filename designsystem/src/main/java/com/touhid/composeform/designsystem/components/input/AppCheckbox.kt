package com.touhid.composeform.designsystem.components.input

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.semantics.Role
import com.touhid.composeform.designsystem.components.text.AppText
import com.touhid.composeform.designsystem.components.text.AppTextOverride
import com.touhid.composeform.designsystem.theme.AppSpacing

// Which side of the label the checkbox sits on - same Start/End shape as AppIconPosition (text's
// AppIconLabelValue), just without Top/Bottom since a checkbox+label row has no vertical variant.
enum class AppCheckboxPosition { Start, End }

@Composable
fun AppCheckbox(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    labelOverride: AppTextOverride = AppTextOverride(),
    // Escape hatch for a one-off checked-state accent color that doesn't warrant its own theme
    // token - no-op (falls back to Material3's default) when left unspecified, same convention as
    // AppButton's containerColor/contentColor.
    checkedColor: Color = Color.Unspecified,
    position: AppCheckboxPosition = AppCheckboxPosition.Start,
) {
    val checkboxColors = if (checkedColor.isSpecified) {
        CheckboxDefaults.colors(checkedColor = checkedColor)
    } else {
        CheckboxDefaults.colors()
    }

    Row(
        modifier = modifier.toggleable(
            value = checked,
            enabled = enabled,
            role = Role.Checkbox,
            onValueChange = onCheckedChange,
        ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(AppSpacing.Small),
    ) {
        if (position == AppCheckboxPosition.Start) {
            Checkbox(checked = checked, onCheckedChange = null, enabled = enabled, colors = checkboxColors)
            AppText(text = label, override = labelOverride)
        } else {
            // weight(1f) pushes the checkbox to the row's trailing edge - relies on the caller's
            // own container giving this Row a bounded width to expand into (e.g. via its own
            // fillMaxWidth()), same as any other weighted Row child.
            AppText(text = label, override = labelOverride, modifier = Modifier.weight(1f))
            Checkbox(checked = checked, onCheckedChange = null, enabled = enabled, colors = checkboxColors)
        }
    }
}
