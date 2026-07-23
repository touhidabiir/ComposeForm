package com.touhid.composeform.designsystem.components.text

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.touhid.composeform.designsystem.theme.AppSpacing
import com.touhid.composeform.designsystem.theme.ComposeFormTheme

@Preview(name = "Light", showBackground = true)
@Preview(name = "Dark", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun TextPreview() {
    ComposeFormTheme {
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
            }
        }
    }
}

@Preview(name = "Light", showBackground = true)
@Preview(name = "Dark", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun HtmlTextPreview() {
    ComposeFormTheme {
        Surface {
            Column(modifier = Modifier.padding(AppSpacing.Medium)) {
                AppHtmlText(
                    html = "<b>Take a clear photo</b><p>Follow these tips for a good photo:</p>" +
                        "<ul><li>Place the document on a flat surface</li>" +
                        "<li>Shoot in good lighting</li>" +
                        "<li>Keep the whole document inside the frame</li></ul>",
                )
            }
        }
    }
}
