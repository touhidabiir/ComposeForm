package com.touhid.composeform.designsystem.theme

import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import com.touhid.composeform.designsystem.R

private val fontProvider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs,
)

val InterFontFamily = FontFamily(Font(googleFont = GoogleFont("Inter"), fontProvider = fontProvider))
val NotoSansBengaliFontFamily = FontFamily(Font(googleFont = GoogleFont("Noto Sans Bengali"), fontProvider = fontProvider))
