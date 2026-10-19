package com.touhid.composeform.designsystem.components.surface

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.touhid.composeform.designsystem.theme.AppSpacing

// [topContent], when set, renders before the padded content column, flush with the card's own
// bounds - e.g. a full-bleed banner whose top corners should land rounded to match the card's,
// rather than sitting inset like the rest of [content]. Compose's padding modifier rejects
// negative values, so this is the supported way to get a "bleeds to the edge" region without
// faking it with offsets; it stays here (not a separate wrapper) since it's still the same Card.
@Composable
fun AppCard(
    modifier: Modifier = Modifier,
    topContent: (@Composable () -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(AppSpacing.Medium),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        topContent?.invoke()
        Column(
            modifier = Modifier.padding(AppSpacing.Medium),
            content = content,
        )
    }
}
