package com.touhid.composeform.designsystem.components.input

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.touhid.composeform.designsystem.components.text.AppText
import com.touhid.composeform.designsystem.components.text.AppTextOverride
import com.touhid.composeform.designsystem.theme.AppSpacing

private val IndicatorSize = 20.dp

@Composable
fun AppRadioCheckCircle(
    selected: Boolean,
    onClick: () -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    labelOverride: AppTextOverride = AppTextOverride(),
) {
    Row(
        modifier = modifier.selectable(
            selected = selected,
            enabled = enabled,
            role = Role.RadioButton,
            onClick = onClick,
        ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(AppSpacing.Small),
    ) {
        CheckCircleIndicator(selected = selected)
        AppText(text = label, override = labelOverride)
    }
}

@Composable
private fun CheckCircleIndicator(selected: Boolean) {
    val primary = MaterialTheme.colorScheme.primary
    val onPrimary = MaterialTheme.colorScheme.onPrimary
    val outline = MaterialTheme.colorScheme.outlineVariant
    Canvas(modifier = Modifier.size(IndicatorSize)) {
        if (selected) {
            drawCircle(color = primary)
            val checkPath = Path().apply {
                moveTo(size.width * 0.28f, size.height * 0.52f)
                lineTo(size.width * 0.44f, size.height * 0.68f)
                lineTo(size.width * 0.74f, size.height * 0.34f)
            }
            drawPath(
                path = checkPath,
                color = onPrimary,
                style = Stroke(width = size.width * 0.12f, cap = StrokeCap.Round, join = StrokeJoin.Round),
            )
        } else {
            drawCircle(
                color = outline,
                style = Stroke(width = size.width * 0.08f),
                radius = (size.minDimension / 2f) - (size.width * 0.04f),
                center = Offset(size.width / 2f, size.height / 2f),
            )
        }
    }
}
