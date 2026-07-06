package com.example.composeform.designsystem.components.input

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.RadioButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import com.example.composeform.designsystem.components.text.AppText
import com.example.composeform.designsystem.theme.AppSpacing

@Composable
fun AppRadioButton(
    selected: Boolean,
    onClick: () -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    Row(
        modifier = modifier.selectable(
            selected = selected,
            enabled = enabled,
            onClick = onClick,
        ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(AppSpacing.Small),
    ) {
        RadioButton(selected = selected, onClick = null, enabled = enabled)
        AppText(text = label)
    }
}
