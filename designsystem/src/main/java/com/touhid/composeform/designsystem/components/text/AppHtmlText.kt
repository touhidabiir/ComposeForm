package com.touhid.composeform.designsystem.components.text

import android.graphics.Typeface
import android.text.Spanned
import android.text.style.BulletSpan
import android.text.style.CharacterStyle
import android.text.style.StyleSpan
import android.text.style.UnderlineSpan
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.core.text.HtmlCompat

/**
 * Renders a minimal HTML subset (`<b>`/`<strong>`, `<i>`/`<em>`, `<u>`, `<p>`, `<ul><li>`) as Compose
 * text. Bullets are approximated with a literal "•  " prefix per line rather than a true hanging
 * indent, since [AnnotatedString] has no bullet/leading-margin primitive - close enough for the
 * instructional copy this is built for.
 */
@Composable
fun AppHtmlText(
    html: String,
    modifier: Modifier = Modifier,
    style: AppTextStyle = AppTextStyle.BodyLarge,
    color: Color = Color.Unspecified,
    override: AppTextOverride = AppTextOverride(),
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
    val annotated = remember(html) { html.toAnnotatedString() }
    Text(
        text = annotated,
        modifier = modifier,
        color = resolvedColor,
        style = resolvedStyle,
    )
}

private fun String.toAnnotatedString(): AnnotatedString {
    val spanned = HtmlCompat.fromHtml(this, HtmlCompat.FROM_HTML_MODE_COMPACT)
    return spanned.toAnnotatedString()
}

private fun Spanned.toAnnotatedString(): AnnotatedString {
    val length = length
    val text = toString()
    val bulletLineStarts = getSpans(0, length, BulletSpan::class.java).map { getSpanStart(it) }.toSet()

    // Prefixing bullet lines with a literal glyph shifts character indices, so track how much
    // each original index has moved before translating character-style span ranges below.
    val builder = StringBuilder()
    val insertOffsets = IntArray(length + 1)
    var runningOffset = 0
    for (i in 0 until length) {
        if (i in bulletLineStarts) {
            builder.append("•  ")
            runningOffset += 3
        }
        insertOffsets[i] = runningOffset
        builder.append(text[i])
    }
    insertOffsets[length] = runningOffset

    return buildAnnotatedString {
        append(builder.toString())
        for (span in getSpans(0, length, CharacterStyle::class.java)) {
            val newStart = getSpanStart(span) + insertOffsets[getSpanStart(span)]
            val newEnd = getSpanEnd(span) + insertOffsets[getSpanEnd(span)]
            val spanStyle = when (span) {
                is StyleSpan -> when (span.style) {
                    Typeface.BOLD -> SpanStyle(fontWeight = FontWeight.Bold)
                    Typeface.ITALIC -> SpanStyle(fontStyle = FontStyle.Italic)
                    Typeface.BOLD_ITALIC -> SpanStyle(fontWeight = FontWeight.Bold, fontStyle = FontStyle.Italic)
                    else -> null
                }
                is UnderlineSpan -> SpanStyle(textDecoration = TextDecoration.Underline)
                else -> null
            }
            spanStyle?.let { addStyle(it, newStart, newEnd) }
        }
    }
}
