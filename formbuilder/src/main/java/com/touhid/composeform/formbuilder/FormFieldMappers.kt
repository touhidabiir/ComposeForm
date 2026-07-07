package com.touhid.composeform.formbuilder

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.touhid.composeform.designsystem.components.input.AppTextFieldType
import com.touhid.composeform.designsystem.components.text.AppTextOverride
import com.touhid.composeform.formbuilder.schema.FormBorder
import com.touhid.composeform.formbuilder.schema.FormSize
import com.touhid.composeform.formbuilder.schema.FormTextStyle

internal fun FormTextStyle?.toOverride(): AppTextOverride {
    if (this == null) return AppTextOverride()
    return AppTextOverride(
        fontSize = size?.sp ?: TextUnit.Unspecified,
        fontWeight = weight?.toFontWeight(),
        color = color?.let { parseHexColor(it) } ?: Color.Unspecified,
    )
}

private fun String.toFontWeight(): FontWeight? = when (lowercase()) {
    "light" -> FontWeight.Light
    "normal" -> FontWeight.Normal
    "medium" -> FontWeight.Medium
    "semibold" -> FontWeight.SemiBold
    "bold" -> FontWeight.Bold
    else -> null
}

private fun parseHexColor(hex: String): Color? {
    val cleaned = hex.removePrefix("#")
    val colorLong = cleaned.toLongOrNull(16) ?: return null
    return when (cleaned.length) {
        6 -> Color((0xFF000000L or colorLong).toInt())
        8 -> Color(colorLong.toInt())
        else -> null
    }
}

internal fun String.toAppTextFieldType(): AppTextFieldType = when (lowercase()) {
    "number" -> AppTextFieldType.Number
    "email" -> AppTextFieldType.Email
    "password" -> AppTextFieldType.Password
    else -> AppTextFieldType.Text
}

internal fun FormBorder.toModifier(): Modifier {
    if (!enabled) return Modifier
    return Modifier.border(width = width.dp, color = parseHexColor(color) ?: Color.Black)
}

internal fun FormSize.toModifier(): Modifier {
    var modifier: Modifier = Modifier
    modifier = when (width.lowercase()) {
        "match_parent" -> modifier.fillMaxWidth()
        "wrap_content" -> modifier
        else -> width.toIntOrNull()?.let { modifier.width(it.dp) } ?: modifier
    }
    modifier = when (height.lowercase()) {
        "match_parent" -> modifier.fillMaxHeight()
        "wrap_content" -> modifier
        else -> height.toIntOrNull()?.let { modifier.height(it.dp) } ?: modifier
    }
    return modifier
}
