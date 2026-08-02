package com.touhid.composeform.designsystem.components.text

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.touhid.composeform.designsystem.theme.AppSpacing

// A value on its own (icon + value) when [label] is null, or a caption stacked above the value
// when it's set - covers both the compact rows in a list card and the label/value pairs in a
// detail screen with one component, rather than two near-identical ones.
@Composable
fun AppLabeledValue(
    value: String,
    modifier: Modifier = Modifier,
    label: String? = null,
    icon: (@Composable () -> Unit)? = null,
    valueOverride: AppTextOverride = AppTextOverride(),
) {
    Row(
        modifier = modifier,
        verticalAlignment = if (label != null) Alignment.Top else Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(AppSpacing.Small),
    ) {
        icon?.invoke()
        Column {
            label?.let {
                AppText(text = it, style = AppTextStyle.Label, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            AppText(text = value, style = AppTextStyle.BodyMedium, override = valueOverride)
        }
    }
}
