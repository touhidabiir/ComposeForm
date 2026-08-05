package com.touhid.composeform.designsystem.components.text

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.touhid.composeform.designsystem.theme.AppSpacing
import com.touhid.composeform.designsystem.theme.ComposeFormTheme

@Preview(name = "Light", showBackground = true)
@Preview(name = "Dark", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun TextPreview() {
    // dynamicColor = false: pins these to the app's own Light/DarkColorScheme so the preview
    // renders deterministically - dynamic color samples the device wallpaper, which the preview
    // renderer has no real one for, and resolves inconsistently there.
    ComposeFormTheme(dynamicColor = false) {
        Surface {
            Column(
                modifier = Modifier.padding(AppSpacing.Medium),
                verticalArrangement = Arrangement.spacedBy(AppSpacing.Small),
            ) {
                AppText("Title Large", style = AppTextStyle.TitleLarge)
                AppText("Title Medium", style = AppTextStyle.TitleMedium)
                AppText("Body Large", style = AppTextStyle.BodyLarge)
                AppText("Body Medium", style = AppTextStyle.BodyMedium)
                AppText("Label", style = AppTextStyle.Label)
                AppIconLabelValue(
                    value = "01208-567890",
                    icon = { Icon(Icons.Filled.Phone, contentDescription = null, modifier = Modifier.size(16.dp)) },
                )
                AppIconLabelValue(label = "Detailed address", value = "2 No Road, Block-B, Bakalia")
                AppIconLabelValue(
                    label = "Custom label color",
                    value = "2 No Road, Block-B, Bakalia",
                    labelOverride = AppTextOverride(color = Color(0xFFD32F2F)),
                )
                AppIconLabelValue(
                    value = "Icon at the end",
                    icon = { Icon(Icons.Filled.Phone, contentDescription = null, modifier = Modifier.size(16.dp)) },
                    iconPosition = AppIconPosition.End,
                )
                AppIconLabelValue(
                    label = "Icon on top",
                    value = "Stacked above the label/value",
                    icon = { Icon(Icons.Filled.Phone, contentDescription = null, modifier = Modifier.size(16.dp)) },
                    iconPosition = AppIconPosition.Top,
                )
            }
        }
    }
}
