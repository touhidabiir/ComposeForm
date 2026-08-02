package com.touhid.composeform.designsystem.components.surface

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.touhid.composeform.designsystem.theme.AppSpacing

@Composable
fun AppCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    Card(modifier = modifier, shape = RoundedCornerShape(AppSpacing.Medium)) {
        Column(
            modifier = Modifier.padding(AppSpacing.Medium),
            content = content,
        )
    }
}
