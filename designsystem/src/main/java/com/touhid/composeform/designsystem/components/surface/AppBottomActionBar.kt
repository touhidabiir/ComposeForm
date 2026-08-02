package com.touhid.composeform.designsystem.components.surface

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.touhid.composeform.designsystem.theme.AppSpacing

// A flat, elevated bar meant for a screen's pinned bottom actions (e.g. Approve/Reject) -
// distinct from AppCard, which is a rounded content container rather than a screen-edge bar.
@Composable
fun AppBottomActionBar(
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit,
) {
    Surface(modifier = modifier, tonalElevation = 3.dp, shadowElevation = 3.dp) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(AppSpacing.Medium),
            horizontalArrangement = Arrangement.spacedBy(AppSpacing.Medium),
            content = content,
        )
    }
}
