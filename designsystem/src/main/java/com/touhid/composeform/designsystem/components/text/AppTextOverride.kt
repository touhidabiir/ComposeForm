package com.touhid.composeform.designsystem.components.text

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.isUnspecified

data class AppTextOverride(
    val fontSize: TextUnit = TextUnit.Unspecified,
    val fontWeight: FontWeight? = null,
    val color: Color = Color.Unspecified,
)

internal fun TextUnit.orElse(fallback: TextUnit): TextUnit = if (isUnspecified) fallback else this
