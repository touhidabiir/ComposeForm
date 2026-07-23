package com.touhid.composeform.designsystem.components.button

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
private fun ButtonPreview() {
    ComposeFormTheme {
        Surface {
            Column(
                modifier = Modifier.padding(AppSpacing.Medium),
                verticalArrangement = Arrangement.spacedBy(AppSpacing.Small),
            ) {
                AppButton(text = "Primary Action", onClick = {})
                AppOutlinedButton(text = "Secondary Action", onClick = {})
                AppStepperButton(label = "Next step", onClick = {}, progressText = "১/১০")
                AppStepperButton(label = "Next step", onClick = {})
                AppShutterButton(onClick = {})
            }
        }
    }
}
