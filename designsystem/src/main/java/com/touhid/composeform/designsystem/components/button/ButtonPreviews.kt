package com.touhid.composeform.designsystem.components.button

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.touhid.composeform.designsystem.theme.AppSpacing
import com.touhid.composeform.designsystem.theme.ComposeFormTheme

@Preview(name = "Light", showBackground = true)
@Preview(name = "Dark", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun ButtonPreview() {
    ComposeFormTheme {
        Surface {
            Column(
                modifier = Modifier.padding(AppSpacing.Medium),
                verticalArrangement = Arrangement.spacedBy(AppSpacing.Small),
            ) {
                AppButton(text = "Primary Action", onClick = {})
                AppButton(text = "Approve", onClick = {}, tone = AppButtonTone.Success)
                AppOutlinedButton(text = "Secondary Action", onClick = {})
                AppOutlinedButton(text = "Reject", onClick = {}, tone = AppButtonTone.Danger)
                AppButton(text = "Custom dark brand color", onClick = {}, containerColor = Color(0xFF1A237E))
                AppButton(
                    text = "Custom light brand color",
                    onClick = {},
                    containerColor = Color(0xFFFFE082),
                    contentColor = Color(0xFF1A237E),
                )
                AppStepperButton(label = "Next step", onClick = {}, progressText = "১/১০")
                AppStepperButton(label = "Next step", onClick = {})
            }
        }
    }
}
