package com.touhid.composeform.designsystem.theme

import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import com.touhid.composeform.designsystem.R

// Inter and Noto Sans Bengali are bundled as variable fonts (res/font/), each with a "wght" axis —
// one Font() entry per weight maps that axis to a FontWeight, since Compose won't otherwise know
// which instance of a variable font to pick for a requested weight (licensed under OFL 1.1,
// see designsystem/licenses/).
private fun variableFontFamily(resId: Int): FontFamily = FontFamily(
    Font(resId, weight = FontWeight.Light, variationSettings = FontVariation.Settings(FontVariation.weight(300))),
    Font(resId, weight = FontWeight.Normal, variationSettings = FontVariation.Settings(FontVariation.weight(400))),
    Font(resId, weight = FontWeight.Medium, variationSettings = FontVariation.Settings(FontVariation.weight(500))),
    Font(resId, weight = FontWeight.SemiBold, variationSettings = FontVariation.Settings(FontVariation.weight(600))),
    Font(resId, weight = FontWeight.Bold, variationSettings = FontVariation.Settings(FontVariation.weight(700))),
)

val InterFontFamily = variableFontFamily(R.font.inter_variable)
val NotoSansBengaliFontFamily = variableFontFamily(R.font.noto_sans_bengali_variable)
