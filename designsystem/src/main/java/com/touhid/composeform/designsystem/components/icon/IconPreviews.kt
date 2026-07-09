package com.touhid.composeform.designsystem.components.icon

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.touhid.composeform.designsystem.theme.AppSpacing
import com.touhid.composeform.designsystem.theme.ComposeFormTheme

@Preview(name = "Light", showBackground = true)
@Preview(name = "Dark", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun IconButtonPreview() {
    ComposeFormTheme {
        Surface {
            Row(
                modifier = Modifier.padding(AppSpacing.Medium),
                horizontalArrangement = Arrangement.spacedBy(AppSpacing.Small),
            ) {
                AppIconButton(icon = Icons.Filled.Menu, contentDescription = "Menu", onClick = {})
                AppIconButton(icon = Icons.Filled.Settings, contentDescription = "Settings", onClick = {}, enabled = false)
            }
        }
    }
}
