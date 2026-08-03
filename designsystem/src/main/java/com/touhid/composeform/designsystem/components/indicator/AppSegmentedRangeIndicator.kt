package com.touhid.composeform.designsystem.components.indicator

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

private val DefaultSegmentColors = listOf(
    Color(0xFFEA4335), // red
    Color(0xFFFF8F00), // orange
    Color(0xFFFBBC05), // yellow
    Color(0xFFAEEA00), // yellow-green
    Color(0xFF34A853), // green
)

// A red -> green risk/score scale rendered as discrete blocks rather than a smooth gradient -
// the block [value] (0f..1f) falls into stands taller than its neighbors, standing in for a
// pointer without a separate marker.
@Composable
fun AppSegmentedRangeIndicator(
    value: Float,
    modifier: Modifier = Modifier,
    segmentColors: List<Color> = DefaultSegmentColors,
    baseHeight: Dp = 8.dp,
    activeHeight: Dp = 16.dp,
    gap: Dp = 4.dp,
) {
    val clamped = value.coerceIn(0f, 1f)
    val activeIndex = (clamped * segmentColors.size).toInt().coerceIn(0, segmentColors.size - 1)
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(gap),
        verticalAlignment = Alignment.Bottom,
    ) {
        segmentColors.forEachIndexed { index, color ->
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(if (index == activeIndex) activeHeight else baseHeight)
                    .background(color = color, shape = RoundedCornerShape(percent = 50)),
            )
        }
    }
}
