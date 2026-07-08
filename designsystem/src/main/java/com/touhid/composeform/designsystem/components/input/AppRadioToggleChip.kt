package com.touhid.composeform.designsystem.components.input

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.touhid.composeform.designsystem.components.text.AppText
import com.touhid.composeform.designsystem.components.text.AppTextOverride
import com.touhid.composeform.designsystem.theme.AppSpacing

private val ChipShape = RoundedCornerShape(percent = 50)
private val SelectionCircleSize = 20.dp

@Composable
fun AppRadioToggleChip(
    selected: Boolean,
    onClick: () -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    labelOverride: AppTextOverride = AppTextOverride(),
) {
    Surface(
        modifier = modifier.selectable(
            selected = selected,
            enabled = enabled,
            role = Role.RadioButton,
            onClick = onClick,
        ),
        shape = ChipShape,
        color = Color.Transparent,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = AppSpacing.Medium, vertical = AppSpacing.Small),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            AppText(text = label, override = labelOverride)
            SelectionCircle(selected = selected)
        }
    }
}

@Composable
private fun SelectionCircle(selected: Boolean) {
    val circleModifier = Modifier.size(SelectionCircleSize)
    if (selected) {
        Box(circleModifier.background(color = MaterialTheme.colorScheme.primary, shape = CircleShape))
    } else {
        Box(
            circleModifier.border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant,
                shape = CircleShape,
            ),
        )
    }
}
