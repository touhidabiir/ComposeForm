package com.touhid.composeform.designsystem.components.indicator

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.touhid.composeform.designsystem.components.text.AppText
import com.touhid.composeform.designsystem.components.text.AppTextOverride
import com.touhid.composeform.designsystem.components.text.AppTextStyle

// A circle outlined with [color] (not filled) with the score centered on it. [color] is a fixed
// badge tone (e.g. picked by the caller from the score's tier/severity) - not a proportional
// fill/sweep of score/maxScore, since the design this backs doesn't visualize the score as an
// arc at all.
@Composable
fun AppScoreBadge(
    score: Int,
    maxScore: Int,
    modifier: Modifier = Modifier,
    diameter: Dp = 120.dp,
    borderWidth: Dp = 6.dp,
    color: Color = MaterialTheme.colorScheme.primary,
    contentColor: Color = color,
) {
    Box(
        modifier = modifier
            .size(diameter)
            .border(border = BorderStroke(width = borderWidth, color = color), shape = CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        AppText(
            text = "$score/$maxScore",
            style = AppTextStyle.TitleLarge,
            override = AppTextOverride(color = contentColor),
        )
    }
}
