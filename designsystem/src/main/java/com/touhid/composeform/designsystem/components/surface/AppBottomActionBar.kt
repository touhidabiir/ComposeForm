package com.touhid.composeform.designsystem.components.surface

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.touhid.composeform.designsystem.theme.AppSpacing

// A flat, elevated bar meant for a screen's pinned bottom actions (e.g. Approve/Reject) -
// distinct from AppCard, which is a rounded content container rather than a screen-edge bar.
// color/tonalElevation are set explicitly rather than left at Surface's defaults - a nonzero
// tonalElevation blends the primary color's tint into the surface, which would otherwise leave
// this pink-tinted instead of matching the screen's actual white bar. [cornerRadius] defaults to
// 0 (a flat screen-edge bar) - callers that want rounded top corners (e.g. a bottom-sheet-style
// bar) pass one explicitly, e.g. AppSpacing.Medium, rather than this component always rounding.
@Composable
fun AppBottomActionBar(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 0.dp,
    content: @Composable RowScope.() -> Unit,
) {
    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(topStart = cornerRadius, topEnd = cornerRadius),
        tonalElevation = 0.dp,
        shadowElevation = 3.dp,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(AppSpacing.Medium),
            horizontalArrangement = Arrangement.spacedBy(AppSpacing.Medium),
            content = content,
        )
    }
}
