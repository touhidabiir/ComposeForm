package com.touhid.composeform.designsystem.components.text

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow

enum class AppTextStyle { TitleLarge, TitleMedium, BodyLarge, BodyMedium, Label }

@Composable
fun AppText(
    text: String,
    modifier: Modifier = Modifier,
    style: AppTextStyle = AppTextStyle.BodyLarge,
    color: Color = Color.Unspecified,
    override: AppTextOverride = AppTextOverride(),
    maxLines: Int = Int.MAX_VALUE,
    overflow: TextOverflow = TextOverflow.Clip,
    textAlign: TextAlign? = null,
) {
    val baseStyle = when (style) {
        AppTextStyle.TitleLarge -> MaterialTheme.typography.titleLarge
        AppTextStyle.TitleMedium -> MaterialTheme.typography.titleMedium
        AppTextStyle.BodyLarge -> MaterialTheme.typography.bodyLarge
        AppTextStyle.BodyMedium -> MaterialTheme.typography.bodyMedium
        AppTextStyle.Label -> MaterialTheme.typography.labelLarge
    }
    val resolvedStyle = baseStyle.copy(
        fontSize = override.fontSize.orElse(baseStyle.fontSize),
        fontWeight = override.fontWeight ?: baseStyle.fontWeight,
    )
    val resolvedColor = if (override.color.isSpecified) override.color else color
    Text(
        text = text,
        modifier = modifier,
        color = resolvedColor,
        style = resolvedStyle,
        maxLines = maxLines,
        overflow = overflow,
        textAlign = textAlign,
    )
}
