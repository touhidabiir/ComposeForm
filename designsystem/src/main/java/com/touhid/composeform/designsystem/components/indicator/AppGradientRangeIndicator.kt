package com.touhid.composeform.designsystem.components.indicator

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

private val DefaultRangeColors = listOf(Color(0xFFEA4335), Color(0xFFFBBC05), Color(0xFF34A853))

// A red -> yellow -> green scale with a pointer marking [value] (0f..1f) along it,
// used to visualize a risk/score position without repeating the score's own number.
@Composable
fun AppGradientRangeIndicator(
    value: Float,
    modifier: Modifier = Modifier,
    trackHeight: Dp = 8.dp,
    colors: List<Color> = DefaultRangeColors,
) {
    val clamped = value.coerceIn(0f, 1f)
    Canvas(modifier = modifier.fillMaxWidth().height(trackHeight * 2)) {
        val barHeight = trackHeight.toPx()
        val barTop = (size.height - barHeight) / 2f
        drawRoundRect(
            brush = Brush.horizontalGradient(colors),
            topLeft = Offset(0f, barTop),
            size = Size(size.width, barHeight),
            cornerRadius = CornerRadius(barHeight / 2f, barHeight / 2f),
        )
        val markerRadius = barHeight
        val pointerX = (size.width * clamped).coerceIn(markerRadius, size.width - markerRadius)
        val center = Offset(pointerX, barTop + barHeight / 2f)
        drawCircle(color = Color.White, radius = markerRadius, center = center)
        drawCircle(
            color = Color.Black.copy(alpha = 0.45f),
            radius = markerRadius,
            center = center,
            style = Stroke(width = 1.5.dp.toPx()),
        )
    }
}
