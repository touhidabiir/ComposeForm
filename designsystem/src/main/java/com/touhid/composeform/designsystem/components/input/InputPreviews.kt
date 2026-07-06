package com.touhid.composeform.designsystem.components.input

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
private fun InputPreview() {
    ComposeFormTheme {
        Surface {
            Column(
                modifier = Modifier.padding(AppSpacing.Medium),
                verticalArrangement = Arrangement.spacedBy(AppSpacing.Medium),
            ) {
                AppTextField(
                    value = "Jane Doe",
                    onValueChange = {},
                    label = "Name",
                )
                AppTextField(
                    value = "",
                    onValueChange = {},
                    label = "Email",
                    isError = true,
                    supportingText = "Required",
                )
                AppCheckbox(checked = true, onCheckedChange = {}, label = "Subscribe to updates")
                AppRadioButton(selected = true, onClick = {}, label = "Option A")
                AppRadioButton(selected = false, onClick = {}, label = "Option B")
                AppSwitch(checked = true, onCheckedChange = {}, label = "Enable notifications")
                AppDropdown(
                    options = listOf(
                        AppDropdownOption("One"),
                        AppDropdownOption("Two"),
                        AppDropdownOption("Three"),
                    ),
                    selectedOption = AppDropdownOption("One"),
                    onOptionSelected = {},
                    label = "Choose an option",
                )
            }
        }
    }
}
