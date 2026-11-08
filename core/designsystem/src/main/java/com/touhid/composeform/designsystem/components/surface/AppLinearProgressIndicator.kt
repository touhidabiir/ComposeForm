package com.touhid.composeform.designsystem.components.surface

import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp
import com.touhid.composeform.designsystem.theme.BrandPrimary

private val TrackColor = Color(0xFFD9D9D9)

// A determinate, flat progress bar: brand fill on a grey track, square ends, and none of
// Material3's gap or stop dot - e.g. the OTP screen's countdown. progress is 0..1, read lazily
// so a ticking value only redraws the bar, not its caller.
@Composable
fun AppLinearProgressIndicator(progress: () -> Float, modifier: Modifier = Modifier) {
    LinearProgressIndicator(
        progress = { progress().coerceIn(0f, 1f) },
        modifier = modifier,
        color = BrandPrimary,
        trackColor = TrackColor,
        strokeCap = StrokeCap.Butt,
        gapSize = 0.dp,
        drawStopIndicator = {},
    )
}
