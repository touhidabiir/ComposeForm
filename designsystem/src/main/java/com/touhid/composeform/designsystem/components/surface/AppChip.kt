package com.touhid.composeform.designsystem.components.surface

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.touhid.composeform.designsystem.components.text.AppText
import com.touhid.composeform.designsystem.components.text.AppTextStyle
import com.touhid.composeform.designsystem.theme.AppSpacing
import com.touhid.composeform.designsystem.theme.StatusInfo
import com.touhid.composeform.designsystem.theme.StatusInfoContainer

// A selectable pill - filled/accented when selected, neutral otherwise - for filter tabs and
// similar choice selections. Distinct from AppStatusBadge: that one always shows a status dot
// and communicates state (approved/pending/etc.), this one has no dot and communicates
// selection (this is the currently active choice among several). Text/icon color stays the same
// brand pink whether selected or not - only the pill's fill changes.
@Composable
fun AppChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val containerColor = if (selected) StatusInfoContainer else Color.Transparent
    Surface(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(percent = 50),
        color = containerColor,
        contentColor = StatusInfo,
    ) {
        Box(modifier = Modifier.padding(horizontal = AppSpacing.Medium, vertical = AppSpacing.Small)) {
            AppText(text = text, style = AppTextStyle.Label, color = StatusInfo)
        }
    }
}
