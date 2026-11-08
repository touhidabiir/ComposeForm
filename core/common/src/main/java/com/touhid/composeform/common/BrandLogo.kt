package com.touhid.composeform.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.touhid.composeform.designsystem.components.text.AppText
import com.touhid.composeform.designsystem.components.text.AppTextOverride
import com.touhid.composeform.designsystem.theme.AppSpacing
import com.touhid.composeform.designsystem.theme.BrandPrimary

// The app's logo: mark + wordmark + product tag pill. Shared by :app's splash and the sign-in
// screens. onBrandBackground draws it white for the brand-pink splash, otherwise brand pink.
// Everything here is a placeholder until the real logo assets exist.
@Composable
fun BrandLogo(modifier: Modifier = Modifier, onBrandBackground: Boolean = false) {
    val contentColor = if (onBrandBackground) Color.White else BrandPrimary
    val tagContainerColor = if (onBrandBackground) Color.White.copy(alpha = 0.15f) else BrandPrimary.copy(alpha = 0.08f)
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        Image(
            painter = painterResource(R.drawable.common_brand_mark),
            contentDescription = null,
            colorFilter = ColorFilter.tint(contentColor),
            modifier = Modifier.size(56.dp),
        )
        Column(
            modifier = Modifier.padding(start = AppSpacing.Small),
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(AppSpacing.ExtraSmall),
        ) {
            AppText(
                text = stringResource(R.string.common_brand_name),
                override = AppTextOverride(fontSize = 34.sp, fontWeight = FontWeight.Normal, color = contentColor),
            )
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(percent = 50))
                    .background(tagContainerColor)
                    .padding(horizontal = AppSpacing.Large, vertical = 2.dp),
            ) {
                AppText(
                    text = stringResource(R.string.common_brand_tag),
                    override = AppTextOverride(fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = contentColor),
                )
            }
        }
    }
}
